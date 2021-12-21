package com.bottomtextdanny.effective_fg.mixin.client;

import com.bottomtextdanny.effective_fg.EffectiveFg;
import com.bottomtextdanny.effective_fg.client.config.EffectiveConfig;
import com.bottomtextdanny.effective_fg.registry.ParticleRegistry;
import com.bottomtextdanny.effective_fg.registry.SoundEventRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.audio.SoundSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public World level;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract double getY();

    @Shadow public abstract float getBbWidth();

    @Shadow @Final protected Random random;

    @Shadow public abstract List<Entity> getPassengers();

    public Entity getFirstPassenger() {
        return this.passengers.isEmpty() ? null : (Entity)this.passengers.get(0);
    }

    @Shadow protected boolean onGround;

    @Shadow public abstract boolean isInWater();

    @Shadow public abstract boolean isInLava();

    @Shadow public abstract BlockPos blockPosition();

    @Shadow public abstract Vector3d getDeltaMovement();

    @Shadow @Final private List<Entity> passengers;

    @Inject(method = "doWaterSplashEffect", at = @At("TAIL"))
    protected void onSwimmingStart(CallbackInfo callbackInfo) {
        if (!EffectiveConfig.enableEffects.get())
            return;

        if (this.level.isClientSide) {
            Entity entity = !this.getPassengers().isEmpty() && this.getFirstPassenger() != null ? this.getFirstPassenger() : (Entity) (Object) this;
            float f = entity == (Object) this ? 0.2f : 0.9f;
            Vector3d vec3d = entity.getDeltaMovement();
            float g = Math.min(1.0f, (float) Math.sqrt(vec3d.x * vec3d.x * (double) 0.2f + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double) 0.2f) * f);
            if (g > EffectiveFg.SPLASH_SPEED_WATER_THRESHOLD) {
                for (int i = -10; i < 10; i++) {
                    if (this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i, this.getZ())).getBlock() == Blocks.WATER && this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i, this.getZ())).getFluidState().isSource() && this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i + 1, this.getZ())).isAir()) {
                        this.level.playLocalSound(this.getX(), Math.round(this.getY()) + i + 0.9f, this.getZ(), SoundEventRegistry.AMBIENCE_SPLASH.get(), SoundCategory.AMBIENT, g*EffectiveFg.SPLASH_WATER_VOLUME_FACTOR, 0.8f, true);
                        this.level.addParticle(ParticleRegistry.SPLASH.get(), this.getX(), Math.round(this.getY()) + i + 0.9f, this.getZ(), 0, 0, 0);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < this.getBbWidth() * EffectiveFg.SPLASH_WATER_DROPLET_FACTOR; i++) {
                    this.level.addParticle(ParticleRegistry.DROPLET.get(), this.getX() + random.nextGaussian() * this.getBbWidth() / 5f, this.getY(), this.getZ() + random.nextGaussian() * this.getBbWidth(), random.nextGaussian() / 15f, random.nextFloat() / 2.5f, random.nextGaussian() / 15f);
                }
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEventRegistry.AMBIENCE_SMALL_SPLASH.get(), SoundCategory.AMBIENT, g*EffectiveFg.SMALL_SPLASH_WATER_VOLUME_FACTOR, 1.0f, false);
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo callbackInfo) {
        if (!EffectiveConfig.enableEffects.get())
            return;

        if (!this.onGround && !this.isInWater() && !this.isInLava() && level.getBlockState(this.blockPosition().offset(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z)).getBlock() == Blocks.LAVA) {
            if (this.level.isClientSide) {
                Entity entity = !this.getPassengers().isEmpty() && this.getFirstPassenger() != null ? this.getFirstPassenger() : (Entity) (Object) this;
                float f = entity == (Object) this ? 0.2f : 0.9f;
                Vector3d vec3d = entity.getDeltaMovement();
                float g = Math.min(1.0f, (float) Math.sqrt(vec3d.x * vec3d.x * (double) 0.2f + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double) 0.2f) * f);
                //System.out.println(g);
                if (g > EffectiveFg.SPLASH_SPEED_LAVA_THRESHOLD) {
                    for (int i = -10; i < 10; i++) {
                        if (this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i, this.getZ())).getBlock() == Blocks.LAVA && this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i, this.getZ())).getFluidState().isSource() && this.level.getBlockState(new BlockPos(this.getX(), Math.round(this.getY()) + i + 1, this.getZ())).isAir()) {
                            this.level.playLocalSound(this.getX(), Math.round(this.getY()) + i + 0.9f, this.getZ(), SoundEvents.BUCKET_EMPTY_LAVA, SoundCategory.AMBIENT, 1.0f, 0.8f, true);
                            this.level.addParticle(ParticleRegistry.LAVA_SPLASH.get(), this.getX(), Math.round(this.getY()) + i + 0.9f, this.getZ(), 0, 0, 0);
                            break;
                        }
                    }
                }
            }
        }
    }
}
