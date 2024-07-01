package com.aqupd.grizzlybear.ai;


import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;


public class GrizzlyBearFishGoal extends MoveToBlockGoal {
    private int upTick;
    private boolean fished = false;
    private int stayTick;
    public GrizzlyBearFishGoal(GrizzlyBearEntity mob, double speed, int range) {
        super(mob, speed, range,4);
        upTick = mob.getRandom().nextInt(20)+10;
        stayTick = mob.getRandom().nextInt(100)+40;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return ((world.getBlockState(pos).is(Blocks.WATER)) && world.isEmptyBlock(pos.above()));
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return 1;
    }


    @Override
    public boolean canUse() {
        return !this.mob.isBaby() && this.mob.getRandom().nextInt(6500)==1 && super.canUse();
    }

    @Override
    public void start() {
        upTick = mob.getRandom().nextInt(5)+10;
        stayTick = mob.getRandom().nextInt(40)+100;
        super.start();
    }
    
    @Override
    public boolean canContinueToUse() {
        if (fished) {
            fished = false;
            return false;
        }
        return super.canContinueToUse() ;
    }

    @Override
    public void tick() {
        if (this.isReachedTarget() && upTick <= 0){
            ((GrizzlyBearEntity)this.mob).setStanding(false);

            LootTable lootTable = this.mob.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
            LootParams lootParams = new LootParams.Builder((ServerLevel)this.mob.level()).withParameter(LootContextParams.ORIGIN, this.mob.position()).withParameter(LootContextParams.THIS_ENTITY, this.mob).create(LootContextParamSets.COMMAND);
            List<ItemStack> list = lootTable.getRandomItems(lootParams);

            for (ItemStack itemStack: list) {
                if (!itemStack.is(ItemTags.FISHES) || this.mob.getRandom().nextInt(3)==1) {
                    ItemEntity itemEntity = new ItemEntity(this.mob.level(), this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ(), itemStack);
                    double d = this.mob.getX() - this.blockPos.getX();
                    double e = this.mob.getY() - this.blockPos.getY();
                    double f = this.mob.getZ() - this.blockPos.getZ();
                    itemEntity.setDeltaMovement(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);
                    this.mob.level().addFreshEntity(itemEntity);
                    itemEntity.playSound(SoundEvents.ITEM_PICKUP,0.2F,2);
                }else {
                    this.mob.heal(2F);
                    for(int i = 0; i < 8; ++i) {
                        double x = this.mob.getX() +this.blockPos.getX();
                        double y = 2.25 + this.mob.getY();
                        double z = this.blockPos.getZ() + this.mob.getZ();
                        ((ServerLevel) this.mob.level()).sendParticles(ParticleTypes.END_ROD,x/2,y,z/2,1,0,-0.2F,0,0.2f);
                    }
                }
            }
            fished = true;
        } else if (this.isReachedTarget() ){
            if (stayTick <=0) {
                ((GrizzlyBearEntity) this.mob).setStanding(true);
                upTick--;
            }else {
                this.mob.getLookControl().setLookAt(blockPos.getX(),blockPos.getY(),blockPos.getZ());
                stayTick--;
            }
        }
        super.tick();
    }
}
