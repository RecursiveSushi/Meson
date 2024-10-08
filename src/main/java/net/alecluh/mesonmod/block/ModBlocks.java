package net.alecluh.mesonmod.block;

import net.alecluh.mesonmod.MesonMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.block.MapColor;

public class ModBlocks {
    public static final Block SPEAKER_BLOCK = registerBlock("speaker_block",
        new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).sounds(BlockSoundGroup.WOOD).strength(0.8F).burnable())
        );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(MesonMod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(MesonMod.MOD_ID, name), 
            new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        MesonMod.LOGGER.info("Registering Mod Blocks for " + MesonMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.SPEAKER_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(ModBlocks.SPEAKER_BLOCK);
        });
    }
    
}