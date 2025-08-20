package net.capozi.geographic.foundation;

import net.capozi.geographic.Geographic;
import net.capozi.geographic.common.item.CalibratedCompassItem;
import net.capozi.geographic.common.item.NorthStarItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemInit {
    public static void init() {}
    private static Item registerItem(String name, Item item) {
      return Registry.register(Registries.ITEM, Identifier.of(Geographic.MOD_ID, name), item);
    }
    public static final Item CALIBRATED_COMPASS = registerItem("calibrated_compass", new CalibratedCompassItem(new Item.Settings().maxCount(1)));
    public static final Item NORTH_STAR =  registerItem("north_star", new NorthStarItem(new Item.Settings().maxCount(1)));
    public static final Item GRAND_NORTH_STAR = registerItem("grand_north_star", new NorthStarItem(new Item.Settings().maxCount(1)));
}