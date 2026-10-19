package io.github.gonalez.znpcs.skin;

public final class SkinProperties {
   private final String value;
   private final String signature;

   public SkinProperties(String value, String signature) {
      this.value = value;
      this.signature = signature;
   }

   public String getValue() {
      return this.value;
   }

   public String getSignature() {
      return this.signature;
   }
}
