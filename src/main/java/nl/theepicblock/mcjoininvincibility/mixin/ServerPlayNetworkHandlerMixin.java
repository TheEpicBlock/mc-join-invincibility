package nl.theepicblock.mcjoininvincibility.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import nl.theepicblock.mcjoininvincibility.ServerPlayerEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;"))
    public void onMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (((ServerPlayerEntityAccessor)player).getInvincibleTicks() > 60) {
            Vec3d playerPos = player.getPos();
            if (playerPos.x != packet.getX(playerPos.x) && playerPos.y != packet.getY(playerPos.y) && playerPos.z != packet.getZ(playerPos.z)) {
                ((ServerPlayerEntityAccessor)player).disableInvincibility();
                player.sendMessage(new LiteralText("You're no longer invincible due to moving"), false);
            }
        }
    }

    @Inject(method = "onPlayerInput", at = @At("RETURN"))
    public void onInput(PlayerInputC2SPacket packet, CallbackInfo ci) {
        if (((ServerPlayerEntityAccessor)player).getInvincibleTicks() > 60 &&
                (packet.isJumping() || packet.isSneaking() || packet.getForward() != 0 || packet.getSideways() != 0)) {
            ((ServerPlayerEntityAccessor)player).disableInvincibility();
            player.sendMessage(new LiteralText("You're no longer invincible due to moving"), false);
        }
    }

    @Inject(method = {"onPlayerAction", "onPlayerInteractBlock", "onPlayerInteractItem", "onPlayerInteractEntity"}, at = @At("RETURN"))
    public void onAction(CallbackInfo ci) {
        if (((ServerPlayerEntityAccessor)player).getInvincibleTicks() > 60) {
            ((ServerPlayerEntityAccessor)player).disableInvincibility();
            player.sendMessage(new LiteralText("You're no longer invincible due to activity"), false);
        }
    }
}
