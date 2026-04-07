package net.jaftsun.fromtheskies.entity.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.BreemVariant;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BreemRenderer extends MobRenderer<BreemEntity, BreemModel<BreemEntity>> {

    private static final Map<BreemVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(BreemVariant.class), map -> {
                map.put(BreemVariant.VILLAGER,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_villager_texture.png"));
                map.put(BreemVariant.SOLDIER,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_soldier_texture.png"));
                map.put(BreemVariant.BRUTE,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_brute_texture.png"));
                map.put(BreemVariant.SHAMAN,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_shaman_texture.png"));
            });

    public BreemRenderer(EntityRendererProvider.Context context) {
        super(context, new BreemModel<>(context.bakeLayer(BreemModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BreemEntity entity) {
        return LOCATION_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    public void render(BreemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
