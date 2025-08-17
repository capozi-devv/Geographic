package net.capozi.geographic;

import net.capozi.geographic.common.client.CalibratedAnglePredicateProvider;
import net.capozi.geographic.common.item.CalibratedCompassItem;
import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeographicClient implements ClientModInitializer {
    public static World getClientWorld() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        return world;
    }
    public static void registerModelPredicates() {
        World world = getClientWorld();
        CalibratedAnglePredicateProvider.CompassTarget target = new CalibratedAnglePredicateProvider.CompassTarget() {
            @Override
            public @Nullable GlobalPos getPos(ClientWorld world, ItemStack stack, Entity entity) {
                return CalibratedCompassItem.createPos(world);
            }
        };
        ModelPredicateProviderRegistry.register(ItemInit.CALIBRATED_COMPASS, Identifier.of(Geographic.MOD_ID, "angle"), new CalibratedAnglePredicateProvider(target));
    }
    @Override
    public void onInitializeClient() {
        registerModelPredicates();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            World world = client.world;
            if(client.player == null || !client.player.isAlive()) return; //that motherfucker is not real (or just dead)

            ItemStack heldItem = client.player.getMainHandStack();
            int xPosOffset = client.options.sneakKey.isPressed() ? 1 : 0;
            int yPosOffest = client.options.jumpKey.isPressed() ? 1 : 0;
            int zPosOffset = client.options.sprintKey.isPressed() ? 1 : 0;
            if(heldItem.getItem() instanceof CalibratedCompassItem ) {
                var tracker = heldItem.get(DataComponentTypes.LODESTONE_TRACKER);
                GlobalPos currentStoredPos = tracker != null ? tracker.target().orElse(CalibratedCompassItem.createPos(world)) : CalibratedCompassItem.createPos(world);
                if (client.options.attackKey.isPressed()) {
                    BlockPos pos = currentStoredPos.pos();
                    BlockPos newPos = new BlockPos(pos.getX() + xPosOffset, pos.getY() + yPosOffest, pos.getZ() + zPosOffset);
                    GlobalPos updated = GlobalPos.create(currentStoredPos.dimension(), newPos);
                    CalibratedCompassItem.setTarget(heldItem, updated);
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeIdentifier(world.getRegistryKey().getValue()); // dimension
                    buf.writeBlockPos(newPos);
                    player.sendMessage(Text.literal("Position Calibrated to → " + newPos.toShortString()), true);
                }
                if (client.options.useKey.isPressed()) {
                    BlockPos pos = currentStoredPos.pos();
                    BlockPos newPos = new BlockPos(pos.getX() - xPosOffset, pos.getY() - yPosOffest, pos.getZ() - zPosOffset);
                    GlobalPos updated = GlobalPos.create(currentStoredPos.dimension(), newPos);
                    CalibratedCompassItem.setTarget(heldItem, updated);
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeIdentifier(world.getRegistryKey().getValue()); // dimension
                    buf.writeBlockPos(newPos);
                    player.sendMessage(Text.literal("Position Calibrated to → " + newPos.toShortString()), true);
                }
            }

        });
    }
}
