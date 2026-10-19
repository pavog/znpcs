package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.utility.Utils;
import java.lang.reflect.Constructor;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class PacketV8 implements Packet {
   @Override
   public String version() {
      return "8";
   }

   @Override
   public Object getPlayerPacket(Object nmsWorld, GameProfile gameProfile) throws ReflectiveOperationException {
      Constructor<?> constructor = Utils.isVersionSameOrNew(14)
         ? CacheRegistry.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR.load()
         : CacheRegistry.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR.load();
      return CacheRegistry.PLAYER_CONSTRUCTOR_OLD
         .load()
         .newInstance(CacheRegistry.GET_SERVER_METHOD.load().invoke(Bukkit.getServer()), nmsWorld, gameProfile, constructor.newInstance(nmsWorld));
   }

   @Override
   public Object getTeleportPacket(int entityId, Object nmsEntity) throws ReflectiveOperationException {
      return CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.load().newInstance(nmsEntity);
   }

   @Override
   public Object getSpawnPacket(int entityId, UUID uuid, Location location, EntityType entityType, Object nmsEntity) throws ReflectiveOperationException {
      return entityType == EntityType.PLAYER && !Utils.isVersionSameOrNew("20.2")
         ? CacheRegistry.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.load().newInstance(nmsEntity)
         : CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.load().newInstance(nmsEntity);
   }

   @Override
   public Object convertItemStack(int entityId, ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
      return CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD
         .load()
         .newInstance(entityId, itemSlot.getSlotOld(), CacheRegistry.AS_NMS_COPY_METHOD.load().invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack));
   }

   @Override
   public Object getClickType(Object interactPacket) throws ReflectiveOperationException {
      return Utils.getValue(interactPacket, "action");
   }

   @Override
   public Object getDatawatcherObject(Object nmsEntity) throws ReflectiveOperationException {
      return CacheRegistry.GET_DATA_WATCHER_METHOD.load().invoke(nmsEntity);
   }

   @Override
   public Object getMetadataPacket(int entityId, Object nmsEntity) throws ReflectiveOperationException {
      Object dataWatcher = this.getDatawatcherObject(nmsEntity);

      try {
         return CacheRegistry.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.load().newInstance(entityId, dataWatcher, Boolean.TRUE);
      } catch (Exception e2) {
         return CacheRegistry.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR_V1
            .load()
            .newInstance(entityId, CacheRegistry.GET_DATAWATCHER_B_LIST.load().invoke(dataWatcher));
      }
   }

   @Override
   public Object getHologramSpawnPacket(Object armorStand) throws ReflectiveOperationException {
      return CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.load().newInstance(armorStand);
   }

   @Override
   public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
      Builder<Object> builder = ImmutableList.builder();

      for (Entry<ItemSlot, ItemStack> stackEntry : npc.getNpcPojo().getNpcEquip().entrySet()) {
         builder.add(
            CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD
               .load()
               .newInstance(
                  npc.getEntityID(), stackEntry.getKey().getSlotOld(), this.convertItemStack(npc.getEntityID(), stackEntry.getKey(), stackEntry.getValue())
               )
         );
      }

      return builder.build();
   }

   @Override
   public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
      throw new IllegalStateException("Glow color is not supported for 1.8 version.");
   }

   @Override
   public boolean allowGlowColor() {
      return false;
   }
}
