package net.capozi.geographic.common.block;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class RopeLadderBlock extends LadderBlock {
    public RopeLadderBlock(Settings settings) {
        super(settings);
    }
    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockState(pos.down()) == Blocks.AIR.getDefaultState()) {
            world.setBlockState(pos.down(), this.getDefaultState());
        }
    }
}
