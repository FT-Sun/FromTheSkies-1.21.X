package net.jaftsun.fromtheskies.entity.client;

import net.jaftsun.fromtheskies.FromTheSkies;
import net.jaftsun.fromtheskies.entity.BreemVariant;
//import net.jaftsun.fromtheskies.entity.animations.BreemBruteAnimation;
//import net.jaftsun.fromtheskies.entity.animations.BreemShamanAnimation;
//import net.jaftsun.fromtheskies.entity.animations.BreemSoldierAnimation;
//import net.jaftsun.fromtheskies.entity.animations.BreemVillagerAnimation;
import net.jaftsun.fromtheskies.entity.custom.BreemEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class BreemModel<T extends BreemEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "breem"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public BreemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 16)
                        .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        partDefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 16).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partDefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partDefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 16).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        BreemVariant variant = entity.getVariant();

//        switch (variant) {
//            case VILLAGER -> {
//                this.animateWalk(BreemVillagerAnimation.Walking, limbSwing, limbSwingAmount, 2.0F, 2.5F);
//                this.animate(entity.idleAnimationState, BreemVillagerAnimation.Idle, ageInTicks, 1.0F);
//                this.animate(entity.attackAnimationState, BreemVillagerAnimation.Hitting, ageInTicks, 1.0F);
//            }
//            case SOLDIER -> {
//                this.animateWalk(BreemSoldierAnimation.Walking, limbSwing, limbSwingAmount, 2.0F, 2.5F);
//                this.animate(entity.idleAnimationState, BreemSoldierAnimation.Idle, ageInTicks, 1.0F);
//                this.animate(entity.attackAnimationState, BreemSoldierAnimation.Hitting, ageInTicks, 1.0F);
//            }
//            case BRUTE -> {
//                this.animateWalk(BreemBruteAnimation.Walking, limbSwing, limbSwingAmount, 2.0F, 2.5F);
//                this.animate(entity.idleAnimationState, BreemBruteAnimation.Idle, ageInTicks, 1.0F);
//                this.animate(entity.attackAnimationState, BreemBruteAnimation.Hitting, ageInTicks, 1.0F);
//            }
//            case SHAMAN -> {
//                this.animateWalk(BreemShamanAnimation.Walking, limbSwing, limbSwingAmount, 2.0F, 2.5F);
//                this.animate(entity.idleAnimationState, BreemShamanAnimation.Idle, ageInTicks, 1.0F);
//                this.animate(entity.attackAnimationState, BreemShamanAnimation.SpellOrAttack, ageInTicks, 1.0F);
//            }
//        }
    }
}
