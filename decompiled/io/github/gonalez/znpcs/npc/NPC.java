package io.github.gonalez.znpcs.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.UnexpectedCallException;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.configuration.ConfigurationValue;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.npc.line.LineReplacer;
import io.github.gonalez.znpcs.npc.packet.PacketCache;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.Utils;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class NPC {
   private static final ConcurrentMap<Integer, NPC> NPC_MAP = new ConcurrentHashMap<>();
   private static final String PROFILE_TEXTURES = "textures";
   private static final String START_PREFIX = "[ZNPC] ";
   private final Set<ZUser> viewers = Sets.newConcurrentHashSet();
   private final PacketCache packets = new PacketCache();
   private final NPCModel npcPojo;
   private final String npcName;
   private final NPCSkin npcSkin;
   private long lastMove = -1L;
   private int entityID;
   private Object glowColor;
   private Object tabConstructor;
   private Object updateTabConstructor;
   private Object nmsEntity;
   private Object bukkitEntity;
   private UUID uuid;
   private GameProfile gameProfile;
   private NPCPath.PathInitializer npcPath;
   private final List<NPC> holograms = new ArrayList<>();
   private boolean shouldLoad;

   public NPC(NPCModel npcModel, boolean load) {
      this.npcPojo = npcModel;
      this.npcName = NamingType.DEFAULT.resolve(this);
      this.npcSkin = NPCSkin.forValues(npcModel.getSkin(), npcModel.getSignature());
      this.shouldLoad = load;
   }

   public NPC(NPCModel npcModel) {
      this(npcModel, false);
   }

   public void onLoad(boolean registry) {
      this.shouldLoad = registry;
      if (registry && NPC_MAP.containsKey(this.getNpcPojo().getId())) {
         throw new IllegalStateException("npc with id " + this.getNpcPojo().getId() + " already exists.");
      }

      this.changeSkin(this.npcSkin, false);
      this.changeType(this.npcPojo.getNpcType());
      this.setLocation(this.getNpcPojo().getLocation().bukkitLocation(), false);
      if (this.npcPojo.getPathName() != null) {
         this.setPath(NPCPath.AbstractTypeWriter.find(this.npcPojo.getPathName()));
      }

      this.npcPojo.getCustomizationMap().forEach((key, value) -> this.npcPojo.getNpcType().updateCustomization(this, key, value));
      if (registry) {
         NPC_MAP.put(this.getNpcPojo().getId(), this);
      }
   }

   private void destroyHologram() {
      for (NPC npc : this.holograms) {
         npc.deleteViewers();
      }

      this.holograms.clear();
   }

   private Location getFixedLocationForHologram(Location location) {
      return location.clone().subtract(0.0, 0.2, 0.0).add(0.0, this.getNpcPojo().getHologramHeight(), 0.0);
   }

   public void createHologram() {
      this.destroyHologram();
      if (this.shouldLoad && !FunctionFactory.isTrue(this, "holo")) {
         List<String> lines = this.getNpcPojo().getHologramLines();
         Location location = this.getFixedLocationForHologram(this.getNpcPojo().getLocation().bukkitLocation());
         location.add(0.0, this.getNpcPojo().getNpcType().getHoloHeight(), 0.0);
         location.subtract(0.0, Configuration.CONFIGURATION.<Double>getValue(ConfigurationValue.LINE_SPACING), 0.0);

         for (String line : lines) {
            location.add(0.0, Configuration.CONFIGURATION.<Double>getValue(ConfigurationValue.LINE_SPACING), 0.0);
            NPC npc = new NPC(
               new NPCModel().withLocation(new ZLocation(location.clone())).withNpcType(NPCType.ARMOR_STAND).withHologramLines(ImmutableList.of(line))
            );
            npc.onLoad(false);
            this.holograms.add(npc);
            this.viewers.forEach(npc::spawn);
         }
      }
   }

   private void updateLine(@Nullable ZUser user) throws InvocationTargetException, IllegalAccessException {
      String line = this.getNpcPojo().getHologramLines().get(0);
      if (Utils.isVersionSameOrNew(13)) {
         CacheRegistry.SET_CUSTOM_NAME_NEW_METHOD
            .load()
            .invoke(this.getNmsEntity(), CacheRegistry.CRAFT_CHAT_MESSAGE_METHOD.load().invoke(null, LineReplacer.makeAll(user, line)));
      } else {
         CacheRegistry.SET_CUSTOM_NAME_OLD_METHOD.load().invoke(this.getNmsEntity(), LineReplacer.makeAll(user, line));
      }
   }

   public NPCModel getNpcPojo() {
      return this.npcPojo;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public int getEntityID() {
      return this.entityID;
   }

   public Object getBukkitEntity() {
      return this.bukkitEntity;
   }

   public Object getNmsEntity() {
      return this.nmsEntity;
   }

   public Object getGlowColor() {
      return this.glowColor;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public NPCPath.PathInitializer getNpcPath() {
      return this.npcPath;
   }

   public Set<ZUser> getViewers() {
      return this.viewers;
   }

   public PacketCache getPackets() {
      return this.packets;
   }

   public void setGlowColor(Object glowColor) {
      this.glowColor = glowColor;
   }

   public void setLocation(Location location, boolean updateTime) {
      try {
         if (this.npcPath == null) {
            this.lookAt(null, location, true);
            if (updateTime) {
               this.lastMove = System.nanoTime();
            }

            this.npcPojo
               .setLocation(
                  new ZLocation(
                     location = new Location(
                        location.getWorld(),
                        ConfigurationConstants.CENTER_NPC ? location.getBlockX() + 0.5 : location.getX(),
                        location.getY(),
                        ConfigurationConstants.CENTER_NPC ? location.getBlockZ() + 0.5 : location.getZ(),
                        location.getYaw(),
                        location.getPitch()
                     )
                  )
               );
         }

         CacheRegistry.SET_LOCATION_METHOD
            .load()
            .invoke(this.nmsEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
         Object npcTeleportPacket = this.getPackets().getProxyInstance().getTeleportPacket(this.entityID, this.nmsEntity);
         this.viewers.forEach(player -> Utils.sendPackets(player, npcTeleportPacket));
         this.createHologram();
      } catch (ReflectiveOperationException operationException) {
         throw new UnexpectedCallException(operationException);
      }
   }

   public void changeSkin(NPCSkin skinFetch, boolean refresh) {
      this.npcPojo.setSkin(skinFetch.getTexture());
      this.npcPojo.setSignature(skinFetch.getSignature());
      this.gameProfile = this.newGameProfileWithProperties("[ZNPC] " + this.npcName, UUID.randomUUID(), skinFetch, null);
      if (refresh) {
         this.updateProfile(this.gameProfile);
         ServersNPC.SCHEDULER.scheduleSyncDelayedTask(this::deleteViewers, 20);
      }
   }

   public GameProfile newGameProfileWithProperties(String name, UUID uuid, NPCSkin skin, Property property) {
      if (property == null) {
         property = new Property("textures", skin.getTexture(), skin.getSignature());
      }

      try {
         PropertyMap propertyMap = (PropertyMap)CacheRegistry.PROPERTY_MAP_NEW_CONSTRUCTOR.load().newInstance(ImmutableMultimap.of("textures", property));
         return (GameProfile)CacheRegistry.GAMEPROFILE_NEW_CONSTRUCTOR.load().newInstance(uuid, name, propertyMap);
      } catch (Throwable exception) {
         GameProfile gameProfile1 = new GameProfile(uuid, name);
         gameProfile1.getProperties().put("textures", property);
         return gameProfile1;
      }
   }

   public void setSecondLayerSkin() {
      try {
         Object dataWatcherObject = this.getPackets().getProxyInstance().getDatawatcherObject(this.getNmsEntity());
         if (Utils.isVersionSameOrNew(9)) {
            CacheRegistry.SET_DATA_WATCHER_METHOD
               .load()
               .invoke(
                  dataWatcherObject,
                  CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR
                     .load()
                     .newInstance(this.npcSkin.getLayerIndex(), CacheRegistry.DATA_WATCHER_REGISTER_FIELD.load()),
                  (byte)127
               );
         } else {
            CacheRegistry.WATCH_DATA_WATCHER_METHOD.load().invoke(dataWatcherObject, 10, (byte)127);
         }
      } catch (ReflectiveOperationException var2) {
         throw new UnexpectedCallException(var2);
      }
   }

   public synchronized void changeType(NPCType npcType) {
      this.deleteViewers();

      try {
         Object nmsWorld = CacheRegistry.GET_HANDLE_WORLD_METHOD.load().invoke(this.getLocation().getWorld());
         boolean isPlayer = npcType == NPCType.PLAYER;
         this.nmsEntity = isPlayer
            ? this.packets.getProxyInstance().getPlayerPacket(nmsWorld, this.gameProfile)
            : (
               Utils.isVersionSameOrNew(14)
                  ? npcType.getConstructor().newInstance(npcType.getNmsEntityType(), nmsWorld)
                  : npcType.getConstructor().newInstance(nmsWorld)
            );
         this.bukkitEntity = CacheRegistry.GET_BUKKIT_ENTITY_METHOD.load().invoke(this.nmsEntity);
         this.uuid = (UUID)CacheRegistry.GET_UNIQUE_ID_METHOD.load().invoke(this.nmsEntity);
         this.entityID = (Integer)CacheRegistry.GET_ENTITY_ID.load().invoke(this.nmsEntity);
         if (isPlayer) {
            this.updateTabPackets();

            try {
               this.setSecondLayerSkin();
            } catch (Exception var5) {
            }
         }

         this.npcPojo.setNpcType(npcType);
         this.setLocation(this.getLocation(), false);
         this.packets.flushCache("spawnPacket", "removeTab");
         FunctionFactory.findFunctionsForNpc(this).forEach(function -> function.resolve(this));
         this.getPackets().getProxyInstance().update(this.packets);
         this.createHologram();
      } catch (Exception operationException) {
         throw new UnexpectedCallException(operationException);
      }
   }

   private void updateTabPackets() throws Exception {
      try {
         this.tabConstructor = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR
            .load()
            .newInstance(CacheRegistry.ADD_PLAYER_FIELD.load(), Collections.singletonList(this.nmsEntity));
      } catch (Throwable e) {
         try {
            this.tabConstructor = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR
               .load()
               .newInstance(CacheRegistry.ADD_PLAYER_FIELD.load(), this.nmsEntity);
            this.updateTabConstructor = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR
               .load()
               .newInstance(CacheRegistry.UPDATE_LISTED_FIELD.load(), this.nmsEntity);
         } catch (Exception exception) {
            this.tabConstructor = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR_V2
               .load()
               .newInstance(EnumSet.of((Enum)CacheRegistry.ADD_PLAYER_FIELD.load(), (Enum)CacheRegistry.UPDATE_LISTED_FIELD.load()), Collections.emptyList());
            CacheRegistry.PACKET_PLAY_OUT_INFO_LIST
               .load()
               .set(this.tabConstructor, Collections.singletonList(createPlayerInfoUpdatePacket(this, this.getGameProfile())));
         }
      }

      this.packets.flushCache();
   }

   private static Object createPlayerInfoUpdatePacket(NPC npc, GameProfile gameProfile) throws Exception {
      Class<?> packetClass = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_ENTRY_CLASS;
      Constructor<?> constructor = packetClass.getDeclaredConstructor(UUID.class);
      constructor.setAccessible(true);
      Object packet = constructor.newInstance(npc.getUUID());
      getFieldsByRawType(packetClass, GameProfile.class).iterator().next().set(packet, gameProfile);
      getFieldsByRawType(packetClass, boolean.class).iterator().next().set(packet, Boolean.TRUE);
      Method method = packetClass.getDeclaredMethod("a");
      method.setAccessible(true);
      return method.invoke(packet);
   }

   public static List<Field> getFieldsByRawType(Class<?> clazz, Class<?> rawType) {
      List<Field> matchingFields = new ArrayList<>();

      for (Field field : clazz.getDeclaredFields()) {
         if (rawType.isAssignableFrom(field.getType())) {
            field.setAccessible(true);
            matchingFields.add(field);
         }
      }

      return matchingFields;
   }

   public synchronized void respawn() {
      for (ZUser user : this.getViewers()) {
         this.delete(user);
         this.spawn(user);
      }

      this.createHologram();
   }

   public synchronized void spawn(ZUser user) {
      if (!this.viewers.contains(user)) {
         try {
            this.viewers.add(user);
            boolean npcIsPlayer = this.npcPojo.getNpcType() == NPCType.PLAYER;
            if (FunctionFactory.isTrue(this, "glow") || npcIsPlayer) {
               ImmutableList<Object> scoreboardPackets = this.packets.getProxyInstance().updateScoreboard(this);
               scoreboardPackets.forEach(p -> Utils.sendPackets(user, p));
            }

            if (npcIsPlayer) {
               if (FunctionFactory.isTrue(this, "mirror")) {
                  PropertyMap propertyMap = (PropertyMap)CacheRegistry.GAME_PROFILE_PROPERTIES_METHOD.load().invoke(user.getGameProfile());
                  Property textures = (Property)propertyMap.get("textures").iterator().next();
                  this.updateProfile(
                     this.newGameProfileWithProperties(
                        (String)CacheRegistry.GAME_PROFILE_NAME_METHOD.load().invoke(this.gameProfile),
                        (UUID)CacheRegistry.GAME_PROFILE_ID_METHOD.load().invoke(this.gameProfile),
                        null,
                        textures
                     )
                  );
               }

               Utils.sendPackets(user, this.tabConstructor, this.updateTabConstructor);
            }

            Utils.sendPackets(
               user,
               this.packets
                  .getProxyInstance()
                  .getSpawnPacket(
                     this.entityID,
                     this.uuid,
                     this.getLocation(),
                     (EntityType)CacheRegistry.GET_BUKKIT_ENTITY_TYPE_METHOD.load().invoke(this.getBukkitEntity()),
                     this.nmsEntity
                  )
            );
            this.updateMetadata(Collections.singleton(user));
            this.sendEquipPackets(user);
            this.lookAt(user, this.getLocation(), true);
            if (npcIsPlayer) {
               Object removeTabPacket = this.packets.getProxyInstance().getTabRemovePacket(this.nmsEntity);
               ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() -> Utils.sendPackets(user, removeTabPacket, this.updateTabConstructor), 60);
            } else if (this.getNpcPojo().getNpcType() == NPCType.ARMOR_STAND) {
               CacheRegistry.SET_CUSTOM_NAME_VISIBLE_METHOD.load().invoke(this.getNmsEntity(), true);
               CacheRegistry.SET_INVISIBLE_METHOD.load().invoke(this.getNmsEntity(), true);
               this.updateLine(user);
            }

            for (NPC npc : this.holograms) {
               npc.spawn(user);
            }

            this.updateAttributes(user);
         } catch (Throwable operationException) {
            this.delete(user);
            throw new UnexpectedCallException(operationException);
         }
      }
   }

   public void delete(ZUser user) {
      if (this.viewers.contains(user)) {
         this.viewers.remove(user);
         this.handleDelete(user);

         for (NPC npc : this.holograms) {
            npc.delete(user);
         }
      }
   }

   private void handleDelete(ZUser user) {
      try {
         if (this.npcPojo.getNpcType() == NPCType.PLAYER) {
            this.packets.getProxyInstance().getTabRemovePacket(this.nmsEntity);
         }

         Utils.sendPackets(user, this.packets.getProxyInstance().getDestroyPacket(this.entityID));
      } catch (ReflectiveOperationException operationException) {
         throw new UnexpectedCallException(operationException);
      }
   }

   public void lookAt(ZUser player, Location location, boolean rotation) {
      long lastMoveNanos = System.nanoTime() - this.lastMove;
      if (this.lastMove <= 1L || lastMoveNanos >= 1000000000L) {
         Location direction = rotation
            ? location
            : this.npcPojo
               .getLocation()
               .bukkitLocation()
               .clone()
               .setDirection(location.clone().subtract(this.npcPojo.getLocation().bukkitLocation().clone()).toVector());

         try {
            Object lookPacket = CacheRegistry.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR
               .load()
               .newInstance(this.entityID, (byte)(direction.getYaw() * 256.0F / 360.0F), (byte)(direction.getPitch() * 256.0F / 360.0F), Boolean.TRUE);
            Object headRotationPacket = CacheRegistry.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR
               .load()
               .newInstance(this.nmsEntity, (byte)(direction.getYaw() * 256.0F / 360.0F));
            if (player != null) {
               Utils.sendPackets(player, lookPacket, headRotationPacket);
            } else {
               this.viewers.forEach(players -> Utils.sendPackets(players, headRotationPacket));
            }
         } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
         }
      }
   }

   public void deleteViewers() {
      Iterator<ZUser> iterator = this.viewers.iterator();

      while (iterator.hasNext()) {
         ZUser user = iterator.next();
         this.delete(user);
         iterator.remove();
      }
   }

   void updateAttributes(ZUser user) {
      for (Entry<String, Double> entry : this.getNpcPojo().getBukkitAttributes().entrySet()) {
         try {
            Attribute attribute = Attribute.valueOf(entry.getKey());
            Object minecraftAttribute = CacheRegistry.NMS_ATTRIBUTE_FROM_BUKKIT_METHOD.load().invoke(null, attribute);
            Object attributeInst = CacheRegistry.ATTRIBUTE_INSTANCE_CONSTRUCTOR.load().newInstance(minecraftAttribute, (Consumer<Object>)o -> {});
            CacheRegistry.ATTRIBUTE_INSTANCE_SET_BASE_VALUE_METHOD.load().invoke(attributeInst, entry.getValue());
            Object packet = CacheRegistry.UPDATE_ATTRIBUTES_CONSTRUCTOR.load().newInstance(this.entityID, List.of(attributeInst));
            Utils.sendPackets(user, packet);
         } catch (Exception operationException) {
            throw new UnexpectedCallException(operationException);
         }
      }
   }

   void updateMetadata(Iterable<ZUser> users) {
      try {
         Object metaData = this.packets.getProxyInstance().getMetadataPacket(this.entityID, this.nmsEntity);

         for (ZUser user : users) {
            Utils.sendPackets(user, metaData);
         }
      } catch (ReflectiveOperationException operationException) {
         throw new UnexpectedCallException(operationException);
      }
   }

   public void updateProfile(GameProfile gameProfile) {
      if (this.npcPojo.getNpcType() == NPCType.PLAYER) {
         try {
            CacheRegistry.SET_PROFILE_METHOD.load().set(this.nmsEntity, gameProfile);
            this.updateTabPackets();
         } catch (Exception operationException) {
            throw new UnexpectedCallException(operationException);
         }
      }
   }

   public void sendEquipPackets(ZUser zUser) {
      if (!this.npcPojo.getNpcEquip().isEmpty()) {
         try {
            ImmutableList<Object> equipPackets = this.packets.getProxyInstance().getEquipPackets(this);
            equipPackets.forEach(o -> Utils.sendPackets(zUser, o));
         } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException.getCause());
         }
      }
   }

   public void setPath(NPCPath.AbstractTypeWriter typeWriter) {
      if (typeWriter == null) {
         this.npcPath = null;
         this.npcPojo.setPathName("none");
      } else {
         this.npcPath = typeWriter.getPath(this);
         this.npcPojo.setPathName(typeWriter.getName());
      }
   }

   public void tryStartConversation(Player player) {
      ConversationModel conversation = this.npcPojo.getConversation();
      if (conversation == null) {
         throw new IllegalStateException("can't find conversation");
      }

      conversation.startConversation(this, player);
   }

   public Location getLocation() {
      return this.npcPath != null ? this.npcPath.getLocation().bukkitLocation() : this.npcPojo.getLocation().bukkitLocation();
   }

   public static NPC find(int id) {
      return NPC_MAP.get(id);
   }

   public static void unregister(int id) {
      NPC npc = find(id);
      if (npc == null) {
         throw new IllegalStateException("can't find npc with id " + id);
      }

      NPC_MAP.remove(id);
      npc.deleteViewers();
   }

   public static Collection<NPC> all() {
      return NPC_MAP.values();
   }

   public void onUpdate(ZUser zUser) {
      for (NPC hologramLine : this.holograms) {
         try {
            hologramLine.updateLine(zUser);
            hologramLine.updateMetadata(Collections.singleton(zUser));
         } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
         }
      }
   }
}
