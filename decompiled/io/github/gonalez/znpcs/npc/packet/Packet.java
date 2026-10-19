package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.utility.ReflectionUtils;
import io.github.gonalez.znpcs.utility.Utils;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface Packet {
   String version();

   @PacketValue(keyName = "playerPacket")
   Object getPlayerPacket(Object var1, GameProfile var2) throws ReflectiveOperationException;

   @PacketValue(keyName = "spawnPacket")
   Object getSpawnPacket(int var1, UUID var2, Location var3, EntityType var4, Object var5) throws ReflectiveOperationException;

   Object convertItemStack(int var1, ItemSlot var2, ItemStack var3) throws ReflectiveOperationException;

   Object getClickType(Object var1) throws ReflectiveOperationException;

   Object getMetadataPacket(int var1, Object var2) throws ReflectiveOperationException;

   @PacketValue(keyName = "hologramSpawnPacket", valueType = ValueType.ARGUMENTS)
   Object getHologramSpawnPacket(Object var1) throws ReflectiveOperationException;

   @PacketValue(keyName = "destroyPacket", valueType = ValueType.ARGUMENTS)
   default Object getDestroyPacket(int entityId) throws ReflectiveOperationException {
      return CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR
         .load()
         .newInstance(CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.load().getParameterTypes()[0].isArray() ? new int[]{entityId} : entityId);
   }

   Object getDatawatcherObject(Object var1) throws ReflectiveOperationException;

   Object getTeleportPacket(int var1, Object var2) throws ReflectiveOperationException;

   @PacketValue(keyName = "enumSlot", valueType = ValueType.ARGUMENTS)
   default Object getItemSlot(int slot) {
      return CacheRegistry.ENUM_ITEM_SLOT.getEnumConstants()[slot];
   }

   @PacketValue(keyName = "removeTab")
   default Object getTabRemovePacket(Object nmsEntity) throws ReflectiveOperationException {
      try {
         return CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR
            .load()
            .newInstance(CacheRegistry.REMOVE_PLAYER_FIELD.load(), Collections.singletonList(nmsEntity));
      } catch (Throwable throwable) {
         boolean useOldMethod = CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_REMOVE_CLASS != null;
         return useOldMethod
            ? CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_REMOVE_CONSTRUCTOR
               .load()
               .newInstance(Collections.singletonList(CacheRegistry.GET_UNIQUE_ID_METHOD.load().invoke(nmsEntity)))
            : CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.load().newInstance(CacheRegistry.REMOVE_PLAYER_FIELD.load(), nmsEntity);
      }
   }

   @PacketValue(keyName = "equipPackets")
   ImmutableList<Object> getEquipPackets(NPC var1) throws ReflectiveOperationException;

   @PacketValue(keyName = "scoreboardPackets")
   default ImmutableList<Object> updateScoreboard(NPC npc) throws ReflectiveOperationException {
      Builder<Object> builder = ImmutableList.builder();
      boolean isVersion17 = Utils.isVersionSameOrNew(17);
      boolean isVersion9 = Utils.isVersionSameOrNew(9);
      String gameProfileName = (String)CacheRegistry.GAME_PROFILE_NAME_METHOD.load().invoke(npc.getGameProfile());
      Object scoreboardTeamPacket = isVersion17
         ? CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.load().newInstance(null, gameProfileName)
         : CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.load().newInstance();
      if (!isVersion17) {
         Utils.setValue(scoreboardTeamPacket, "a", gameProfileName);
         Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 1);
      }

      builder.add(isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1.load().invoke(null, scoreboardTeamPacket) : scoreboardTeamPacket);
      if (isVersion17) {
         scoreboardTeamPacket = CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.load().newInstance(null, gameProfileName);
         if (Utils.isVersionSameOrNew(18)) {
            Utils.setValue(scoreboardTeamPacket, "d", gameProfileName);
            ReflectionUtils.findFieldForClassAndSet(
               scoreboardTeamPacket, CacheRegistry.ENUM_TAG_VISIBILITY, CacheRegistry.ENUM_TAG_VISIBILITY_NEVER_FIELD.load()
            );

            try {
               Utils.setValue(scoreboardTeamPacket, "m", CacheRegistry.ENUM_CHAT_FORMAT_FIND.load().invoke(null, "DARK_GRAY"));
            } catch (Exception exception) {
               Utils.setValue(scoreboardTeamPacket, "m", CacheRegistry.ENUM_CHAT_FORMAT_FIND.load().invoke(null, "darkgray"));
            }
         } else {
            Utils.setValue(scoreboardTeamPacket, "e", gameProfileName);
            Utils.setValue(scoreboardTeamPacket, "l", CacheRegistry.ENUM_TAG_VISIBILITY_NEVER_FIELD.load());
         }
      } else {
         scoreboardTeamPacket = CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.load().newInstance();
         Utils.setValue(scoreboardTeamPacket, "a", gameProfileName);
         Utils.setValue(scoreboardTeamPacket, "e", "never");
         Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 0);
      }

      Collection<String> collection = isVersion17
         ? (Collection)CacheRegistry.SCOREBOARD_PLAYER_LIST.load().invoke(scoreboardTeamPacket)
         : (Collection)Utils.getValue(scoreboardTeamPacket, isVersion9 ? "h" : "g");
      if (npc.getNpcPojo().getNpcType() == NPCType.PLAYER) {
         collection.add(gameProfileName);
      } else {
         collection.add(npc.getUUID().toString());
      }

      if (this.allowGlowColor() && FunctionFactory.isTrue(npc, "glow")) {
         this.updateGlowPacket(npc, scoreboardTeamPacket);
      }

      builder.add(
         isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE.load().invoke(null, scoreboardTeamPacket, Boolean.TRUE) : scoreboardTeamPacket
      );
      return builder.build();
   }

   void updateGlowPacket(NPC var1, Object var2) throws ReflectiveOperationException;

   boolean allowGlowColor();

   default void update(PacketCache packetCache) {
      packetCache.flushCache("scoreboardPackets");
   }
}
