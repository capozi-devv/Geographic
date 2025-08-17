package net.capozi.geographic.common.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CalibratedAnglePredicateProvider implements ClampedModelPredicateProvider {
    public static final int field_38798 = 0;
    private final AngleInterpolator aimedInterpolator = new AngleInterpolator();
    private final AngleInterpolator aimlessInterpolator = new AngleInterpolator();
    public final CompassTarget compassTarget;

   public CalibratedAnglePredicateProvider(CompassTarget compassTarget) {
       this.compassTarget = compassTarget;
   }

    public float unclampedCall(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
        Entity entity = (Entity)(livingEntity != null ? livingEntity : itemStack.getHolder());
        if (entity == null) {
            return 0.0F;
        } else {
            clientWorld = this.getClientWorld(entity, clientWorld);
            return clientWorld == null ? 0.0F : this.getAngle(itemStack, clientWorld, i, entity);
        }
    }

    private float getAngle(ItemStack stack, ClientWorld world, int seed, Entity entity) {
        GlobalPos globalPos = this.compassTarget.getPos(world, stack, entity);
        long l = world.getTime();
        return !this.canPointTo(entity, globalPos) ? this.getAimlessAngle(seed, l) : this.getAngleTo(entity, l, globalPos.pos());
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessInterpolator.shouldUpdate(time)) {
            this.aimlessInterpolator.update(time, Math.random());
        }

        double d = this.aimlessInterpolator.value + (double)((float)this.scatter(seed) / (float)Integer.MAX_VALUE);
        return MathHelper.floorMod((float)d, 1.0F);
    }

    private float getAngleTo(Entity entity, long time, BlockPos pos) {
        double d = this.getAngleTo(entity, pos);
        double e = this.getBodyYaw(entity);
        double f;
        if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isMainPlayer() && playerEntity.getWorld().getTickManager().shouldTick()) {
                if (this.aimedInterpolator.shouldUpdate(time)) {
                    this.aimedInterpolator.update(time, (double)0.5F - (e - (double)0.25F));
                }

                f = d + this.aimedInterpolator.value;
                return MathHelper.floorMod((float)f, 1.0F);
            }
        }

        f = (double)0.5F - (e - (double)0.25F - d);
        return MathHelper.floorMod((float)f, 1.0F);
    }

    @Nullable
    private ClientWorld getClientWorld(Entity entity, @Nullable ClientWorld world) {
        return world == null && entity.getWorld() instanceof ClientWorld ? (ClientWorld)entity.getWorld() : world;
    }

    private boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
        return pos != null && pos.dimension() == entity.getWorld().getRegistryKey() && !(pos.pos().getSquaredDistance(entity.getPos()) < (double)1.0E-5F);
    }

    private double getAngleTo(Entity entity, BlockPos pos) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / (double)((float)Math.PI * 2F);
    }

    private double getBodyYaw(Entity entity) {
        return MathHelper.floorMod((double)(entity.getBodyYaw() / 360.0F), (double)1.0F);
    }

    private int scatter(int seed) {
        return seed * 1327217883;
    }

    @Environment(EnvType.CLIENT)
    static class AngleInterpolator {
        double value;
        private double speed;
        private long lastUpdateTime;

        boolean shouldUpdate(long time) {
            return this.lastUpdateTime != time;
        }

        void update(long time, double target) {
            this.lastUpdateTime = time;
            double d = target - this.value;
            d = MathHelper.floorMod(d + (double)0.5F, (double)1.0F) - (double)0.5F;
            this.speed += d * 0.1;
            this.speed *= 0.8;
            this.value = MathHelper.floorMod(this.value + this.speed, (double)1.0F);
        }
    }

    @Environment(EnvType.CLIENT)
    public interface CompassTarget {
        @Nullable
        GlobalPos getPos(ClientWorld world, ItemStack stack, Entity entity);
    }
}
