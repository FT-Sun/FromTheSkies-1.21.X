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
import net.minecraft.world.entity.*;
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
    public static final EntityDataAccessor<Boolean> DATA_INSPECTING =
            SynchedEntityData.defineId(BreemEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_ANGRY_SHAKING =
            SynchedEntityData.defineId(BreemEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    //Custoom timers for head animations
    private int inspectAnimationTicks = 0;
    private int angryShakeTicks = 0;
    private int barterCooldownTicks = 0;

    private boolean provoked = false;

    public BreemEntity(EntityType<? extends AbstractPiglin> entityType, Level level) {
        super(entityType, level);
        this.setImmuneToZombification(true);
        this.setCanPickUpLoot(false);
        this.setBaby(false);
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack){
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, BreemVariant.UNSET.getId());
        builder.define(DATA_INSPECTING, false);
        builder.define(DATA_ANGRY_SHAKING, false);
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

        if (this.isBaby()){
            this.setBaby(false);
        }

        //Freeze movement while inspecting
        if (this.isInspectAnimationActive()){
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            this.getNavigation().stop();
        }

        if (this.inspectAnimationTicks > 0) {
            this.inspectAnimationTicks--;
            if (this.inspectAnimationTicks == 0){
                this.entityData.set(DATA_INSPECTING, false);

                //Remove diamond after inspecting
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        }

        if (this.barterCooldownTicks > 0){
            this.barterCooldownTicks--;
            if (this.barterCooldownTicks == 0 && !this.level().isClientSide){
                doSimpleDiamondBarter((ServerLevel) this.level());
            }
        }

        if (this.level().isClientSide()){
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
        //Remove inherited pigling gear
        if (!this.isInspectAnimationActive()){
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        // Stop moving while inspecting
        if (this.isInspectAnimationActive()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            this.zza = 0.0F;
            this.xxa = 0.0F;
        }

        if (this.getTarget() instanceof Player player ) {
            if (player.isCreative() || player.isSpectator() || !player.isAlive()) {
                this.setTarget(null);
                this.setAggressive(false);
                this.provoked = false;
            }
        }

        //If no target, no recent anger state, calm down
        if (this.getTarget() == null && !this.level().isClientSide){
            this.setAggressive(false);
        }

        if (this.getTarget() == null || !this.isAggressive()){
            this.entityData.set(DATA_ANGRY_SHAKING, false);
        } else {
            this.entityData.set(DATA_ANGRY_SHAKING, true);
        }
    }

    @Override
    public boolean isConverting() {
        return false;
    }

//    @Override
//    protected boolean shouldZombify() {
//        return false;
//    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player player) {
            //Never attack creative or spectator
            if(player.isCreative() || player.isSpectator()){
                return false;
            }

            // Never attack players who are safe with diamonds
            if (this.provoked){
                return true;
            }
            //If not provoked, only attack players who are NOT safe with diamonds
            return !isPlayerSafeFromBreem(player);
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof Player player) {
            //Ignore creative and specators players
            if(player.isCreative() || player.isSpectator()){
                super.setTarget(null);
                return;
            }
            //If not provoked yet, only target players who are not safe
            if (!this.provoked && isPlayerSafeFromBreem(player)){
                super.setTarget(null);
                return;
            }
        }
        super.setTarget(target);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (canDiamondBarter(player, stack)) {
            if(!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }


                //Calm them down when diamond is offered
                this.provoked = false;
                this.setTarget(null);
                //Start head animation inspect
                this.startInspectAnimation();
                //Wait before giving reward
                this.barterCooldownTicks = 40;

                //doSimpleDiamondBarter((ServerLevel) this.level());

                this.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);

            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    private boolean canDiamondBarter(Player player, ItemStack stack) {
        return !this.isBaby()
                && stack.is(Items.DIAMOND)
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

        this.spawnAtLocation(reward);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

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
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        this.setCanPickUpLoot(false);
        this.setBaby(false);
        return data;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Piglin.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public void startInspectAnimation(){
        this.inspectAnimationTicks = 40;
        this.entityData.set(DATA_INSPECTING, true);

        //Show diamond in hand
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND));
    }
    public boolean isInspectAnimationActive(){
        return this.entityData.get(DATA_INSPECTING);
    }
    public void startAngryShakeAnimation(){
        this.entityData.set(DATA_ANGRY_SHAKING, true);
    }
    public boolean isAngryShakeActive(){
        return  this.entityData.get(DATA_ANGRY_SHAKING);
    }


    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount){
        boolean result = super.hurt(source, amount);

        if (result) {
            this.provoked = true;
            this.startAngryShakeAnimation();
            this.setAggressive(true);

            if (source.getEntity() instanceof LivingEntity living) {
                super.setTarget(living);
                //Alert nearby Breems
                this.alertNearbyBreems(living);
            }
        }

        return result;
    }

    private void alertNearbyBreems (LivingEntity target){
        double radius = 12.0D;

        for (BreemEntity nearby : this.level().getEntitiesOfClass(
                BreemEntity.class,
                this.getBoundingBox().inflate(radius))){

            if (nearby != this){
                nearby.provoked = true;
                nearby.startAngryShakeAnimation();
                nearby.setAggressive(true);
                nearby.setTarget(target);
            }

        }
    }

}
