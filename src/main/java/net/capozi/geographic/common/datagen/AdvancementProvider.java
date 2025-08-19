package net.capozi.geographic.common.datagen;

import net.capozi.geographic.Geographic;
import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {
    public AdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }
    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry getCompass = Advancement.Builder.create()
                .display(
                        ItemInit.CALIBRATED_COMPASS,
                        Text.literal("Calibration Operation"),
                        Text.literal("Obtain a Calibrated Compass"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.GOAL,
                        true,
                        true,
                        false
                )
                .criterion("got_compass", InventoryChangedCriterion.Conditions.items(ItemInit.CALIBRATED_COMPASS))
                .build(consumer, Geographic.MOD_ID + "/get_compass");
        AdvancementEntry goated = Advancement.Builder.create()
                .display(
                        Items.GOAT_HORN,
                        Text.literal("Goated with the Sauce"),
                        Text.literal("Play a goat horn"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("goated", ConsumeItemCriterion.Conditions.item(Items.GOAT_HORN))
                .build(consumer, Geographic.MOD_ID + "/goated");
    }
}
