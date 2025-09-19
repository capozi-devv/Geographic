package net.capozi.geographic;

import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GeographicClient implements ClientModInitializer {
    public static World getClientWorld() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        return world;
    }
    @Override public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(ItemInit.CALIBRATED_COMPASS, Identifier.of(Geographic.MOD_ID, "angle"), new ClampedModelPredicateProvider() {
            private double rotation;
            private double rota;
            private long lastUpdateTick;
            @Override
            public float unclampedCall(ItemStack stack, ClientWorld world, LivingEntity entityLiving, int seed) {
                if (entityLiving == null && !stack.isInFrame()) {
                    return 0.0F;
                } else {
                    final boolean entityExists = entityLiving != null;
                    final Entity entity = (Entity) (entityExists ? entityLiving : stack.getFrame());
                    if (world == null && entity.getWorld() instanceof ClientWorld) {
                        world = (ClientWorld) entity.getWorld();
                    }
                    double rotation = entityExists ? (double) entity.getYaw() : getFrameRotation((ItemFrameEntity) entity);
                    rotation = rotation % 360.0D;
                    double adjusted = Math.PI - ((rotation - 90.0D) * 0.01745329238474369D - getAngle(world, entity, stack));
                    if (entityExists) {
                        adjusted = wobble(world, adjusted);
                    }
                    final float f = (float) (adjusted / (Math.PI * 2D));
                    return MathHelper.floorMod(f, 1.0F);
                }
            }
            private double wobble(ClientWorld world, double amount) {
                if (world.getTime() != lastUpdateTick) {
                    lastUpdateTick = world.getTime();
                    double d0 = amount - rotation;
                    d0 = MathHelper.floorMod(d0 + Math.PI, Math.PI * 2D) - Math.PI;
                    d0 = MathHelper.clamp(d0, -1.0D, 1.0D);
                    rota += d0 * 0.1D;
                    rota *= 0.8D;
                    rotation += rota;
                }
                return rotation;
            }
            private double getFrameRotation(ItemFrameEntity itemFrame) {
                return (double) MathHelper.wrapDegrees(180 + itemFrame.getHorizontalFacing().getHorizontal() * 90);
            }
            private double getAngle(ClientWorld world, Entity entity, ItemStack stack) {
                if (stack.getItem() == ItemInit.CALIBRATED_COMPASS) {
                    LodestoneTrackerComponent tracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
                    BlockPos pos;
                    if (tracker != null && stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER) && tracker.target().isPresent()) {
                        GlobalPos globalPos = tracker.target().get();
                        BlockPos bpos = globalPos.pos();
                        int x = bpos.getX();
                        int z = bpos.getZ();
                        pos = new BlockPos(x, 0, z);
                    } else {
                        pos = world.getSpawnPos();
                    }
                    return Math.atan2((double) pos.getZ() - entity.getPos().z, (double) pos.getX() - entity.getPos().x);
                }
                return 0.0D;
            }
        });
    }
}