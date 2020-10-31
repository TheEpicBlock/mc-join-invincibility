package nl.theepicblock.mcjoininvincibility.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import nl.theepicblock.mcjoininvincibility.ServerPlayerEntityAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ServerPlayerEntityAccessor {
    @Shadow private int joinInvulnerabilityTicks;
    @Shadow public abstract void setGameMode(GameMode gameMode);

    @Shadow public ServerPlayNetworkHandler networkHandler;
    @Shadow @Final public ServerPlayerInteractionManager interactionManager;

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        this.joinInvulnerabilityTicks = 20*60+1; //one minute
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (joinInvulnerabilityTicks == 1) {
            disableInvincibility();
            this.sendMessage(new LiteralText("your invincibility has worn off"), false);
        }
    }

    @Override
    public void disableInvincibility() {
        joinInvulnerabilityTicks = 0;
    }

    @Override
    public int getInvincibleTicks() {
        return joinInvulnerabilityTicks;
    }
}
