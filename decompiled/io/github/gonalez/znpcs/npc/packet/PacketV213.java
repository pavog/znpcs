package io.github.gonalez.znpcs.npc.packet;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import java.util.Collections;

public class PacketV213 extends PacketV21 {
   @Override
   public String version() {
      return "21.3";
   }

   @Override
   public Object getTeleportPacket(int entityId, Object nmsEntity) throws ReflectiveOperationException {
      return CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR_V2
         .load()
         .newInstance(entityId, CacheRegistry.PositionMoveRotation_ForEntity_Method.load().invoke(null, nmsEntity), Collections.emptySet(), false);
   }
}
