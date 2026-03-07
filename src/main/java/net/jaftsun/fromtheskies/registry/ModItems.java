package net.jaftsun.fromtheskies.registry;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.item.ModToolTiers;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FromTheSkies.MOD_ID);

//    Sample Item
//    public static final DeferredItem<Item> SAMPLE = ITEMS.register("sample", () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> SAMPLE_SWORD = ITEMS.register("sample_sword",
            () -> new SwordItem(ModToolTiers.SAMPLE, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.SAMPLE, 100, 20f))));
    public static final DeferredItem<PickaxeItem> SAMPLE_PICKAXE = ITEMS.register("sample_pickaxe",
            () -> new PickaxeItem(ModToolTiers.SAMPLE, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.SAMPLE, 10.0f, -20f))));
    public static final DeferredItem<AxeItem> SAMPLE_AXE = ITEMS.register("sample_axe",
            () -> new AxeItem(ModToolTiers.SAMPLE, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.SAMPLE, 2.5f, -2f))));
    public static final DeferredItem<ShovelItem> SAMPLE_SHOVEL = ITEMS.register("sample_shovel",
            () -> new ShovelItem(ModToolTiers.SAMPLE, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.SAMPLE, 15.0f, -0.5f))));
    public static final DeferredItem<HoeItem> SAMPLE_HOE = ITEMS.register("sample_hoe",
            () -> new HoeItem(ModToolTiers.SAMPLE, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.SAMPLE, 1.0f, -1f))));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
