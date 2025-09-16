package net.capozi.geographic;

import net.capozi.geographic.common.item.CalibratedCompassItem;
import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class Geographic implements ModInitializer {
    public static final String MOD_ID = "geographic";
    @Override public void onInitialize() {
        ItemInit.init();
        CalibratedCompassItem.registerCompassCalibration();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.RECOVERY_COMPASS, ItemInit.CALIBRATED_COMPASS);
            content.addAfter(ItemInit.CALIBRATED_COMPASS, ItemInit.NORTH_STAR);
            content.addAfter(ItemInit.NORTH_STAR, ItemInit.GRAND_NORTH_STAR);
            content.addAfter(ItemInit.GRAND_NORTH_STAR, ItemInit.WANDERING_WAYFINDER);
        });
    }
}
