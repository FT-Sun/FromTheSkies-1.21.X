package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FromTheSkies.MOD_ID);

    // your existing items...

    public static final DeferredItem<SpawnEggItem> BREEM_SPAWN_EGG =
            ITEMS.registerItem("breem_spawn_egg",
                    properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.BREEM.get())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
