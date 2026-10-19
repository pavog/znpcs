package io.github.gonalez.znpcs.npc.function;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.FunctionContext;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCFunction;

public class GlowFunction extends NPCFunction {
   public GlowFunction() {
      super("glow");
   }

   @Override
   protected NPCFunction.ResultType runFunction(NPC npc, FunctionContext functionContext) {
      if (!(functionContext instanceof FunctionContext.ContextWithValue)) {
         throw new IllegalStateException("invalid context type, " + functionContext.getClass().getSimpleName() + ", expected ContextWithValue.");
      }

      String glowColorName = ((FunctionContext.ContextWithValue)functionContext).getValue();

      try {
         Object glowColor = CacheRegistry.ENUM_CHAT_FORMAT_FIND
            .load()
            .invoke(null, glowColorName != null && !glowColorName.isEmpty() ? glowColorName : "WHITE");
         if (glowColor == null) {
            return NPCFunction.ResultType.FAIL;
         }

         npc.getNpcPojo().setGlowName(glowColorName);
         npc.setGlowColor(glowColor);
         CacheRegistry.SET_DATA_WATCHER_METHOD
            .load()
            .invoke(
               CacheRegistry.GET_DATA_WATCHER_METHOD.load().invoke(npc.getNmsEntity()),
               CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR.load().newInstance(0, CacheRegistry.DATA_WATCHER_REGISTER_FIELD.load()),
               Byte.valueOf((byte)(!FunctionFactory.isTrue(npc, this) ? 64 : 0))
            );
         npc.getPackets().getProxyInstance().update(npc.getPackets());
         npc.deleteViewers();
         return NPCFunction.ResultType.SUCCESS;
      } catch (ReflectiveOperationException e) {
         return NPCFunction.ResultType.FAIL;
      }
   }

   @Override
   protected boolean allow(NPC npc) {
      return npc.getPackets().getProxyInstance().allowGlowColor();
   }

   @Override
   public void resolve(NPC npc) {
      this.runFunction(npc, new FunctionContext.ContextWithValue(npc, npc.getNpcPojo().getGlowName()));
   }
}
