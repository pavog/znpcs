package io.github.gonalez.znpcs.npc;

public abstract class NPCFunction {
   private final String name;

   public NPCFunction(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   protected abstract boolean allow(NPC var1);

   protected abstract NPCFunction.ResultType runFunction(NPC var1, FunctionContext var2);

   public void doRunFunction(NPC npc, FunctionContext functionContext) {
      if (this.allow(npc)) {
         NPCFunction.ResultType resultType = this.runFunction(npc, functionContext);
         if (resultType == NPCFunction.ResultType.SUCCESS) {
            npc.getNpcPojo().getFunctions().put(this.getName(), !this.isTrue(npc));
         }
      }
   }

   protected void resolve(NPC npc) {
      throw new IllegalStateException("resolve is not implemented.");
   }

   public boolean isTrue(NPC npc) {
      return FunctionFactory.isTrue(npc, this);
   }

   public enum ResultType {
      SUCCESS,
      FAIL;
   }

   public static class WithoutFunction extends NPCFunction {
      public WithoutFunction(String name) {
         super(name);
      }

      @Override
      protected NPCFunction.ResultType runFunction(NPC npc, FunctionContext functionContext) {
         return NPCFunction.ResultType.SUCCESS;
      }

      @Override
      protected boolean allow(NPC npc) {
         return true;
      }

      @Override
      protected void resolve(NPC npc) {
      }
   }

   public static class WithoutFunctionSelfUpdate extends NPCFunction.WithoutFunction {
      public WithoutFunctionSelfUpdate(String name) {
         super(name);
      }

      @Override
      protected NPCFunction.ResultType runFunction(NPC npc, FunctionContext functionContext) {
         npc.deleteViewers();
         return NPCFunction.ResultType.SUCCESS;
      }
   }
}
