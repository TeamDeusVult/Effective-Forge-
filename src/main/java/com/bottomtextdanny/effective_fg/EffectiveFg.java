package com.bottomtextdanny.effective_fg;

import com.bottomtextdanny.effective_fg.client.WorldTickHandler;
import com.bottomtextdanny.effective_fg.client.config.EffectiveConfig;
import com.bottomtextdanny.effective_fg.registry.ParticleRegistry;
import com.bottomtextdanny.effective_fg.registry.SoundEventRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(EffectiveFg.ID)
public class EffectiveFg {
    public static final int WATERFALL_PARTICLE_RARITY = 3;
    public static final int WATERFALL_PARTICLE_SOUND_RARITY = 9;
    public static final float FLOWING_WATER_SHOULD_BEHAVE_AT_HEIGHT = 0.77F;
    public static final int FLOWING_WATER_DROP_RARITY = 50;
    public static final float SPLASH_WATER_VOLUME_FACTOR = 10.0F;
    public static final float SMALL_SPLASH_WATER_VOLUME_FACTOR = 5.0F;
    public static final float SPLASH_WATER_DROPLET_FACTOR = 25.0F;
    public static final float SPLASH_SPEED_WATER_THRESHOLD = 0.1F;
    public static final float SPLASH_SPEED_LAVA_THRESHOLD = 0.05F;
    public static final String ID = "effective_fg";

    public EffectiveFg() {
        EffectiveConfig.loadConfig(FMLPaths.CONFIGDIR.get().resolve("effective.toml"));

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(WorldTickHandler::worldTickLast);
        SoundEventRegistry.ENTRIES.register(modEventBus);
        ParticleRegistry.ENTRIES.register(modEventBus);
    }
}
