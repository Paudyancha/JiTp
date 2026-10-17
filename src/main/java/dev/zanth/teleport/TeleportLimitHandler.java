package dev.zanth.teleport;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Lets players without permission use {@code /tp} / {@code /teleport}, but only the two
 * "bare" forms:
 *
 * <pre>/tp &lt;location&gt;   (self -&gt; a position)
 * /tp &lt;entity&gt;     (self -&gt; an entity, selectors like @e[type=pig] included)</pre>
 *
 * Everything else is silently dropped.
 *
 * <p>How it works (borrowing the idea from the neighbouring randomly_tp mod): the event
 * handler cancels the player's own run and re-runs the very same command text through a
 * copy of the source with permission level 2 ({@link CommandSourceStack#withPermission}).
 * The vanilla command therefore executes with full rights on the vanilla code path -
 * which also covers the checks that look at permission <em>while parsing</em>, e.g.
 * {@code EntitySelectorParser.allowSelectors} refuses {@code @e}/{@code @a} for a
 * permission-less player before execution is ever reached.</p>
 *
 * <p>{@code withPermission} keeps the player as the message target, so vanilla's own
 * success/failure feedback still reaches them (unlike a {@code CommandSource.NULL}
 * stack). Sources that already have permission (operators, command blocks, functions,
 * the console) are never touched.</p>
 *
 * <p>NeoForge fires {@link CommandEvent} in {@code Commands#performCommand}, after the
 * brigadier parse but before {@code finishParsing}/execution, and cancelling it without
 * setting an exception returns silently - no message is sent for rejected shapes.</p>
 */
@EventBusSubscriber
public final class TeleportLimitHandler {

    /** What vanilla gates /tp and entity selectors behind. */
    private static final int JITP_RUN_LEVEL = 2;

    private TeleportLimitHandler() {
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        ParseResults<CommandSourceStack> parse = event.getParseResults();
        CommandSourceStack source = parse.getContext().getSource();

        // Operators, command blocks, functions and the console: completely untouched.
        // This also stops the re-run below from being handled again.
        if (source.hasPermission(JITP_RUN_LEVEL)) {
            return;
        }

        // Judge by the raw command text (no leading slash at this point), so a nested
        // "/execute run tp ..." can never be mistaken for a bare teleport - and a
        // permission-less player cannot reach /execute anyway.
        String raw = parse.getReader().getString().trim();
        if (!raw.startsWith("tp ") && !raw.startsWith("teleport ")) {
            return;
        }

        MinecraftServer server = source.getServer();
        CommandSourceStack privileged = source.withPermission(JITP_RUN_LEVEL);

        // Re-parse with the privileged source: this is what makes selectors work, since
        // EntityArgument checks hasPermission(2) while parsing and the player's own
        // parse would have died right there with "selectors not allowed".
        ParseResults<CommandSourceStack> privilegedParse =
            server.getCommands().getDispatcher().parse(raw, privileged);

        // The player's own run is never executed: either the shape is allowed and the
        // privileged re-run does the work, or it is not allowed and nothing happens.
        event.setCanceled(true);

        if (jitp$isBareTeleport(privilegedParse)) {
            server.getCommands().performPrefixedCommand(privileged, raw);
        }
    }

    /** True for exactly {@code /tp <location>} and {@code /tp <entity>}. */
    private static boolean jitp$isBareTeleport(ParseResults<CommandSourceStack> parse) {
        Set<String> arguments = new LinkedHashSet<>();
        for (CommandContextBuilder<CommandSourceStack> context = parse.getContext();
             context != null;
             context = context.getChild()) {
            arguments.addAll(context.getArguments().keySet());
        }
        return arguments.size() == 1
            && (arguments.contains("location") || arguments.contains("destination"));
    }
}
