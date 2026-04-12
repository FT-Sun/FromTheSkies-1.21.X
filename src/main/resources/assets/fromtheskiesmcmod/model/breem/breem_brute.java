// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class breem_villager<T extends breem> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "breem_villager"), "main");
	private final ModelPart Head;
	private final ModelPart Cape;
	private final ModelPart Torso;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public breem_villager(ModelPart root) {
		this.Head = root.getChild("Head");
		this.Cape = root.getChild("Cape");
		this.Torso = root.getChild("Torso");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(26, 6).addBox(-1.1184F, -3.0006F, -1.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0001F))
		.texOffs(19, 6).addBox(-1.1184F, -2.5006F, -1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 1).addBox(-0.6184F, -3.5006F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(15, 14).addBox(-0.6184F, -3.0006F, -1.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(14, 18).addBox(-2.1184F, -1.5006F, -1.0F, 4.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 1).addBox(-1.6184F, -2.0006F, -1.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0001F))
		.texOffs(25, 13).addBox(-1.1184F, -2.5006F, -0.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 20).addBox(-1.6184F, -2.5006F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 11).addBox(-1.6184F, -0.5006F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -1.0F));

		PartDefinition Cape = partdefinition.addOrReplaceChild("Cape", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, 0.0F, -0.25F, 6.0F, 9.0F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(9, 8).addBox(-2.0F, 9.0F, -0.25F, 4.0F, 1.0F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.75F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(1, 8).addBox(-1.25F, -8.0F, -1.5F, 2.5F, 2.0F, 2.0F, new CubeDeformation(0.001F))
		.texOffs(0, 2).addBox(-3.0F, -11.0F, -1.5F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(1, 23).mirror().addBox(-1.25F, -1.0F, -1.0F, 1.25F, 7.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, 14.0F, -0.5F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(9, 23).addBox(0.0F, -1.0F, -1.0F, 1.25F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 14.0F, -0.5F));

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(17, 23).mirror().addBox(-0.25F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.0F, 18.0F, -0.5F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(25, 23).addBox(-1.0F, 0.0F, -1.0F, 1.25F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 18.0F, -0.5F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(breem entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Cape.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}