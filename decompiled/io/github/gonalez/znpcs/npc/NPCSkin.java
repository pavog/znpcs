package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.utility.Utils;

public class NPCSkin {
   private static final String[] EMPTY_ARRAY = new String[]{"", ""};
   private static final int LAYER_INDEX = NPCSkin.SkinLayerValues.findLayerByVersion();
   private final String texture;
   private final String signature;

   protected NPCSkin(String... values) {
      if (values.length < 1) {
         throw new IllegalArgumentException("Length cannot be zero or negative.");
      }

      this.texture = values[0];
      this.signature = values[1];
   }

   public String getTexture() {
      return this.texture;
   }

   public String getSignature() {
      return this.signature;
   }

   public int getLayerIndex() {
      return LAYER_INDEX;
   }

   public static NPCSkin forValues(String... values) {
      return new NPCSkin(values.length > 0 ? values : EMPTY_ARRAY);
   }

   enum SkinLayerValues {
      V8("8", 10),
      V9("10", 13),
      V14("14", 15),
      V16("15", 16),
      V17("16", 17),
      V18("17", 17),
      V26("20.6", 17),
      V219("21.9", 15),
      V211("21.11", 16);

      final String minVersion;
      final int layerValue;

      SkinLayerValues(String minVersion, int layerValue) {
         this.minVersion = minVersion;
         this.layerValue = layerValue;
      }

      static int findLayerByVersion() {
         int value = V8.layerValue;

         for (NPCSkin.SkinLayerValues skinLayerValue : values()) {
            if (Utils.isVersionSameOrNew(skinLayerValue.minVersion)) {
               value = skinLayerValue.layerValue;
            }
         }

         return value;
      }
   }
}
