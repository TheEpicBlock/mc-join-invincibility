package nl.theepicblock.mcjoininvincibility;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface ServerPlayerEntityAccessor {
    void disableInvincibility();

    int getInvincibleTicks();
}
