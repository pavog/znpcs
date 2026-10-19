package io.github.gonalez.znpcs.npc.event;

public enum ClickType {
   RIGHT,
   LEFT,
   DEFAULT;

   public static ClickType forName(String clickName) {
      if (clickName.startsWith("INTERACT")) {
         return RIGHT;
      } else {
         return clickName.startsWith("ATTACK") ? LEFT : DEFAULT;
      }
   }
}
