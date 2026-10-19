package dev.zanth.teleport.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TeleportCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(TeleportCommand.class)
public abstract class TeleportCommandPermissionMixin {

    @Redirect(
        method = "register",
        at = @At(
            value = "INVOKE",
            ordinal = 0,
            target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
        )
    )
    private static ArgumentBuilder<CommandSourceStack, ?> jitp$allowTeleport(
            LiteralArgumentBuilder<CommandSourceStack> builder,
            Predicate<CommandSourceStack> requirement) {
        return builder.requires(source -> true);
    }

    @Redirect(
        method = "register",
        at = @At(
            value = "INVOKE",
            ordinal = 1,
            target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;requires(Ljava/util/function/Predicate;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
        )
    )
    private static ArgumentBuilder<CommandSourceStack, ?> jitp$allowTp(
            LiteralArgumentBuilder<CommandSourceStack> builder,
            Predicate<CommandSourceStack> requirement) {
        return builder.requires(source -> true);
    }
}
