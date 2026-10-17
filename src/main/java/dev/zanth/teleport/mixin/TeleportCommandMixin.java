package dev.zanth.teleport.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(TeleportCommand.class)
public interface TeleportCommandMixin {
    // The accessor name must differ from the target method name/signature:
    // Mixin merges this method into TeleportCommand, where two identical
    // members cannot coexist. "call" prefix is the Mixin convention.
    @Invoker("performTeleport")
    static void callPerformTeleport(
        CommandSourceStack source,
        Entity entity,
        ServerLevel level,
        double x,
        double y,
        double z,
        Set<RelativeMovement> relativeList,
        float yaw, float pitch,
        TeleportCommand.LookAt facing
    ) throws CommandSyntaxException {

    }
}
