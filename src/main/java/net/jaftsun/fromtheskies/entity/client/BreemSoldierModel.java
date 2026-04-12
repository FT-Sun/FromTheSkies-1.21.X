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

public class BreemSoldierModel<T extends BreemEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(FromTheSkies.MOD_ID, "breem_soldier"),
                    "main"
            );

    private final ModelPart root;

    // Main body parts from the soldier Blockbench export
    private final ModelPart Head;
    private final ModelPart Cape;
    private final ModelPart Torso;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public BreemSoldierModel(ModelPart root) {
        this.root = root;

        // These names must match the exported model part names
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

        // Head
        partDefinition.addOrReplaceChild("Head",
                CubeListBuilder.create()
                        .texOffs(26, 8).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0001F))
                        .texOffs(16, 4).addBox(-1.0F, -2.5F, -1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 1).addBox(-0.5F, -3.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 15).addBox(-0.5F, -3.0F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 19).addBox(-2.0F, -1.5F, -1.0F, 4.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 0).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0001F))
                        .texOffs(20, 12).addBox(-1.0F, -2.5F, -0.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 17).addBox(-1.5F, -2.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 9).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, -1.0F)
        );

        // Cape
        partDefinition.addOrReplaceChild("Cape",
                CubeListBuilder.create()
                        .texOffs(0, 13).addBox(-3.0F, 0.0F, -0.25F, 6.0F, 9.0F, 0.5F, new CubeDeformation(0.0F))
                        .texOffs(0, 13).addBox(-2.0F, 9.0F, -0.25F, 4.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, 0.75F)
        );

        // Torso
        partDefinition.addOrReplaceChild("Torso",
                CubeListBuilder.create()
                        .texOffs(1, 8).addBox(-1.25F, -8.0F, -1.5F, 2.5F, 2.0F, 2.0F, new CubeDeformation(0.001F))
                        .texOffs(1, 2).addBox(-2.0F, -11.0F, -1.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        // Right arm
        partDefinition.addOrReplaceChild("RightArm",
                CubeListBuilder.create()
                        .texOffs(0, 24).mirror()
                        .addBox(-1.25F, -1.0F, -1.0F, 1.25F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-2.0F, 14.0F, -0.5F)
        );

        // Left arm
        partDefinition.addOrReplaceChild("LeftArm",
                CubeListBuilder.create()
                        .texOffs(7, 24)
                        .addBox(0.0F, -1.0F, -1.0F, 1.25F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 14.0F, -0.5F)
        );

        // Right leg
        partDefinition.addOrReplaceChild("RightLeg",
                CubeListBuilder.create()
                        .texOffs(15, 23).mirror()
                        .addBox(-0.25F, 0.0263F, -0.9999F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-1.0F, 18.0F, -0.5F)
        );

        // Left leg
        partDefinition.addOrReplaceChild("LeftLeg",
                CubeListBuilder.create()
                        .texOffs(22, 23)
                        .addBox(-1.0F, -0.0263F, -0.9999F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.0F, 18.0F, -0.5F)
        );

        // Soldier export uses 32x32 texture size
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Reset pose before applying any movement
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Basic head look rotation
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.Head.xRot = headPitch * ((float) Math.PI / 180F);

        // Keeping this simple for now while focusing on rendering.
        // We can add soldier-specific animations back later.
    }
}
