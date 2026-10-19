package io.github.gonalez.znpcs.cache;

import io.github.gonalez.znpcs.utility.Utils;

public enum CachePackage {
   DEFAULT,
   CRAFT_BUKKIT("org.bukkit.craftbukkit"),
   MINECRAFT_SERVER("net.minecraft");

   private final String fixedPackageName;

   CachePackage(String packageName) {
      if (packageName.endsWith("craftbukkit")) {
         String pckg = Utils.getBukkitPackage();
         if (!pckg.isEmpty()) {
            packageName = packageName + "." + Utils.getBukkitPackage();
         }

         this.fixedPackageName = packageName;
      } else {
         this.fixedPackageName = Utils.isVersionSameOrNew(17)
            ? packageName
            : packageName + (packageName.contains("minecraft") ? ".server." + Utils.getBukkitPackage() : "");
      }
   }

   CachePackage() {
      this.fixedPackageName = "";
   }

   public String getForCategory(CacheCategory packetCategory, String extra) {
      return Utils.isVersionSameOrNew(17) ? packetCategory.getPackageName() + (!extra.isEmpty() ? "." + extra : "") : this.fixedPackageName;
   }

   public String getFixedPackageName() {
      return this.fixedPackageName;
   }
}
