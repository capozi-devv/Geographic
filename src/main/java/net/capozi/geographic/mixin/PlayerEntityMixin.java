package net.capozi.geographic.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "getMovementSpeed", at = @At("HEAD"))
    private void onMove(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        BlockPos posBelow = player.getBlockPos().down();
        BlockState stateBelow = player.getWorld().getBlockState(posBelow);
        Block blockBelow = stateBelow.getBlock();
        if (blockBelow.equals(Blocks.DIRT_PATH)) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .setBaseValue(0.15);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_EFFICIENCY)
                    .setBaseValue(0.175);
        }
    }
}
