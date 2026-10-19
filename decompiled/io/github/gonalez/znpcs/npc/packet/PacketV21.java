package io.github.gonalez.znpcs.npc.packet;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.utility.Utils;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class PacketV21 extends PacketV19 {
   @Override
   public String version() {
      return "20.5";
   }

   @Override
   public Object getDatawatcherObject(Object nmsEntity) throws ReflectiveOperationException {
      return CacheRegistry.DATAWATCHER_ENTITY_FIELD.load().get(nmsEntity);
   }

   @Override
   public Object getSpawnPacket(int entityId, UUID uuid, Location location, EntityType entityType, Object nmsEntity) throws ReflectiveOperationException {
      return !Utils.isVersionSameOrNew(21)
         ? super.getSpawnPacket(entityId, uuid, location, entityType, nmsEntity)
         : CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR
            .load()
            .newInstance(
               entityId,
               uuid,
               location.getX(),
               location.getY(),
               location.getZ(),
               location.getYaw(),
               location.getPitch(),
               CacheRegistry.CraftEntityType_bukkitToMinecraft_Method.load().invoke(null, entityType),
               0,
               CacheRegistry.VEC3D_FIELD.load(),
               0
            );
   }
}
