package net.jaftsun.fromtheskies.entity.client;

import net.jaftsun.fromtheskies.FromTheSkies;
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
import net.minecraft.util.Mth;

public class BreemBruteModel<T extends BreemEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "breem_brute"),
                    "main"
            );

    private final ModelPart root;

    // Main parts from the brute Blockbench export
    private final ModelPart Head;
    private final ModelPart Cape;
    private final ModelPart Torso;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public BreemBruteModel(ModelPart root) {
        this.root = root;

        this.Head = root.getChild("Head");
        this.Cape = root.getChild("Cape");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition Head = partDefinition.addOrReplaceChild("Head",
                CubeListBuilder.create()
                        .texOffs(26, 6).addBox(-1.1184F, -3.0006F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0001F))
                        .texOffs(19, 6).addBox(-1.1184F, -2.5006F, -1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 1).addBox(-0.6184F, -3.5006F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(15, 14).addBox(-0.6184F, -3.0006F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 18).addBox(-2.1184F, -1.5006F, -1.0F, 4.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 1).addBox(-1.6184F, -2.0006F, -1.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0001F))
                        .texOffs(25, 13).addBox(-1.1184F, -2.5006F, -0.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 20).addBox(-1.6184F, -2.5006F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 11).addBox(-1.6184F, -0.5006F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, -1.0F)
        );

        PartDefinition Cape = partDefinition.addOrReplaceChild("Cape",
                CubeListBuilder.create()
                        .texOffs(0, 13).addBox(-3.0F, 0.0F, -0.25F, 6.0F, 9.0F, 0.5F, new CubeDeformation(0.0F))
                        .texOffs(9, 8).addBox(-2.0F, 9.0F, -0.25F, 4.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, 0.75F)
        );

        PartDefinition Torso = partDefinition.addOrReplaceChild("Torso",
                CubeListBuilder.create()
                        .texOffs(1, 8).addBox(-1.25F, -8.0F, -1.5F, 2.5F, 2.0F, 2.0F, new CubeDeformation(0.001F))
                        .texOffs(0, 2).addBox(-3.0F, -11.0F, -1.5F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition RightArm = partDefinition.addOrReplaceChild("RightArm",
                CubeListBuilder.create()
                        .texOffs(1, 23).mirror()
                        .addBox(-1.25F, -1.0F, -1.0F, 1.25F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-3.0F, 14.0F, -0.5F)
        );

        PartDefinition LeftArm = partDefinition.addOrReplaceChild("LeftArm",
                CubeListBuilder.create()
                        .texOffs(9, 23)
                        .addBox(0.0F, -1.0F, -1.0F, 1.25F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 14.0F, -0.5F)
        );

        PartDefinition RightLeg = partDefinition.addOrReplaceChild("RightLeg",
                CubeListBuilder.create()
                        .texOffs(17, 23).mirror()
                        .addBox(-0.25F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-1.0F, 18.0F, -0.5F)
        );

        PartDefinition LeftLeg = partDefinition.addOrReplaceChild("LeftLeg",
                CubeListBuilder.create()
                        .texOffs(25, 23)
                        .addBox(-1.0F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, 18.0F, -0.5F)
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Basic head look rotation for now.
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.Head.xRot = headPitch * ((float) Math.PI / 180F);



        //Interact diamonds
        if (entity.isInspectAnimationActive()) {
            this.RightArm.xRot = -1.5F;
            this.RightArm.yRot = -0.15F;
            //calm shake
            this.Head.zRot += Mth.sin(ageInTicks * 0.9F) * 0.18F;
        } else {
            // WALK / RUN
            this.RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
            this.LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.2F * limbSwingAmount;

            this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.2F * limbSwingAmount;
            this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;

            if (entity.isSprinting()) {
                this.RightLeg.xRot *= 1.35F;
                this.LeftLeg.xRot *= 1.35F;
                this.RightArm.xRot *= 1.35F;
                this.LeftArm.xRot *= 1.35F;
            }
            //Attack
            float attackProgress = entity.getAttackAnim(0.0F);
            if (attackProgress > 0.0F) {
                this.RightArm.xRot = -2.0F + attackProgress * 1.2F;
            }
        }

        //Angry Shake
        if (entity.isAngryShakeActive() || entity.isAggressive() || entity.getTarget() != null) {
            //this.Head.yRot += Mth.sin(ageInTicks * 1.8F) * 0.55F;
            this.Head.zRot += Mth.sin(ageInTicks * 1.9F) * 0.55F;
        }
    }
}