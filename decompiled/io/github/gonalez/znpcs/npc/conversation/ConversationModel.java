package io.github.gonalez.znpcs.npc.conversation;

import io.github.gonalez.znpcs.npc.NPC;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public class ConversationModel {
   private String conversationName;
   private ConversationModel.ConversationType conversationType;
   private final transient Map<UUID, Long> lastStarted = new HashMap<>();

   public ConversationModel(String conversationName, String conversationType) {
      this.conversationName = conversationName;

      try {
         this.conversationType = ConversationModel.ConversationType.valueOf(conversationType.toUpperCase());
      } catch (IllegalArgumentException exception) {
         throw new IllegalStateException("can't find conversation type " + conversationType);
      }
   }

   public String getConversationName() {
      return this.conversationName;
   }

   public ConversationModel.ConversationType getConversationType() {
      return this.conversationType;
   }

   public Conversation getConversation() {
      return Conversation.forName(this.conversationName);
   }

   public void startConversation(NPC npc, Player player) {
      if (!Conversation.exists(this.conversationName)) {
         throw new IllegalStateException("can't find conversation " + this.conversationName);
      }

      if (!ConversationProcessor.isPlayerConversing(player.getUniqueId())) {
         if (this.lastStarted.containsKey(player.getUniqueId())) {
            long lastConversationNanos = System.nanoTime() - this.lastStarted.get(player.getUniqueId());
            if (lastConversationNanos < 1000000000L * this.getConversation().getDelay()) {
               return;
            }
         }

         this.lastStarted.remove(player.getUniqueId());
         if (this.conversationType.canStart(npc, this.getConversation(), player)) {
            new ConversationProcessor(npc, this, player);
            this.lastStarted.put(player.getUniqueId(), System.nanoTime());
         }
      }
   }

   public boolean canRun(NPC npc, Player player) {
      return Stream.of(ConversationModel.ConversationType.values())
         .anyMatch(conversationType1 -> !conversationType1.canStart(npc, this.getConversation(), player));
   }

   private ConversationModel() {
   }

   public enum ConversationType {
      RADIUS {
         @Override
         public boolean canStart(NPC npc, Conversation conversation, Player player) {
            return player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) <= conversation.getRadius();
         }
      },
      CLICK {
         @Override
         public boolean canStart(NPC npc, Conversation conversation, Player player) {
            return true;
         }
      };

      ConversationType() {
      }

      abstract boolean canStart(NPC var1, Conversation var2, Player var3);
   }
}
