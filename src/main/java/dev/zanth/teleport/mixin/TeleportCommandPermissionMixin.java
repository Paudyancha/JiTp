package dev.zanth.teleport.mixin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.TeleportCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

/**
 * Removes the {@code requires(source -> source.hasPermission(2))} condition from the
 * vanilla {@code /teleport} and {@code /tp} nodes while they are registered, so the
 * vanilla command works without permission.
 *
 * <p>The command tree itself is not touched: vanilla still builds it, and because the
 * requirement now passes, {@code Commands#fillUsableCommands} ships it to every client
 * - which is what makes the client believe it may use /tp. Execution also stays on the
 * vanilla path; we only neutralise the predicate (both literal nodes carry it, hence
 * the two redirects).</p>
 */
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
