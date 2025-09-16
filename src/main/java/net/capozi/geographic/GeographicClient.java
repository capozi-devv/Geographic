package net.capozi.geographic;

import net.capozi.geographic.common.client.CalibratedAnglePredicateProvider;
import net.capozi.geographic.common.item.CalibratedCompassItem;
import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.CompassAnglePredicateProvider;
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
        CompassAnglePredicateProvider.CompassTarget target = new CompassAnglePredicateProvider.CompassTarget() {
            @Override public @Nullable GlobalPos getPos(ClientWorld world, ItemStack stack, Entity entity) {
                return CalibratedCompassItem.createPos(world);
            }
        };
        ModelPredicateProviderRegistry.register(ItemInit.CALIBRATED_COMPASS, Identifier.of(Geographic.MOD_ID, "angle"), new CompassAnglePredicateProvider(target));
    }
    @Override public void onInitializeClient() {
        registerModelPredicates();
    }
}
