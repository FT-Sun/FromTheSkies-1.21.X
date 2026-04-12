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
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class BreemModel<T extends BreemEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "breem"), "main");

    private final ModelPart root;
    //private final ModelPart head;

    //Individual Body Parts
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart Mask;


    public BreemModel(ModelPart root) {
        this.root = root;

        //Names match parts in BlockBench
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Mask = this.Head.getChild("Mask");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition Head = partDefinition.addOrReplaceChild("Head",
                CubeListBuilder.create().texOffs(26, 8).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0001F))
                        .texOffs(15, 4).addBox(-1.0F, -2.5F, -1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 1).addBox(-0.5F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 12).addBox(-0.5F, -3.0F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-2.0F, -1.5F, -1.0F, 4.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 2).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0001F))
                        .texOffs(19, 12).addBox(-1.0F, -2.5F, -0.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(23, 17).addBox(-1.5F, -2.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 9).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, -1.0F)
        );

        PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create()
                        // Top (wider)
                        .texOffs(1, 13).addBox(-2.0F, -2.0F, -2.2F, 4.0F, 3.0F, 0.25F, new CubeDeformation(0.0F))

                        // Bottom (narrower)
                        .texOffs(1, 16).addBox(-1.0F, 1.0F, -2.2F, 2.0F, 2.0F, 0.25F, new CubeDeformation(0.0F)),

                PartPose.offset(0.0F, -1.0F, 0.0F)
        );



        partDefinition.addOrReplaceChild("Torso",
                CubeListBuilder.create().texOffs(1, 8).addBox(-1.25F, -8.0F, -1.5F, 2.5F, 2.0F, 2.0F, new CubeDeformation(0.001F))
                .texOffs(1, 2).addBox(-2.0F, -11.0F, -1.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        partDefinition.addOrReplaceChild("RightArm",
                CubeListBuilder.create().texOffs(0, 24).mirror()
                        .addBox(-1.25F, -1.0F, -1.0F, 1.25F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-2.0F, 14.0F, -0.5F)
        );


        partDefinition.addOrReplaceChild("LeftArm",
                CubeListBuilder.create().texOffs(7, 24)
                .addBox(0.0F, -1.0F, -1.0F, 1.25F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 14.0F, -0.5F)
        );

        partDefinition.addOrReplaceChild("RightLeg",
                CubeListBuilder.create().texOffs(15, 23).mirror()
                        .addBox(-0.25F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-1.0F, 18.0F, -0.5F)
        );


        partDefinition.addOrReplaceChild("LeftLeg",
                CubeListBuilder.create().texOffs(22, 23)
                        .addBox(-1.0F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, 18.0F, -0.5F)
        );
        //Matches the texture size 32x32
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.Head.xRot = headPitch * ((float) Math.PI / 180F);

        BreemVariant variant = entity.getVariant();

        //Only shows mask for shaman
        this.Mask.visible = (variant == BreemVariant.SHAMAN);

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
