package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.UnexpectedCallException;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.cache.TypeCache;
import io.github.gonalez.znpcs.utility.Utils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import org.bukkit.entity.EntityType;

public enum NPCType {
   PLAYER(CacheRegistry.ENTITY_PLAYER_CLASS, 0.0),
   ARMOR_STAND(CacheRegistry.ENTITY_ARMOR_STAND_CLASS, 0.0, "setSmall", "setArms"),
   CREEPER(CacheRegistry.ENTITY_CREEPER_CLASS, -0.15, "setPowered"),
   BAT(CacheRegistry.ENTITY_BAT_CLASS, -0.5, "setAwake"),
   BLAZE(CacheRegistry.ENTITY_BLAZE_CLASS, 0.0),
   CAVE_SPIDER(CacheRegistry.ENTITY_CAVE_SPIDER_CLASS, -1.0),
   COW(CacheRegistry.ENTITY_COW_CLASS, -0.25, "setAge"),
   CHICKEN(CacheRegistry.ENTITY_CHICKEN_CLASS, -1.0, "setAge"),
   ENDER_DRAGON(CacheRegistry.ENTITY_ENDER_DRAGON_CLASS, 1.5),
   ENDERMAN(CacheRegistry.ENTITY_ENDERMAN_CLASS, 0.7),
   ENDERMITE(CacheRegistry.ENTITY_ENDERMITE_CLASS, -1.5),
   GHAST(CacheRegistry.ENTITY_GHAST_CLASS, 3.0),
   IRON_GOLEM(CacheRegistry.ENTITY_IRON_GOLEM_CLASS, 0.75),
   GIANT(CacheRegistry.ENTITY_GIANT_ZOMBIE_CLASS, 11.0),
   GUARDIAN(CacheRegistry.ENTITY_GUARDIAN_CLASS, -0.7),
   HORSE(CacheRegistry.ENTITY_HORSE_CLASS, 0.0, "setStyle", "setAge", "setColor", "setVariant"),
   LLAMA(CacheRegistry.ENTITY_LLAMA_CLASS, 0.0, "setAge"),
   MAGMA_CUBE(CacheRegistry.ENTITY_MAGMA_CUBE_CLASS, -1.25, "setSize"),
   MUSHROOM_COW(CacheRegistry.ENTITY_MUSHROOM_COW_CLASS, -0.25, "setAge"),
   OCELOT(CacheRegistry.ENTITY_OCELOT_CLASS, -1.0, "setCatType", "setAge"),
   PARROT(CacheRegistry.ENTITY_PARROT_CLASS, -1.5, "setVariant"),
   PIG(CacheRegistry.ENTITY_PIG_CLASS, -1.0, "setAge"),
   PANDA(CacheRegistry.ENTITY_PANDA_CLASS, -0.6, "setAge", "setMainGene", "setHiddenGene"),
   RABBIT(CacheRegistry.ENTITY_RABBIT_CLASS, -1.0, "setRabbitType"),
   POLAR_BEAR(CacheRegistry.ENTITY_POLAR_BEAR_CLASS, -0.5),
   SHEEP(CacheRegistry.ENTITY_SHEEP_CLASS, -0.5, "setAge", "setSheared", "setColor"),
   SILVERFISH(CacheRegistry.ENTITY_SILVERFISH_CLASS, -1.5),
   SNOWMAN(CacheRegistry.ENTITY_SNOWMAN_CLASS, 0.0, "setHasPumpkin", "setDerp"),
   SKELETON(CacheRegistry.ENTITY_SKELETON_CLASS, 0.0),
   SHULKER(CacheRegistry.ENTITY_SHULKER_CLASS, 0.0),
   SLIME(CacheRegistry.ENTITY_SLIME_CLASS, -1.25, "setSize"),
   SPIDER(CacheRegistry.ENTITY_SPIDER_CLASS, -1.0),
   SQUID(CacheRegistry.ENTITY_SQUID_CLASS, -1.0),
   VILLAGER(CacheRegistry.ENTITY_VILLAGER_CLASS, 0.0, "setProfession", "setVillagerType", "setAge"),
   WITCH(CacheRegistry.ENTITY_WITCH_CLASS, 0.5),
   WITHER(CacheRegistry.ENTITY_WITHER_CLASS, 1.75),
   ZOMBIE(CacheRegistry.ENTITY_ZOMBIE_CLASS, 0.0, "setBaby"),
   WOLF(CacheRegistry.ENTITY_WOLF_CLASS, -1.0, "setSitting", "setTamed", "setAngry", "setAge", "setCollarColor"),
   FOX(CacheRegistry.ENTITY_FOX_CLASS, -1.0, "setFoxType", "setSitting", "setSleeping", "setAge", "setCrouching"),
   BEE(CacheRegistry.ENTITY_BEE_CLASS, -1.0, "setAnger", "setHasNectar", "setHasStung"),
   TURTLE(CacheRegistry.ENTITY_TURTLE, -1.0),
   WARDEN(CacheRegistry.ENTITY_WARDEN, 1.0),
   AXOLOTL(CacheRegistry.ENTITY_AXOLOTL_CLASS, -1.0, "setVariant", "setAge"),
   GOAT(CacheRegistry.ENTITY_GOAT_CLASS, -0.5, "setScreamingGoat", "setAge"),
   ALLAY(CacheRegistry.ENTITY_ALLAY_CLASS, -0.5, "startDancing", "stopDancing"),
   PIGLIN(CacheRegistry.ENTITY_PIGLIN_CLASS, -1.0),
   FROG(CacheRegistry.ENTITY_FROG_CLASS, -0.5, "setAge"),
   CAMEL(CacheRegistry.ENTITY_CAMEL_CLASS, 0.5, "setAge"),
   HOGLIN(CacheRegistry.ENTITY_HOGLIN_CLASS, -1.0),
   CAT(CacheRegistry.ENTITY_CAT_CLASS, -1.0, "setCatType", "setCollarColor");

