package io.github.gonalez.znpcs.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.znpcs.npc.function.GlowFunction;
import io.github.gonalez.znpcs.utility.GuavaCollectors;

public final class FunctionFactory {
   public static final ImmutableList<NPCFunction> WITHOUT_FUNCTION = ImmutableList.of(
      new NPCFunction.WithoutFunction("look"), new NPCFunction.WithoutFunctionSelfUpdate("holo") {
         @Override
         public void doRunFunction(NPC npc, FunctionContext functionContext) {
            super.doRunFunction(npc, functionContext);
            npc.createHologram();
         }
      }, new NPCFunction.WithoutFunctionSelfUpdate("mirror")
   );
   public static final ImmutableList<NPCFunction> WITH_FUNCTION = ImmutableList.of(new GlowFunction());
   public static final ImmutableList<NPCFunction> ALL = ImmutableList.builder().addAll(WITHOUT_FUNCTION).addAll(WITH_FUNCTION).build();
   public static final ImmutableMap<String, NPCFunction> BY_NAME = ALL.stream()
      .collect(GuavaCollectors.toImmutableMap(NPCFunction::getName, function -> (NPCFunction)function));

   public static NPCFunction findFunctionForName(String name) {
      return (NPCFunction)BY_NAME.get(name);
   }

   public static ImmutableList<NPCFunction> findFunctionsForNpc(NPC npc) {
      return ALL.stream().filter(function -> isTrue(npc, function)).collect(GuavaCollectors.toImmutableList());
   }

   public static boolean isTrue(NPC npc, NPCFunction function) {
      return npc.getNpcPojo().getFunctions().getOrDefault(function.getName(), false);
   }

   public static boolean isTrue(NPC npc, String function) {
      return isTrue(npc, findFunctionForName(function));
   }
}
