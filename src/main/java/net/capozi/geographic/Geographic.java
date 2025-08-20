package net.capozi.geographic;

import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class Geographic implements ModInitializer {
    public static final String MOD_ID = "geographic";
    @Override
    public void onInitialize() {
        ItemInit.init();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.COMPASS, ItemInit.CALIBRATED_COMPASS);

        });
    }
}
