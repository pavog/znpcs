package io.github.gonalez.znpcs.npc.packet;

import java.util.Arrays;

public enum ValueType {
   ARGUMENTS {
      @Override
      String resolve(String keyName, Object[] args) {
         if (args.length == 0) {
            throw new IllegalArgumentException("invalid size, must be > 0");
         } else {
            return keyName + Arrays.hashCode(args);
         }
      }
   },
   DEFAULT {
      @Override
      String resolve(String keyName, Object[] args) {
         return keyName;
      }
   };

   ValueType() {
   }

   abstract String resolve(String var1, Object[] var2);
}
