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

@EventBusSubscriber
public final class TeleportLimitHandler {

    private static final int JITP_RUN_LEVEL = 2;

    private TeleportLimitHandler() {
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        ParseResults<CommandSourceStack> parse = event.getParseResults();
        CommandSourceStack source = parse.getContext().getSource();

        if (source.hasPermission(JITP_RUN_LEVEL)) {
            return;
        }
        String raw = parse.getReader().getString().trim();
        if (!raw.startsWith("tp ") && !raw.startsWith("teleport ")) {
            return;
        }

        MinecraftServer server = source.getServer();
        CommandSourceStack privileged = source.withPermission(JITP_RUN_LEVEL);

        ParseResults<CommandSourceStack> privilegedParse =
            server.getCommands().getDispatcher().parse(raw, privileged);
        event.setCanceled(true);

        if (jitp$isBareTeleport(privilegedParse)) {
            server.getCommands().performPrefixedCommand(privileged, raw);
        }
    }

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
