package net.capozi.geographic;

import net.capozi.geographic.foundation.ItemInit;
import net.fabricmc.api.ModInitializer;

public class Geographic implements ModInitializer {
    public static final String MOD_ID = "geographic";
    @Override
    public void onInitialize() {
        ItemInit.init();
    }
}
