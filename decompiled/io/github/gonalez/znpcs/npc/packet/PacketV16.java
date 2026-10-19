package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.inventory.ItemStack;

public class PacketV16 extends PacketV9 {
   @Override
   public String version() {
      return "16";
   }

   @Override
   public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
      List<Pair<?, ?>> pairs = Lists.newArrayListWithCapacity(ItemSlot.values().length);

      for (Entry<ItemSlot, ItemStack> entry : npc.getNpcPojo().getNpcEquip().entrySet()) {
         pairs.add(new Pair(this.getItemSlot(entry.getKey().getSlot()), this.convertItemStack(npc.getEntityID(), entry.getKey(), entry.getValue())));
      }

      return ImmutableList.of(CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1.load().newInstance(npc.getEntityID(), pairs));
   }
}
