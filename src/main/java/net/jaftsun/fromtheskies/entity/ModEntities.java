package net.jaftsun.fromtheskies.entity;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.jaftsun.fromtheskies.entity.custom.GeckoEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FromTheSkies.MOD_ID);

    public static final Supplier<EntityType<GeckoEntity>> GECKO =
            ENTITY_TYPES.register("gecko", () -> EntityType.Builder.of(GeckoEntity::new, MobCategory.CREATURE)
                    .sized(0.60f, 0.35f).build("gecko"));

    public static final Supplier<EntityType<BreemEntity>> BREEM =
            ENTITY_TYPES.register("breem",
                    () -> EntityType.Builder.of(BreemEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .build("breem"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
