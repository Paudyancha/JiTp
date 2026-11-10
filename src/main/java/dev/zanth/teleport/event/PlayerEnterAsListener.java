package dev.zanth.teleport.event;

import dev.zanth.teleport.JiTP;
import dev.zanth.teleport.data.PlayerTPData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

@EventBusSubscriber(modid = JiTP.MOD_ID)
public class PlayerEnterAsListener {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel overworld = player.server.overworld();
        PlayerTPData data = overworld.getDataStorage().computeIfAbsent(PlayerTPData.FACTORY, "player_tp_data");
        UUID uuid = player.getUUID();
        if (!data.hasFlag(uuid)) {
            data.setFlag(uuid, true);
            JiTP.LOGGER.info("{} logged with JiTP", player.getName());
        }
    }
}
