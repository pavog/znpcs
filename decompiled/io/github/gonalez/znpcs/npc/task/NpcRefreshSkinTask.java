package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.npc.NPC;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.scheduler.BukkitRunnable;

public class NpcRefreshSkinTask extends BukkitRunnable {
   private final Set<Integer> outgoingRefresh = new HashSet<>();
   private int count = 0;

   public void run() {
      for (NPC npc : NPC.all()) {
         int refreshSkinDuration = npc.getNpcPojo().getRefreshSkinDuration();
         if (refreshSkinDuration != 0 && this.count % refreshSkinDuration == 0) {
            int id = npc.getNpcPojo().getId();
            if (!this.outgoingRefresh.contains(id)) {
               this.outgoingRefresh.add(id);
            }
         }
      }

      this.count++;
   }
}
