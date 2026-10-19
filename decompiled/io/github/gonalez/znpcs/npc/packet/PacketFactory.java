package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import io.github.gonalez.znpcs.utility.Utils;

public final class PacketFactory {
   public static final ImmutableSet<Packet> ALL = ImmutableSet.of(
      new PacketV8(), new PacketV9(), new PacketV16(), new PacketV17(), new PacketV18(), new PacketV19(), new Packet[]{new PacketV21(), new PacketV213()}
   );
   public static final Packet PACKET_FOR_CURRENT_VERSION = findPacketForVersion();

   public static Packet findPacketForVersion() {
      Packet latest = null;
      UnmodifiableIterator var1 = ALL.iterator();

      while (var1.hasNext()) {
         Packet packet = (Packet)var1.next();
         if (Utils.isVersionSameOrNew(packet.version())) {
            latest = packet;
         }
      }

      if (latest == null) {
         throw new IllegalArgumentException("No packet instance found for version: " + Utils.BUKKIT_VERSION);
      } else {
         return latest;
      }
   }
}
