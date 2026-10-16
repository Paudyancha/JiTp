package dev.zanth.teleport;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(JiTP.MOD_ID)
public class JiTP {
    public static final String MOD_ID = "jitp";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unused")
    public JiTP(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SuppressWarnings("unused")
    private void commonSetup(FMLCommonSetupEvent event) {
    }
}
