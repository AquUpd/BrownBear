package com.aqupd.grizzlybear;

import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import com.aqupd.grizzlybear.utils.AqConfig;
import com.aqupd.grizzlybear.utils.AqDebug;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import static com.aqupd.grizzlybear.utils.AqLogger.logInfo;

public class Main implements ModInitializer {

	int weight = AqConfig.INSTANCE.getNumberProperty("spawn.weight");
	int mingroup = AqConfig.INSTANCE.getNumberProperty("spawn.min");
	int maxgroup = AqConfig.INSTANCE.getNumberProperty("spawn.max");

	public static final EntityType<GrizzlyBearEntity> GRIZZLYBEAR = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			new ResourceLocation("aqupd", "grizzly_bear"),
			EntityType.Builder.of(GrizzlyBearEntity::new, MobCategory.CREATURE).sized(1.4f, 1.4f).build()
	);

	public static SoundEvent GRIZZLY_BEAR_AMBIENT = register("entity.grizzly_bear.ambient");
	public static SoundEvent GRIZZLY_BEAR_AMBIENT_BABY = register("entity.grizzly_bear.ambient_baby");
	public static SoundEvent GRIZZLY_BEAR_DEATH = register("entity.grizzly_bear.death");
	public static SoundEvent GRIZZLY_BEAR_HURT = register("entity.grizzly_bear.hurt");
	public static SoundEvent GRIZZLY_BEAR_STEP = register("entity.grizzly_bear.step");
	public static SoundEvent GRIZZLY_BEAR_WARNING = register("entity.grizzly_bear.warning");

	public static final SpawnEggItem GRIZZLY_BEAR_SPAWN_EGG = new SpawnEggItem(GRIZZLYBEAR, 8545340, 4139806, new Item.Properties().stacksTo(64));

	public static final TagKey<Biome> GRIZZLY_BEAR_SPAWN_BIOMES = TagKey.create(Registries.BIOME, new ResourceLocation("aqupd", "grizzly_bear_spawning_biomes"));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> AqDebug.INSTANCE.startDebug(AqConfig.INSTANCE.getBooleanProperty("debug")));

		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("aqupd", "grizzly_bear_spawn_egg"), GRIZZLY_BEAR_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(GRIZZLYBEAR, GrizzlyBearEntity.createGrizzlyBearAttributes());

		BiomeModifications.addSpawn(
			biomeSelectionContext -> biomeSelectionContext.hasTag(GRIZZLY_BEAR_SPAWN_BIOMES),
				MobCategory.CREATURE, GRIZZLYBEAR, weight, mingroup, maxgroup
		);

		SpawnPlacements.register(GRIZZLYBEAR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(GRIZZLY_BEAR_SPAWN_EGG));
		logInfo("Grizzly Bears mod is loaded!");
	}

	private static SoundEvent register(String id) {
		SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation("aqupd", id));
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, soundEvent);
	}
}
