package dev.zanth.teleport;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.zanth.teleport.mixin.TeleportCommandMixin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.Set;

// NOTE: keep this file name in sync with the class name; the "Sever" spelling is
// historical. Rename file + class together (IDEA: Shift+F6 on the class name).
@EventBusSubscriber
public class SeverChatListener {
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        if (!event.getRawText().equals("tp")) {
            return;
        }

        Player player = event.getPlayer();
        ServerLevel level = (ServerLevel) player.level();
        CommandSourceStack source = player.createCommandSourceStack();

        try {
            TeleportCommandMixin.callPerformTeleport(
                source,
                player,
                level,
                0.0,
                100.0,
                0.0,
                Set.of(),
                player.getYRot(), player.getXRot(),
                null
            );
            event.setCanceled(true);
        } catch (CommandSyntaxException e) {
            JiTP.LOGGER.error("tp failed", e);
        }
    }
}
