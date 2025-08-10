package net.capozi.geographic.foundation;

import net.capozi.geographic.Geographic;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemInit {
    public static void init() {}
    private static Item registerItem(String name, Item item) {
      return Registry.register(Registries.ITEM, Identifier.of(Geographic.MOD_ID, name), item);
    }
    
}