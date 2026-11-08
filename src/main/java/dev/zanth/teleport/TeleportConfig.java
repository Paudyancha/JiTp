package dev.zanth.teleport;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 模组配置。保存无权限玩家能否使用 {@code /tp} 与 {@code /teleport} 的开关，默认允许。
 * 采用 SERVER 类型配置，使每个存档或服务器可以独立调整该行为。
 */
public final class TeleportConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ALLOW_TELEPORT_COMMAND;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ALLOW_TELEPORT_COMMAND = builder
            .comment("Allow players without permission level 2 to use /tp and /teleport. Changes apply immediately.")
            .define("allowTeleportCommand", true);
        SPEC = builder.build();
        JiTP.LOGGER.info("Transmission permission {} opened", TeleportConfig.isTeleportAllowed() ? "has been" : "not");
    }

    private TeleportConfig() {
    }

    /**
     * 判断当前是否允许无权限玩家使用传送命令。
     * 每次命令解析都会读取配置，配置文件变更由 NeoForge 的文件监听重载，不需要重启服务器。
     * 配置尚未加载时返回允许，避免服务器启动阶段解析命令时读取配置抛出异常。
     *
     * @return 配置允许或配置未加载时返回 {@code true}
     */
    public static boolean isTeleportAllowed() {
        return !SPEC.isLoaded() || ALLOW_TELEPORT_COMMAND.get();
    }
}