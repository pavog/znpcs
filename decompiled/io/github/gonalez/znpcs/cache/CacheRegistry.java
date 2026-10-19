package io.github.gonalez.znpcs.cache;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.github.gonalez.znpcs.utility.Utils;
import io.github.gonalez.znpcs.utility.Version;
import io.github.gonalez.znpcs.utility.VersionRange;
import io.netty.channel.Channel;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public final class CacheRegistry {
   public static final Class<?> PACKET_PLAY_IN_USE_ENTITY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName("PacketPlayInUseEntity")
      )
      .load();
   public static final Class<?> ENUM_PLAYER_INFO_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.PACKET)
            .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
            .withClassName("ClientboundPlayerInfoUpdatePacket$a")
      )
      .load();
   public static final Class<?> PACKET_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PROTOCOL).withClassName("Packet")
      )
      .load();
   public static final Class<?> ENTITY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withClassName("Entity")
      )
      .load();
   public static final Class<?> ENTITY_LIVING = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withClassName("EntityLiving")
      )
      .load();
   public static final Class<?> ENTITY_PLAYER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER_LEVEL).withClassName("EntityPlayer")
      )
      .load();
   public static final Class<?> ENTITY_ARMOR_STAND_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("decoration")
            .withClassName("EntityArmorStand")
      )
      .load();
   public static final Class<?> ENTITY_BAT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("ambient").withClassName("EntityBat")
      )
      .load();
   public static final Class<?> ENTITY_BLAZE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityBlaze")
      )
      .load();
   public static final Class<?> ENTITY_CAVE_SPIDER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityCaveSpider")
      )
      .load();
   public static final Class<?> ENTITY_CHICKEN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityChicken")
      )
      .load();
   public static final Class<?> ENTITY_COW_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityCow")
      )
      .load();
   public static final Class<?> ENTITY_CAT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityCat")
      )
      .load();
   public static final Class<?> ENTITY_CREEPER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityCreeper")
      )
      .load();
   public static final Class<?> ENTITY_ENDER_DRAGON_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("boss.enderdragon")
            .withClassName("EntityEnderDragon")
      )
      .load();
   public static final Class<?> ENTITY_ENDERMAN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityEnderman")
      )
      .load();
   public static final Class<?> ENTITY_HUMAN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("player").withClassName("EntityHuman")
      )
      .load();
   public static final Class<?> ENTITY_ENDERMITE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityEndermite")
      )
      .load();
   public static final Class<?> ENTITY_GHAST_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityGhast")
      )
      .load();
   public static final Class<?> ENTITY_IRON_GOLEM_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityIronGolem")
      )
      .load();
   public static final Class<?> ENTITY_GIANT_ZOMBIE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityGiantZombie")
      )
      .load();
   public static final Class<?> ENTITY_GUARDIAN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityGuardian")
      )
      .load();
   public static final Class<?> ENTITY_HORSE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal.horse")
            .withClassName("EntityHorse")
      )
      .load();
   public static final Class<?> ENTITY_LLAMA_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal.horse")
            .withClassName("EntityLlama")
      )
      .load();
   public static final Class<?> ENTITY_MAGMA_CUBE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityMagmaCube")
      )
      .load();
   public static final Class<?> ENTITY_MUSHROOM_COW_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityMushroomCow")
      )
      .load();
   public static final Class<?> ENTITY_OCELOT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityOcelot")
      )
      .load();
   public static final Class<?> ENTITY_TURTLE = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityTurtle")
      )
      .load();
   public static final Class<?> ENTITY_WARDEN = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster.warden")
            .withClassName("Warden")
      )
      .load();
   public static final Class<?> ENTITY_BEE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityBee")
      )
      .load();
   public static final Class<?> ENTITY_PARROT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityParrot")
      )
      .load();
   public static final Class<?> ENTITY_PIG_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityPig")
      )
      .load();
   public static final Class<?> ENTITY_RABBIT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityRabbit")
      )
      .load();
   public static final Class<?> ENTITY_POLAR_BEAR_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntityPolarBear")
      )
      .load();
   public static final Class<?> ENTITY_PANDA_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityPanda")
      )
      .load();
   public static final Class<?> ENTITY_SHEEP_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntitySheep")
      )
      .load();
   public static final Class<?> ENTITY_SNOWMAN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal")
            .withClassName("EntitySnowman")
      )
      .load();
   public static final Class<?> ENTITY_SHULKER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityShulker")
      )
      .load();
   public static final Class<?> ENTITY_SILVERFISH_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntitySilverfish")
      )
      .load();
   public static final Class<?> ENTITY_SKELETON_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntitySkeleton")
      )
      .load();
   public static final Class<?> ENTITY_SLIME_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntitySlime")
      )
      .load();
   public static final Class<?> ENTITY_SPIDER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntitySpider")
      )
      .load();
   public static final Class<?> ENTITY_SQUID_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntitySquid")
      )
      .load();
   public static final Class<?> ENTITY_VILLAGER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("npc").withClassName("EntityVillager")
      )
      .load();
   public static final Class<?> ENTITY_WITCH_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityWitch")
      )
      .load();
   public static final Class<?> ENTITY_WITHER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("boss.wither")
            .withClassName("EntityWither")
      )
      .load();
   public static final Class<?> ENTITY_ZOMBIE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster")
            .withClassName("EntityZombie")
      )
      .load();
   public static final Class<?> ENTITY_WOLF_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityWolf")
      )
      .load();
   public static final Class<?> ENTITY_AXOLOTL_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("animal.axolotl")
            .withClassName("Axolotl")
      )
      .load();
   public static final Class<?> ENTITY_GOAT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal.goat").withClassName("Goat")
      )
      .load();
   public static final Class<?> ENTITY_FOX_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal").withClassName("EntityFox")
      )
      .load();
   public static final Class<?> ENTITY_TYPES_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withClassName("EntityTypes")
            .withClassName("EntityType")
            .acceptFirst()
      )
      .load();
   public static final Class<?> ENUM_CHAT_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withClassName("EnumChatFormat")
      )
      .load();
   public static final Class<?> ENUM_ITEM_SLOT = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withClassName("EnumItemSlot")
      )
      .load();
   public static final Class<?> I_CHAT_BASE_COMPONENT = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.CHAT).withClassName("IChatBaseComponent")
      )
      .load();
   public static final Class<?> ITEM_STACK_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ITEM).withClassName("ItemStack")
      )
      .load();
   public static final Class<?> DATA_WATCHER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SYNCHER).withClassName("DataWatcher")
      )
      .load();
   public static final Class<?> DATA_WATCHER_OBJECT = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SYNCHER).withClassName("DataWatcherObject")
      )
      .load();
   public static final Class<?> DATA_WATCHER_REGISTRY = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SYNCHER).withClassName("DataWatcherRegistry")
      )
      .load();
   public static final Class<?> DATA_WATCHER_SERIALIZER = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SYNCHER).withClassName("DataWatcherSerializer")
      )
      .load();
   public static final Class<?> WORLD_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.WORLD_LEVEL).withClassName("World")
      )
      .load();
   public static final Class<?> CRAFT_ITEM_STACK_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("inventory.CraftItemStack")
      )
      .load();
   public static final Class<?> WORLD_SERVER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER_LEVEL).withClassName("WorldServer")
      )
      .load();
   public static final Class<?> MINECRAFT_SERVER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER).withClassName("MinecraftServer")
      )
      .load();
   public static final Class<?> PLAYER_INTERACT_MANAGER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER_LEVEL).withClassName("PlayerInteractManager")
      )
      .load();
   public static final Class<?> PLAYER_CONNECTION_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER_NETWORK).withClassName("PlayerConnection")
      )
      .load();
   public static final Class<?> NETWORK_MANAGER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.NETWORK).withClassName("NetworkManager")
      )
      .load();
   public static final Class<?> PACKET_PLAY_OUT_PLAYER_INFO_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.PACKET)
            .withClassName("PacketPlayOutPlayerInfo")
            .withClassName("ClientboundPlayerInfoUpdatePacket")
      )
      .load();
   public static final Class<?> PACKET_PLAY_OUT_PLAYER_INFO_ENTRY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName("ClientboundPlayerInfoUpdatePacket$c")
      )
      .load();
   public static final Class<?> PACKET_PLAY_OUT_PLAYER_INFO_REMOVE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName("ClientboundPlayerInfoRemovePacket")
      )
      .load();
   public static final Class<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName("PacketPlayOutScoreboardTeam")
      )
      .load();
   public static final Class<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName("PacketPlayOutEntityDestroy")
      )
      .load();
   public static final Class<?> ENTITY_ALLAY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal.allay").withClassName("Allay")
      )
      .load();
   public static final Class<?> ENTITY_CAMEL_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal.camel").withClassName("Camel")
      )
      .load();
   public static final Class<?> ENTITY_PIGLIN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster.piglin")
            .withClassName("EntityPiglin")
      )
      .load();
   public static final Class<?> ENTITY_HOGLIN_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ENTITY)
            .withAdditionalData("monster.hoglin")
            .withClassName("EntityHoglin")
      )
      .load();
   public static final Class<?> ENTITY_FROG_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withAdditionalData("animal.frog").withClassName("Frog  ")
      )
      .load();
   public static final Class<?> SCOREBOARD_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.WORLD_SCORES).withClassName("Scoreboard")
      )
      .load();
   public static final Class<?> SCOREBOARD_TEAM_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.WORLD_SCORES).withClassName("ScoreboardTeam")
      )
      .load();
   public static final Class<?> ENUM_TAG_VISIBILITY = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.WORLD_SCORES)
            .withClassName("ScoreboardTeamBase$EnumNameTagVisibility")
      )
      .load();
   public static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("util.CraftChatMessage")
      )
      .load();
   public static final Class<?> PROFILE_PUBLIC_KEY_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.WORLD_ENTITY_PLAYER).withClassName("ProfilePublicKey")
      )
      .load();
   public static final Class<?> CLIENT_INFORMATION_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.SERVER_LEVEL).withClassName("ClientInformation")
      )
      .load();
   public static final Class<?> CraftEntityType_Class = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withCategory(CacheCategory.CRAFT_ENTITY).withClassName("entity.CraftEntityType")
      )
      .load();
   public static final Class<?> Vec3_Class = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PHYS).withClassName("Vec3D").acceptFirst()
      )
      .load();
   public static final Class<?> HOLDER_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.CORE).withClassName("Holder")
      )
      .load();
   public static final Class<?> ATTRIBUTE_INSTANCE_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.ATTRIBUTES)
            .withClassName("AttributeInstance")
            .withClassName("AttributeModifiable")
      )
      .load();
   public static final TypeCache.BaseCache<Constructor<?>> GAMEPROFILE_NEW_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(GameProfile.class)
         .withParameterTypes(UUID.class, String.class, PropertyMap.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PROPERTY_MAP_NEW_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PropertyMap.class)
         .withParameterTypes(Multimap.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> SCOREBOARD_TEAM_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(SCOREBOARD_TEAM_CLASS)
         .withParameterTypes(SCOREBOARD_CLASS, String.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_CONSTRUCTOR_OLD = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, PLAYER_INTERACT_MANAGER_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_CONSTRUCTOR_NEW = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_CONSTRUCTOR_NEW_1 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, PROFILE_PUBLIC_KEY_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_CONSTRUCTOR_NEW_2 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_CONSTRUCTOR_NEW_3 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, CLIENT_INFORMATION_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_PLAYER_INFO_CLASS)
         .withParameterTypes(ENUM_PLAYER_INFO_CLASS, Utils.isVersionSameOrNew(17) ? Collection.class : Iterable.class)
         .withParameterTypes(ENUM_PLAYER_INFO_CLASS, ENTITY_PLAYER_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR_V2 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_PLAYER_INFO_CLASS)
         .withParameterTypes(EnumSet.class, Collection.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_PLAYER_INFO_REMOVE_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_PLAYER_INFO_REMOVE_CLASS)
         .withParameterTypes(List.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntity$PacketPlayOutEntityLook")
         .withParameterTypes(int.class, byte.class, byte.class, boolean.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityHeadRotation")
         .withParameterTypes(ENTITY_CLASS, byte.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityTeleport")
         .withParameterTypes(ENTITY_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityMetadata")
         .withParameterTypes(int.class, DATA_WATCHER_CLASS, boolean.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR_V1 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityMetadata")
         .withParameterTypes(int.class, List.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutNamedEntitySpawn")
         .withParameterTypes(ENTITY_HUMAN_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS)
         .withParameterTypes(int.class)
         .withParameterTypes(int[].class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutSpawnEntity")
         .withClassName("PacketPlayOutSpawnEntityLiving")
         .withParameterTypes(ENTITY_LIVING)
         .withParameterTypes(ENTITY_CLASS)
         .withParameterTypes(
            int.class, UUID.class, double.class, double.class, double.class, float.class, float.class, ENTITY_TYPES_CLASS, int.class, Vec3_Class, double.class
         )
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PlayerInteractManager")
         .withParameterTypes(WORLD_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PlayerInteractManager")
         .withParameterTypes(WORLD_SERVER_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityEquipment")
         .withParameterTypes(int.class, int.class, ITEM_STACK_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityEquipment")
         .withParameterTypes(int.class, ENUM_ITEM_SLOT, ITEM_STACK_CLASS)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityEquipment")
         .withParameterTypes(int.class, List.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.CHAT)
         .withClassName("ChatComponentText")
         .withParameterTypes(String.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> ENTITY_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_ARMOR_STAND_CLASS)
         .withParameterTypes(WORLD_CLASS, double.class, double.class, double.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> DATA_WATCHER_OBJECT_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(DATA_WATCHER_OBJECT)
         .withParameterTypes(int.class, DATA_WATCHER_SERIALIZER)
   );
   public static final TypeCache.BaseCache<Method> AS_NMS_COPY_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT)
         .withClassName("inventory.CraftItemStack")
         .withMethodName("asNMSCopy")
         .withParameterTypes(ItemStack.class)
   );
   public static final TypeCache.BaseCache<Method> GAME_PROFILE_NAME_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(GameProfile.class)
         .withMethodName("getName")
         .withMethodName("name")
   );
   public static final TypeCache.BaseCache<Method> GAME_PROFILE_ID_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(GameProfile.class)
         .withMethodName("getId")
         .withMethodName("id")
   );
   public static final TypeCache.BaseCache<Method> GAME_PROFILE_PROPERTIES_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(GameProfile.class)
         .withMethodName("properties")
         .withMethodName("getProperties")
   );
   public static final TypeCache.BaseCache<Method> GET_PROFILE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(ENTITY_HUMAN_CLASS)
         .withExpectResult(GameProfile.class)
   );
   public static final TypeCache.BaseCache<Field> SET_PROFILE_METHOD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(ENTITY_HUMAN_CLASS)
         .withExpectResult(GameProfile.class)
   );
   public static final TypeCache.BaseCache<Method> GET_ENTITY_ID = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("getId")
         .withMethodName("al", VersionRange.of(Version.fromString("1.20.5")), VersionRange.of(Version.fromString("1.20.6")))
         .withMethodName("ah", VersionRange.of(Version.fromString("1.20.2")), VersionRange.of(Version.fromString("1.21.1-R0.1-SNAPSHOT")))
         .withMethodName("an", VersionRange.of(Version.fromString("1.21-R0.1-SNAPSHOT"), Version.fromString("1.21.1-R0.1-SNAPSHOT")))
         .withMethodName("af")
         .withMethodName("aj")
         .withMethodName("ar")
         .withMethodName("ae")
         .withMethodName("ao")
         .withExpectResult(int.class)
   );
   public static final TypeCache.BaseCache<Method> GET_HANDLE_PLAYER_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT)
         .withClassName("entity.CraftPlayer")
         .withClassName("entity.CraftHumanEntity")
         .withMethodName("getHandle")
   );
   public static final TypeCache.BaseCache<Method> GET_HANDLE_WORLD_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("CraftWorld").withMethodName("getHandle")
   );
   public static final TypeCache.BaseCache<Method> GET_SERVER_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("CraftServer").withMethodName("getServer")
   );
   public static final TypeCache.BaseCache<Method> SEND_PACKET_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PLAYER_CONNECTION_CLASS)
         .withMethodName("sendPacket")
         .withMethodName("b")
         .withMethodName("a")
         .withMethodName("send")
         .withParameterTypes(PACKET_CLASS)
   );
   public static final TypeCache.BaseCache<Method> SET_CUSTOM_NAME_OLD_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("setCustomName")
         .withParameterTypes(String.class)
   );
   public static final TypeCache.BaseCache<Method> SET_CUSTOM_NAME_NEW_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("b")
         .withMethodName("setCustomName")
         .withParameterTypes(I_CHAT_BASE_COMPONENT)
   );
   public static final TypeCache.BaseCache<Method> GET_BUKKIT_ENTITY_ID_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName(Entity.class).withMethodName("getEntityId")
   );
   public static final TypeCache.BaseCache<Method> SET_CUSTOM_NAME_VISIBLE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("setCustomNameVisible")
         .withMethodName(
            "o", VersionRange.of(Version.fromString("1.20.5")), VersionRange.of(Version.fromString("1.20.6")), VersionRange.of(Version.fromString("1.21.5"))
         )
         .withMethodName("p")
         .withMethodName("n")
         .withParameterTypes(boolean.class)
   );
   public static final TypeCache.BaseCache<Method> SET_INVISIBLE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_ARMOR_STAND_CLASS)
         .withMethodName("setInvisible")
         .withMethodName("l", Utils.isPaper() && VersionRange.of(Version.fromString("1.21.6")).contains())
         .withMethodName("k")
         .withMethodName(
            "j", VersionRange.of(Version.fromString("1.20"), Version.fromString("1.20.2")), VersionRange.of(Version.fromString("1.20.4-R0.1-SNAPSHOT"))
         )
         .withMethodName("l")
         .withParameterTypes(boolean.class)
   );
   public static final TypeCache.BaseCache<Method> SET_LOCATION_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("setPositionRotation")
         .withMethodName("absMoveTo")
         .withMethodName("a")
         .withMethodName("absSnapTo")
         .withParameterTypes(double.class, double.class, double.class, float.class, float.class)
   );
   public static final TypeCache.BaseCache<Method> SET_DATA_WATCHER_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(DATA_WATCHER_CLASS)
         .withMethodName("set")
         .withMethodName("b")
         .withMethodName("c")
         .withMethodName("a")
         .withExpectResult(void.class)
         .withParameterTypes(DATA_WATCHER_OBJECT, Object.class)
         .acceptFirst()
   );
   public static final TypeCache.BaseCache<Method> WATCH_DATA_WATCHER_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(DATA_WATCHER_CLASS)
         .withMethodName("watch")
         .withParameterTypes(int.class, Object.class)
   );
   public static final TypeCache.BaseCache<Method> GET_DATA_WATCHER_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("entityData")
         .withMethodName("aj")
         .withMethodName("al")
         .withMethodName("an")
         .withMethodName("getDataWatcher")
         .withExpectResult(DATA_WATCHER_CLASS)
   );
   public static final TypeCache.BaseCache<Method> GET_BUKKIT_ENTITY_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withMethodName("getBukkitEntity")
   );
   public static final TypeCache.BaseCache<Method> GET_BUKKIT_ENTITY_TYPE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName(Entity.class).withMethodName("getType")
   );
   public static final TypeCache.BaseCache<Method> GET_ENUM_CHAT_ID_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName(ENUM_CHAT_CLASS).withMethodName("b")
   );
   public static final TypeCache.BaseCache<Method> ENUM_CHAT_TO_STRING_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENUM_CHAT_CLASS)
         .withExpectResult(String.class)
         .withMethodName("toString")
   );
   public static final TypeCache.BaseCache<Method> ENTITY_TYPES_A_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.ENTITY)
         .withClassName(ENTITY_TYPES_CLASS)
         .withMethodName("a")
         .withMethodName("byString")
         .withParameterTypes(String.class)
   );
   public static final TypeCache.BaseCache<Method> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1 = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)
         .withMethodName("createRemovePacket")
         .withMethodName("a")
         .withParameterTypes(SCOREBOARD_TEAM_CLASS)
   );
   public static final TypeCache.BaseCache<Method> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)
         .withMethodName("a")
         .withMethodName("createAddOrModifyPacket")
         .withParameterTypes(SCOREBOARD_TEAM_CLASS, boolean.class)
   );
   public static final TypeCache.BaseCache<Method> SCOREBOARD_PLAYER_LIST = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(SCOREBOARD_TEAM_CLASS)
         .withMethodName("getPlayerNameSet")
         .withMethodName("getPlayers")
         .withMethodName("h")
         .withMethodName("g")
         .withExpectResult(Collection.class)
   );
   public static final TypeCache.BaseCache<Method> ENUM_CHAT_FORMAT_FIND = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withClassName(ENUM_CHAT_CLASS)
         .withParameterTypes(String.class)
         .withExpectResult(ENUM_CHAT_CLASS)
         .acceptFirst()
   );
   public static final TypeCache.BaseCache<Method> CRAFT_CHAT_MESSAGE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT)
         .withClassName(CRAFT_CHAT_MESSAGE_CLASS)
         .withMethodName("fromStringOrNull")
         .withParameterTypes(String.class)
   );
   public static final TypeCache.BaseCache<Method> GET_UNIQUE_ID_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withClassName(ENTITY_CLASS).withExpectResult(UUID.class)
   );
   public static final TypeCache.BaseCache<Method> GET_DATAWATCHER_B_LIST = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withMethodName("c")
         .withMethodName("getNonDefaultValues")
         .withClassName(DATA_WATCHER_CLASS)
   );
   public static final TypeCache.BaseCache<Method> CREATE_DEFAULT_CLIENT_INFORMATION = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.SERVER_LEVEL)
         .withExpectResult(CLIENT_INFORMATION_CLASS)
         .withClassName(CLIENT_INFORMATION_CLASS)
   );
   public static final TypeCache.BaseCache<Method> CraftEntityType_bukkitToMinecraft_Method = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT)
         .withMethodName("bukkitToMinecraft")
         .withParameterTypes(EntityType.class)
         .withClassName(CraftEntityType_Class)
   );
   public static final TypeCache.BaseCache<Field> PLAYER_CONNECTION_FIELD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.SERVER_LEVEL)
         .withClassName(ENTITY_PLAYER_CLASS)
         .withExpectResult(PLAYER_CONNECTION_CLASS)
   );
   public static final TypeCache.BaseCache<Field> NETWORK_MANAGER_FIELD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PLAYER_CONNECTION_CLASS)
         .withExpectResult(NETWORK_MANAGER_CLASS)
   );
   public static final TypeCache.BaseCache<Field> CHANNEL_FIELD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.SERVER_NETWORK)
         .withClassName(NETWORK_MANAGER_CLASS)
         .withExpectResult(Channel.class)
   );
   public static final TypeCache.BaseCache<Field> PACKET_IN_USE_ENTITY_ID_FIELD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayInUseEntity")
         .withExpectResult(int.class)
   );
   public static final TypeCache.BaseCache<Field> DATAWATCHER_ENTITY_FIELD = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(ENTITY_CLASS)
         .withFieldName("entityData")
         .withFieldName("ao")
         .withFieldName("al")
         .withExpectResult(DATA_WATCHER_CLASS)
   );
   public static final TypeCache.BaseCache<Field> BUKKIT_COMMAND_MAP = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("CraftServer").withFieldName("commandMap")
   );
   public static final TypeCache.BaseCache<Field> PACKET_PLAY_OUT_INFO_LIST = new TypeCache.BaseCache.FieldLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName(PACKET_PLAY_OUT_PLAYER_INFO_CLASS)
         .withExpectResult(List.class)
   );
   public static final TypeCache.BaseCache<Object> ADD_PLAYER_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.PACKET)
            .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
            .withClassName("ClientboundPlayerInfoUpdatePacket$a")
            .withFieldName(Utils.isVersionSameOrNew(17) ? "a" : "ADD_PLAYER")
      )
      .asValueField();
   public static final TypeCache.BaseCache<Object> UPDATE_LISTED_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.PACKET)
            .withClassName("ClientboundPlayerInfoUpdatePacket$a")
            .withFieldName("d")
      )
      .asValueField();
   public static final TypeCache.BaseCache<Object> REMOVE_PLAYER_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withCategory(CacheCategory.PACKET)
            .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
            .withClassName("ClientboundPlayerInfoUpdatePacket$a")
            .withFieldName(Utils.isVersionSameOrNew(17) ? "e" : "REMOVE_PLAYER")
      )
      .asValueField();
   public static final TypeCache.BaseCache<Object> DATA_WATCHER_REGISTER_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName(DATA_WATCHER_REGISTRY).withFieldName("a")
      )
      .asValueField();
   public static final TypeCache.BaseCache<Object> ENUM_TAG_VISIBILITY_NEVER_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.PACKET).withClassName(ENUM_TAG_VISIBILITY).withFieldName("b")
      )
      .asValueField();
   public static final TypeCache.BaseCache<Object> VEC3D_FIELD = new TypeCache.BaseCache.FieldLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
            .withClassName(Vec3_Class)
            .withFieldName("ZERO")
            .withFieldName("b")
            .withFieldName("c")
            .withExpectResult(Vec3_Class)
            .acceptFirst()
      )
      .asValueField();
   public static final Class<?> PositionMoveRotation_CLASS = (Class<?>)new TypeCache.BaseCache.ClazzLoader(
         new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withCategory(CacheCategory.ENTITY).withClassName("PositionMoveRotation")
      )
      .load();
   public static final TypeCache.BaseCache<Method> PositionMoveRotation_ForEntity_Method = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.SERVER_LEVEL)
         .withMethodName("a")
         .withMethodName("of")
         .withParameterTypes(ENTITY_CLASS)
         .withClassName(PositionMoveRotation_CLASS)
   );
   public static final TypeCache.BaseCache<Method> NMS_ATTRIBUTE_FROM_BUKKIT_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT).withClassName("attribute.CraftAttribute").withMethodName("bukkitToMinecraftHolder")
   );
   public static final TypeCache.BaseCache<Method> ATTRIBUTE_INSTANCE_SET_BASE_VALUE_METHOD = new TypeCache.BaseCache.MethodLoader(
      new TypeCache.CacheBuilder(CachePackage.CRAFT_BUKKIT)
         .withClassName(ATTRIBUTE_INSTANCE_CLASS)
         .withMethodName("setBaseValue")
         .searchByParameters()
         .withParameterTypes(double.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> ATTRIBUTE_INSTANCE_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER).withClassName(ATTRIBUTE_INSTANCE_CLASS).withParameterTypes(HOLDER_CLASS, Consumer.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> UPDATE_ATTRIBUTES_CONSTRUCTOR = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutUpdateAttributes")
         .withParameterTypes(int.class, Collection.class)
   );
   public static final TypeCache.BaseCache<Constructor<?>> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR_V2 = new TypeCache.BaseCache.ConstructorLoader(
      new TypeCache.CacheBuilder(CachePackage.MINECRAFT_SERVER)
         .withCategory(CacheCategory.PACKET)
         .withClassName("PacketPlayOutEntityTeleport")
         .withParameterTypes(int.class, PositionMoveRotation_CLASS, Set.class, boolean.class)
   );
}
