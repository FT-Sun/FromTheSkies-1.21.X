package net.jaftsun.fromtheskies.entity;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = FromTheSkies.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BREEM.get(), BreemEntity.createAttributes().build());
    }
}
