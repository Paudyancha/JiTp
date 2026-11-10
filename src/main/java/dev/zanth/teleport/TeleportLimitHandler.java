package dev.zanth.teleport;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.zanth.teleport.data.PlayerTPData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;

import java.util.LinkedHashSet;
import java.util.Set;

@EventBusSubscriber(modid = JiTP.MOD_ID)
public final class TeleportLimitHandler {

    private static final int JITP_RUN_LEVEL = 2;

    private TeleportLimitHandler() {
    }

    @SubscribeEvent
    public static void onCommand(CommandEvent event) throws CommandSyntaxException {
        if (!TeleportConfig.isTeleportAllowed()) {
            return;
        }
        ParseResults<CommandSourceStack> parse = event.getParseResults();
        CommandSourceStack source = parse.getContext().getSource();

        if (source.hasPermission(JITP_RUN_LEVEL)) {
            return;
        }

        // 若不具备权限则禁止使用
        if (source.isPlayer()) {
            ServerPlayer player = source.getPlayerOrException();
            ServerLevel overworld = player.server.overworld();
            PlayerTPData data = overworld.getDataStorage().computeIfAbsent(PlayerTPData.FACTORY, "player_tp_data");
            if (!data.getFlag(player.getUUID())) {
                source.sendFailure(Component.literal("You do not have permission to use this transmission command!"));
                return;
            }
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
