package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.BreemVariant;
import net.jaftsun.fromtheskies.entity.ModEntities;
import net.jaftsun.fromtheskies.item.BreemVariantSpawnEggItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FromTheSkies.MOD_ID);

    public static final DeferredItem<BreemVariantSpawnEggItem> BREEM_VILLAGER_SPAWN_EGG =
            ITEMS.register("breem_villager_spawn_egg",
                    () -> new BreemVariantSpawnEggItem(
                            ModEntities.BREEM.get(),
                            BreemVariant.VILLAGER,
                            new Item.Properties()
                    ));

    public static final DeferredItem<BreemVariantSpawnEggItem> BREEM_SOLDIER_SPAWN_EGG =
            ITEMS.register("breem_soldier_spawn_egg",
                    () -> new BreemVariantSpawnEggItem(
                            ModEntities.BREEM.get(),
                            BreemVariant.SOLDIER,
                            new Item.Properties()
                    ));

    public static final DeferredItem<BreemVariantSpawnEggItem> BREEM_BRUTE_SPAWN_EGG =
            ITEMS.register("breem_brute_spawn_egg",
                    () -> new BreemVariantSpawnEggItem(
                            ModEntities.BREEM.get(),
                            BreemVariant.BRUTE,
                            new Item.Properties()
                    ));

    public static final DeferredItem<BreemVariantSpawnEggItem> BREEM_SHAMAN_SPAWN_EGG =
            ITEMS.register("breem_shaman_spawn_egg",
                    () -> new BreemVariantSpawnEggItem(
                            ModEntities.BREEM.get(),
                            BreemVariant.SHAMAN,
                            new Item.Properties()
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
