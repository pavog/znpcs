package io.github.gonalez.znpcs.npc;

public interface FunctionContext {
   NPC getNPC();

   class ContextWithValue extends FunctionContext.DefaultContext implements FunctionContext.WithValue {
      private final String value;

      public ContextWithValue(NPC npc, String value) {
         super(npc);
         this.value = value;
      }

      @Override
      public String getValue() {
         return this.value;
      }
   }

   class DefaultContext implements FunctionContext {
      private final NPC npc;

      public DefaultContext(NPC npc) {
         this.npc = npc;
      }

      @Override
      public NPC getNPC() {
         return this.npc;
      }
   }

   interface WithValue extends FunctionContext {
      String getValue();
   }
}
