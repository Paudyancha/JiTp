package dev.zanth.teleport;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(JiTP.MODID)
public class JiTP {
    public static final String MODID = "jitp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JiTP(IEventBus modEventBus, ModContainer modContainer) {
    }
}
