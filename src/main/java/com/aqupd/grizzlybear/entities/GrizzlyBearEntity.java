//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aqupd.grizzlybear.entities;

import com.aqupd.grizzlybear.Main;
import com.aqupd.grizzlybear.ai.GrizzlyBearFishGoal;
import com.aqupd.grizzlybear.utils.AqConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GrizzlyBearEntity extends Animal implements NeutralMob {
    private static final EntityDataAccessor<Boolean> WARNING;
    private float lastWarningAnimationProgress;
    private float warningAnimationProgress;
    private int warningSoundCooldown;
    private static final UniformInt ANGER_TIME_RANGE;
    private static final Ingredient LOVINGFOOD;
    private int angerTime;
    private UUID targetUuid;

    private static final double health = AqConfig.INSTANCE.getDoubleProperty("entity.health");
    private static final double speed = AqConfig.INSTANCE.getDoubleProperty("entity.speed");
    private static final double follow = AqConfig.INSTANCE.getDoubleProperty("entity.follow");
    private static final double damage = AqConfig.INSTANCE.getDoubleProperty("entity.damage");
    private static final int angermin = AqConfig.INSTANCE.getNumberProperty("entity.angertimemin");
    private static final int angermax = AqConfig.INSTANCE.getNumberProperty("entity.angertimemax");
    private static final boolean friendly = AqConfig.INSTANCE.getBooleanProperty("entity.friendlytoplayer");

    public GrizzlyBearEntity(EntityType<? extends GrizzlyBearEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
        return Main.GRIZZLYBEAR.create(level);
    }

    public boolean isFood(ItemStack stack) {
        return LOVINGFOOD.test(stack);
    }

    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean bl = this.isFood(player.getItemInHand(hand));
        if (!bl && !player.isSecondaryUseActive()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GrizzlyBearEntity.AttackGoal());
        this.goalSelector.addGoal(1, new GrizzlyBearEntity.GrizzlyBearEscapeDangerGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, LOVINGFOOD, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new GrizzlyBearFishGoal(this,1.0D,20));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new GrizzlyBearEntity.GrizzlyBearRevengeGoal());
        if (!friendly) {
            this.targetSelector.addGoal(2, new GrizzlyBearEntity.ProtectBabiesGoal());
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Fox.class, 10, true, true, null));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Rabbit.class, 10, true, true, null));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Chicken.class, 10, true, true, null));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Bee.class, 10, true, true, null));
            this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
        }
    }

    public static Builder createGrizzlyBearAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.FOLLOW_RANGE, follow)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, damage);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.readPersistentAngerSaveData(this.level(), nbt);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }


    public void setRemainingPersistentAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.targetUuid = uuid;
    }

    public UUID getPersistentAngerTarget() {
        return this.targetUuid;
    }

    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? Main.GRIZZLY_BEAR_AMBIENT_BABY : Main.GRIZZLY_BEAR_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return Main.GRIZZLY_BEAR_HURT;
    }

    protected SoundEvent getDeathSound() {
        return Main.GRIZZLY_BEAR_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(Main.GRIZZLY_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundCooldown <= 0) {
            this.playSound(Main.GRIZZLY_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundCooldown = 40;
        }

    }
    
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(WARNING, false);
        super.defineSynchedData(builder);
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
                this.refreshDimensions();
            }

            this.lastWarningAnimationProgress = this.warningAnimationProgress;
            if (this.isStanding()) {
                this.warningAnimationProgress = Mth.clamp(this.warningAnimationProgress + 1.0F, 0.0F, 6.0F);
            } else {
                this.warningAnimationProgress = Mth.clamp(this.warningAnimationProgress - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundCooldown > 0) {
            --this.warningSoundCooldown;
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

    }
    
    public @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        if (this.warningAnimationProgress > 0.0F) {
            float f = this.warningAnimationProgress / 6.0F;
            float g = 1.0F + f;
            return super.getDefaultDimensions(pose).scale(1.0F, g);
        } else {
            return super.getDefaultDimensions(pose);
        }
    }

    public boolean isStanding() {
        return this.entityData.get(WARNING);
    }

    public void setStanding(boolean warning) {
        this.entityData.set(WARNING, warning);
    }

    @Environment(EnvType.CLIENT)
    public float getStandingAnimationScale(float tickDelta) {
        return Mth.lerp(tickDelta, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0F;
    }

    protected float getWaterSlowDown() {
        return 0.98F;
    }
    
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgeableMob.AgeableMobGroupData(1.0F);
        }
        
        return super.finalizeSpawn(level, difficulty, spawnType, (SpawnGroupData)spawnGroupData);
    }

    static {
        WARNING = SynchedEntityData.defineId(GrizzlyBearEntity.class, EntityDataSerializers.BOOLEAN);
        ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(angermin, angermax);
        LOVINGFOOD = Ingredient.of(Items.COD, Items.SALMON, Items.SWEET_BERRIES);
    }

    class GrizzlyBearEscapeDangerGoal extends PanicGoal {
        public GrizzlyBearEscapeDangerGoal() {
            super(GrizzlyBearEntity.this, 2.0D);
        }

        public boolean canUse() {
            return (GrizzlyBearEntity.this.isBaby() || GrizzlyBearEntity.this.isOnFire()) && super.canUse();
        }
    }

    private class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(GrizzlyBearEntity.this, 1.25D, true);
        }
        
        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            double d = this.getAttackReachSqr(target);
            var squaredDistance = target.distanceToSqr(this.mob);
            
            if (squaredDistance <= d && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(target);
                GrizzlyBearEntity.this.setStanding(false);
            } else if (squaredDistance <= d * 2.0D) {
                if (this.isTimeToAttack()) {
                    GrizzlyBearEntity.this.setStanding(false);
                    this.resetAttackCooldown();
                }
                
                if (this.getTicksUntilNextAttack() <= 10) {
                    GrizzlyBearEntity.this.setStanding(true);
                    GrizzlyBearEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                GrizzlyBearEntity.this.setStanding(false);
            }
        }

        public void stop() {
            GrizzlyBearEntity.this.setStanding(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity entity) {
            return 4.0F + entity.getBbWidth();
        }
    }

    class GrizzlyBearRevengeGoal extends HurtByTargetGoal {
        public GrizzlyBearRevengeGoal() {
            super(GrizzlyBearEntity.this);
        }

        public void start() {
            super.start();
            if (GrizzlyBearEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(Mob mob, LivingEntity target) {
            if (mob instanceof GrizzlyBearEntity && !mob.isBaby()) {
                super.alertOther(mob, target);
            }

        }
    }

    class ProtectBabiesGoal extends NearestAttackableTargetGoal<Player> {
        public ProtectBabiesGoal() {
            super(GrizzlyBearEntity.this, Player.class, 20, true, true, null);
        }

        public boolean canUse() {
            if (!GrizzlyBearEntity.this.isBaby()) {
                if (super.canUse()) {
                    List<GrizzlyBearEntity> list = GrizzlyBearEntity.this.level().getEntitiesOfClass(GrizzlyBearEntity.class, GrizzlyBearEntity.this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));

                    for (GrizzlyBearEntity grizzlyBearEntity : list) {
                        if (grizzlyBearEntity.isBaby()) {
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }
}
