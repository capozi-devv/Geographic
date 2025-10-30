package net.capozi.geographic.common.item;

import net.capozi.geographic.foundation.ItemInit;

import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class CalibratedCompassItem extends Item {
    private int XorYorZ = 1;
    private int distaceModifier = 1;
    public CalibratedCompassItem(Settings settings) {
        super(settings);
    }
    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        setTarget(stack, GlobalPos.create(stack.getHolder().getWorld().getRegistryKey(), stack.getHolder().getWorld().getSpawnPos()));
        return stack;
    }
    public static GlobalPos createPos(World world) {
        int x = 0;
        int y = 0;
        int z = 0;
        RegistryKey<World> dimension = world.getRegistryKey();
        BlockPos blockPos = new BlockPos(x, y, z);
        GlobalPos targetPos = GlobalPos.create(dimension, blockPos);
        return targetPos;
    }
    public static void setTarget(ItemStack stack, GlobalPos pos) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CalibratedCompassItem)) return;
        stack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(pos), true));
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
        if (user.isSneaking() && !world.isClient() && !user.isSprinting()) {
            XorYorZ = (XorYorZ % 6) + 1;
            user.sendMessage(Text.literal("Calibration Mode Set To: " + XorYorZ), true);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        if (!user.isSneaking() && !world.isClient() && user.isSprinting()) {
            distaceModifier = (distaceModifier % 6) + 1;
            user.sendMessage(Text.literal("Distance Modifier Set To: " + distaceModifier), true);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        if (!world.isClient()) {
            int x = 0, y = 0, z = 0;
            switch (XorYorZ) {
                case 1 -> x = 1;
                case 2 -> y = 1;
                case 3 -> z = 1;
                case 4 -> x = -1;
                case 5 -> y = -1;
                case 6 -> z = -1;
            }
            int multiplier = 1;
            switch (distaceModifier) {
                case 1 -> multiplier = 1;
                case 2 -> multiplier = 10;
                case 3 -> multiplier = 100;
                case 4 -> multiplier = 1000;
                case 5 -> multiplier = 10000;
                case 6 -> multiplier = 100000;
            }
            ItemStack stack = user.getStackInHand(hand);
            LodestoneTrackerComponent tracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
            GlobalPos storedPos = tracker != null ? tracker.target().orElse(CalibratedCompassItem.createPos(world)) : CalibratedCompassItem.createPos(world);
            BlockPos pos = storedPos.pos();
            BlockPos newPos = new BlockPos(pos.getX() + x * multiplier, pos.getY() + y * multiplier, pos.getZ() + z * multiplier);
            GlobalPos updated = GlobalPos.create(storedPos.dimension(), newPos);
            CalibratedCompassItem.setTarget(stack, updated);
            user.sendMessage(Text.literal("Set Target Position To: " + newPos.toShortString()), true);
            tooltipL = stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER).target().get().pos().toShortString();
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        try {
            tooltip.add(Text.literal("Targeting: " + tooltipL).formatted(Formatting.GRAY));
        } catch (NullPointerException e) {
            tooltip.add(Text.literal("Error Loading Position Data").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal("Mode 1: Increase X").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Mode 2: Increase Y").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Mode 3: Increase Z").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Mode 4: Decrease X").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Mode 5: Decrease Y").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Mode 6: Decrease Z").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Sprint + RightClick to Cycle Distance Multiplier").formatted(Formatting.DARK_GRAY));
        tooltip.add(Text.literal("Shift + RightClick to Cycle Modes").formatted(Formatting.DARK_GRAY));
    }
}