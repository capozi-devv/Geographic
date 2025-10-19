package net.capozi.geographic.common.item;

import net.capozi.geographic.foundation.ItemInit;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CalibratedCompassItem extends Item {
    private int XorYorZ = 1;
    public CalibratedCompassItem(Settings settings) {
        super(settings);
    }
    @Nullable public static GlobalPos createPos(World world) {
        int x = 0;
        int y = 0;
        int z = 0;
        RegistryKey<World> dimension = world.getRegistryKey();
        BlockPos blockPos = new BlockPos(x, y, z);
        GlobalPos targetPos = new GlobalPos(dimension, blockPos);
        return targetPos;
    }
    public static void setTarget(ItemStack stack, GlobalPos pos) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CalibratedCompassItem)) return;
        stack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(pos), true));
    }
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(DataComponentTypes.LODESTONE_TRACKER) || super.hasGlint(stack);
    }
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld serverWorld) {
            LodestoneTrackerComponent lodestoneTrackerComponent = (LodestoneTrackerComponent)stack.get(DataComponentTypes.LODESTONE_TRACKER);
            if (lodestoneTrackerComponent != null) {
                LodestoneTrackerComponent lodestoneTrackerComponent2 = lodestoneTrackerComponent.forWorld(serverWorld);
                if (lodestoneTrackerComponent2 != lodestoneTrackerComponent) {
                    stack.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent2);
                }
            }
        }
    }
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (!world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            return super.useOnBlock(context);
        } else {
            world.playSound((PlayerEntity)null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            boolean bl = !playerEntity.isInCreativeMode() && itemStack.getCount() == 1;
            LodestoneTrackerComponent lodestoneTrackerComponent = new LodestoneTrackerComponent(Optional.of(GlobalPos.create(world.getRegistryKey(), blockPos)), true);
            if (bl) {
                itemStack.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
            } else {
                ItemStack itemStack2 = itemStack.copyComponentsToNewStack(ItemInit.CALIBRATED_COMPASS, 1);
                itemStack.decrementUnlessCreative(1, playerEntity);
                itemStack2.set(DataComponentTypes.LODESTONE_TRACKER, lodestoneTrackerComponent);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }
            }
            return ActionResult.success(world.isClient);
        }
    }
    public String tooltipL;
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            if (XorYorZ > 6) XorYorZ = 1;
            XorYorZ++;
            if (XorYorZ > 6) XorYorZ = 1;
            user.sendMessage(Text.literal("Calibration Mode Set To: " + XorYorZ));
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        int x = XorYorZ == 1 ? 1 : 0;
        int y = XorYorZ == 2 ? 1 : 0;
        int z = XorYorZ == 3 ? 1 : 0;
        int xminus = XorYorZ == 4 ? 1 : 0;
        int yminus = XorYorZ == 5 ? 1 : 0;
        int zminus = XorYorZ == 6 ? 1 : 0;
        ItemStack stack = user.getStackInHand(hand);
        var tracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        GlobalPos storedPos = tracker != null ? tracker.target().orElse(CalibratedCompassItem.createPos(world)) : CalibratedCompassItem.createPos(world);
        BlockPos pos = storedPos.pos();
        BlockPos newPos = new BlockPos(pos.getX() + x - xminus, pos.getY() + y - yminus, pos.getZ() + z - zminus);
        GlobalPos updated = GlobalPos.create(storedPos.dimension(), newPos);
        CalibratedCompassItem.setTarget(stack, updated);
        tooltipL = stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER).toString();
        return TypedActionResult.success(user.getStackInHand(hand));
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        try {
            tooltip.add(Text.literal("Targeting: " + tooltipL));
            // add calibration key
        } catch (NullPointerException e) {
            tooltip.add(Text.literal("Error Loading Position Data"));
        }
    }
}