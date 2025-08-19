package net.capozi.geographic;

import net.capozi.geographic.common.datagen.AdvancementProvider;
import net.capozi.geographic.common.datagen.RecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GeographicDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(AdvancementProvider::new);
    }
}
