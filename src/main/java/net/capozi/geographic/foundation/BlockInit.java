package net.capozi.geographic.foundation;

import net.capozi.geographic.Geographic;
import net.capozi.geographic.common.block.WayfinderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockInit {
    public static void init() {}
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(Geographic.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }
    private static Block registerBlock(String name, Block block, boolean registerBlockItem) {
        if (registerBlockItem) { registerBlockItem(name, block); }
        return Registry.register(Registries.BLOCK, Identifier.of(Geographic.MOD_ID, name), block);
    }
    public static final Block WAYFINDER = registerBlock("wayfinder", new WayfinderBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS)), true);
}
