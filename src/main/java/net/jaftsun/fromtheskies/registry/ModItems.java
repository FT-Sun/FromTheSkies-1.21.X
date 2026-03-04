package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FromTheSkies.MOD_ID);

//    Sample Item
//    public static final DeferredItem<Item> SAMPLE = ITEMS.register("sample", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
