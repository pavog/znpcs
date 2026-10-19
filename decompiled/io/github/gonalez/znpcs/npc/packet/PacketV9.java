package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.utility.Utils;
import java.util.Map.Entry;
import org.bukkit.inventory.ItemStack;

public class PacketV9 extends PacketV8 {
   @Override
   public String version() {
      return "9";
   }

   @Override
   public Object convertItemStack(int entityId, ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
      return CacheRegistry.AS_NMS_COPY_METHOD.load().invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack);
   }

   @Override
   public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
      Builder<Object> builder = ImmutableList.builder();

      for (Entry<ItemSlot, ItemStack> stackEntry : npc.getNpcPojo().getNpcEquip().entrySet()) {
         builder.add(
            CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD
               .load()
               .newInstance(
                  npc.getEntityID(),
                  this.getItemSlot(stackEntry.getKey().getSlot()),
                  this.convertItemStack(npc.getEntityID(), stackEntry.getKey(), stackEntry.getValue())
               )
         );
      }

      return builder.build();
   }

   @Override
   public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
      Object enumChatString = CacheRegistry.ENUM_CHAT_TO_STRING_METHOD.load().invoke(npc.getGlowColor());
      if (Utils.isVersionSameOrNew(13)) {
         Utils.setValue(packet, npc.getGlowColor(), CacheRegistry.ENUM_CHAT_CLASS);
         Utils.setValue(packet, "c", CacheRegistry.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.load().newInstance(enumChatString));
      } else {
         Utils.setValue(packet, "g", CacheRegistry.GET_ENUM_CHAT_ID_METHOD.load().invoke(npc.getGlowColor()));
         Utils.setValue(packet, "c", enumChatString);
      }
   }

   @Override
   public boolean allowGlowColor() {
      return true;
   }
}
