package net.alecluh.mesonmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.alecluh.mesonmod.MesonMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item POWER_METER = registerItem("power_meter", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MesonMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        MesonMod.LOGGER.info("Registering Mod Items for " + MesonMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(POWER_METER);
        });
    }
    
}
