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
import java.util.Objects;
import net.minecraft.client.model.HierarchicalModel; //Added this so that BreemModel extends to the SoldierModel

public class BreemRenderer extends MobRenderer<BreemEntity, HierarchicalModel<BreemEntity>> {

    private static final Map<BreemVariant, ResourceLocation> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(BreemVariant.class), map -> {
                map.put(BreemVariant.VILLAGER,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_villager_texture.png"));
                map.put(BreemVariant.SOLDIER,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_soldier_corrected_texture.png"));
                map.put(BreemVariant.BRUTE,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_brute_texture.png"));
                map.put(BreemVariant.SHAMAN,
                        ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "textures/entity/breem/breem_shaman_texture.png"));
            });
    private final BreemModel<BreemEntity> defaultModel;
    private final BreemSoldierModel<BreemEntity> soldierModel;
    private final BreemBruteModel<BreemEntity> bruteModel;

    public BreemRenderer(EntityRendererProvider.Context context) {
        super(context, new BreemModel<>(context.bakeLayer(BreemModel.LAYER_LOCATION)), 0.5f);

        //Keeps references to both models to switch depedning on variants
        this.defaultModel = new BreemModel<>(context.bakeLayer(BreemModel.LAYER_LOCATION));
        this.soldierModel = new BreemSoldierModel<>(context.bakeLayer(BreemSoldierModel.LAYER_LOCATION));
        this.bruteModel = new BreemBruteModel<>(context.bakeLayer(BreemBruteModel.LAYER_LOCATION));

        //default model used for villager and shaman
        this.model = this.defaultModel;
    }

    @Override
    public ResourceLocation getTextureLocation(BreemEntity entity) {
        BreemVariant variant = entity.getVariant();

        if (variant == BreemVariant.UNSET) {
            variant = BreemVariant.VILLAGER;
        }

        return LOCATION_BY_VARIANT.get(variant);
    }

    @Override
    public void render(BreemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {

        BreemVariant variant = entity.getVariant();

        if (variant == BreemVariant.UNSET){
            variant = BreemVariant.VILLAGER;
        }

        //Use soldier model only for soldier
        if (variant == BreemVariant.SOLDIER){
            this.model = soldierModel;
        } else if (variant == BreemVariant.BRUTE){
            this.model = this.bruteModel;
        } else {
            this.model = this.defaultModel;
        }

        //Scale up the normal Breem model so it matches piglings size
        poseStack.scale(2.25f, 2.25f, 2.25f);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
