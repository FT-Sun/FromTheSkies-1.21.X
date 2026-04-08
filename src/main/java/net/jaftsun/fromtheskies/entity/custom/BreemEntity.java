package net.jaftsun.fromtheskies.entity.custom;

import net.jaftsun.fromtheskies.entity.BreemVariant;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class BreemEntity extends Piglin {

    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID =
            SynchedEntityData.defineId(BreemEntity.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    public BreemEntity(EntityType<? extends AbstractPiglin> entityType, Level level) {
        super(entityType, level);
        this.setImmuneToZombification(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, BreemVariant.UNSET.getId());
    }

    public BreemVariant getVariant() {
        return BreemVariant.byId(this.entityData.get(DATA_VARIANT_ID));
    }

    public void setVariant(BreemVariant variant) {
        this.entityData.set(DATA_VARIANT_ID, variant.getId());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.walkAnimation.speed() < 0.05F) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        } else {
            this.idleAnimationState.stop();
        }

        if (this.swinging) {
            this.attackAnimationState.startIfStopped(this.tickCount);
        } else {
            this.attackAnimationState.stop();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() instanceof Player player && isPlayerSafeFromBreem(player)) {
            this.setTarget(null);
        }
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Override
    protected boolean shouldZombify() {
        return false;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof Player player && isPlayerSafeFromBreem(player)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.level().isClientSide && canDiamondBarter(player, stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            doSimpleDiamondBarter((ServerLevel) this.level());

            this.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private boolean canDiamondBarter(Player player, ItemStack stack) {
        return !this.isBaby()
                && this.getTarget() == null
                && stack.is(Items.DIAMOND)
                && !this.isAggressive()
                && this.distanceTo(player) <= 6.0F;
    }

    private boolean isPlayerSafeFromBreem(Player player) {
        return isWearingAnyDiamondArmor(player) || isHoldingDiamond(player);
    }

    private boolean isHoldingDiamond(Player player) {
        return player.getMainHandItem().is(Items.DIAMOND) || player.getOffhandItem().is(Items.DIAMOND);
    }

    private boolean isWearingAnyDiamondArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET)
                || player.getItemBySlot(EquipmentSlot.CHEST).is(Items.DIAMOND_CHESTPLATE)
                || player.getItemBySlot(EquipmentSlot.LEGS).is(Items.DIAMOND_LEGGINGS)
                || player.getItemBySlot(EquipmentSlot.FEET).is(Items.DIAMOND_BOOTS);
    }

    private void doSimpleDiamondBarter(ServerLevel level) {
        int roll = level.random.nextInt(6);

        ItemStack reward = switch (roll) {
            case 0 -> new ItemStack(Items.ENDER_PEARL, 2);
            case 1 -> new ItemStack(Items.OBSIDIAN, 4);
            case 2 -> new ItemStack(Items.STRING, 8);
            case 3 -> new ItemStack(Items.QUARTZ, 8);
            case 4 -> new ItemStack(Items.FIRE_CHARGE, 2);
            default -> new ItemStack(Items.SPECTRAL_ARROW, 8);
        };

        this.spawnAtLocation(level, reward);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {

        if (this.getVariant() == BreemVariant.UNSET) {
            int pick = level.getRandom().nextInt(4);

            switch (pick) {
                case 0 -> this.setVariant(BreemVariant.VILLAGER);
                case 1 -> this.setVariant(BreemVariant.SOLDIER);
                case 2 -> this.setVariant(BreemVariant.BRUTE);
                default -> this.setVariant(BreemVariant.SHAMAN);
            }
        }

        this.setImmuneToZombification(true);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Piglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }
}
