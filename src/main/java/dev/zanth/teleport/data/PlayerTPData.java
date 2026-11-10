package dev.zanth.teleport.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.zanth.teleport.JiTP;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 保存单个玩家是否允许使用 {@code /tp } 与 {@code /teleport } 的qx
 */
public class PlayerTPData extends SavedData {
    private static final Codec<Map<UUID, Boolean>> DATA_CODEC =
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.BOOL);

    private final Map<UUID, Boolean> playerFlags = new HashMap<>();

    public PlayerTPData() {}

    public PlayerTPData(Map<UUID, Boolean> flags) {
        this.playerFlags.putAll(flags);
    }

    public boolean hasFlag(UUID uuid) {
        return this.playerFlags.containsKey(uuid);
    }

    public boolean getFlag(UUID uuid) {
        return this.playerFlags.getOrDefault(uuid, true);
    }

    public void setFlag(UUID uuid, boolean flag) {
        this.playerFlags.put(uuid, flag);
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // 使用 NbtOps 将 Map 编码为 Tag，存入 "PlayerFlags" 键下
        DataResult<Tag> result = DATA_CODEC.encodeStart(NbtOps.INSTANCE, this.playerFlags);

        // 如果编码成功，放入 tag 中；如果失败，打印错误并放一个空的 CompoundTag 兜底
        result.resultOrPartial(error -> JiTP.LOGGER.error("Failed to save PlayerTPData: {}", error))
            .ifPresentOrElse(
                encodedTag -> tag.put("PlayerFlags", encodedTag),
                () -> tag.put("PlayerFlags", new CompoundTag()));
        return tag;
    }

    public static final SavedData.Factory<PlayerTPData> FACTORY = new SavedData.Factory<>(
        PlayerTPData::new, // 不存在则new
        (tag, registries) -> {
            // 从 tag 中获取，解析回 Map
            Tag flagsTag = tag.get("PlayerFlags");
            if (flagsTag != null) {
                Map<UUID, Boolean> parsedMap = DATA_CODEC.parse(NbtOps.INSTANCE, flagsTag)
                    .resultOrPartial(error -> JiTP.LOGGER.error("Failed to load PlayerTPData: {}", error))
                    .orElse(new HashMap<>());
                return new PlayerTPData(parsedMap);
            }
            return new PlayerTPData();
        },
        null
    );
}
