package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.utility.Utils;

public enum NamingType {
   DEFAULT {
      @Override
      public String resolve(NPC npc) {
         return Utils.randomString(6);
      }
   };

   private static final int FIXED_LENGTH = 6;

   NamingType() {
   }

   public abstract String resolve(NPC var1);
}
