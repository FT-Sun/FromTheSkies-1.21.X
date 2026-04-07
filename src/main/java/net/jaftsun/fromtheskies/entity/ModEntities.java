package net.jaftsun.fromtheskies.entity;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(FromTheSkies.MOD_ID);

    public static final Supplier<EntityType<BreemEntity>> BREEM =
            ENTITY_TYPES.register("breem",
                    () -> EntityType.Builder.of(BreemEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .build("breem"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
