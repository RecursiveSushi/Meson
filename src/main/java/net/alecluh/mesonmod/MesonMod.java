package net.alecluh.mesonmod;

import net.fabricmc.api.ModInitializer;
import net.alecluh.mesonmod.block.ModBlocks;
import net.alecluh.mesonmod.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MesonMod implements ModInitializer {
	public static final String MOD_ID = "mesonmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}