   private static final String EMPTY_STRING = "";
   private final double holoHeight;
   private final CustomizationLoader customizationLoader;
   private final Constructor<?> constructor;
   private EntityType bukkitEntityType;
   private Object nmsEntityType;

   NPCType(Class<?> entityClass, double holoHeight, String... methods) {
      this.holoHeight = holoHeight;
      this.customizationLoader = entityClass == null
         ? null
         : new CustomizationLoader(this.bukkitEntityType = EntityType.valueOf(this.name()), Arrays.asList(methods));
      if (entityClass != null && !entityClass.isAssignableFrom(CacheRegistry.ENTITY_PLAYER_CLASS)) {
         try {
            if (Utils.isVersionSameOrNew(14)) {
               this.nmsEntityType = ((Optional)CacheRegistry.ENTITY_TYPES_A_METHOD.load().invoke(null, this.bukkitEntityType.getKey().getKey().toLowerCase()))
                  .get();
               this.constructor = entityClass.getConstructor(CacheRegistry.ENTITY_TYPES_CLASS, CacheRegistry.WORLD_CLASS);
            } else {
               this.constructor = entityClass.getConstructor(CacheRegistry.WORLD_CLASS);
            }
         } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
         }
      } else {
         this.constructor = null;
      }
   }

   public double getHoloHeight() {
      return this.holoHeight;
   }

   public Constructor<?> getConstructor() {
      return this.constructor;
   }

   public Object getNmsEntityType() {
      return this.nmsEntityType;
   }

   public CustomizationLoader getCustomizationLoader() {
      return this.customizationLoader;
   }

   public static Object[] arrayToPrimitive(String[] strings, Method method) {
      Class<?>[] methodParameterTypes = method.getParameterTypes();
      Object[] newArray = new Object[methodParameterTypes.length];

      for (int i = 0; i < methodParameterTypes.length; i++) {
         TypeProperty typeProperty = TypeProperty.forType(methodParameterTypes[i]);
         if (typeProperty != null) {
            newArray[i] = typeProperty.getFunction().apply(strings[i]);
         } else {
            newArray[i] = TypeCache.ClassCache.find(strings[i], methodParameterTypes[i]);
         }
      }

      return newArray;
   }

   public void updateCustomization(NPC npc, String name, String[] values) {
      if (this.customizationLoader.contains(name)) {
         try {
            Method method = this.customizationLoader.getMethods().get(name);
            method.invoke(npc.getBukkitEntity(), arrayToPrimitive(values, method));
            npc.updateMetadata(npc.getViewers());
         } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("can't invoke method: " + name, e);
         }
      }
   }
}
