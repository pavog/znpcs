package io.github.gonalez.znpcs.utility;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.user.ZUser;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadLocalRandom;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Utils {
   public static final Version BUKKIT_VERSION = Version.fromBukkit();
   public static final boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

   public static boolean isPaper() {
      try {
         Class.forName("com.destroystokyo.paper.PaperConfig");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   public static boolean isVersionSameOrNew(String version) {
      return Version.fromString(version).compareTo(BUKKIT_VERSION) <= 0;
   }

   public static boolean isVersionSameOrNew(int version) {
      return isVersionSameOrNew(String.valueOf(version));
   }

   public static String getBukkitPackage() {
      String packageName = Bukkit.getServer().getClass().getPackage().getName();
      String[] split = packageName.split("\\.");
      return split.length > 3 ? split[3] : "";
   }

   public static String toColor(String string) {
      return ChatColor.translateAlternateColorCodes('&', string);
   }

   public static String getWithPlaceholders(String string, Player player) {
      return PlaceholderAPI.setPlaceholders(player, string).replace(ConfigurationConstants.SPACE_SYMBOL, " ");
   }

   public static String randomString(int length) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int index = 0; index < length; index++) {
         stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
      }

      return stringBuilder.toString();
   }

   public static void sendTitle(Player player, String title, String subTitle) {
      player.sendTitle(toColor(title), toColor(subTitle));
   }

   public static void setValue(Object fieldInstance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
      Field f = fieldInstance.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(fieldInstance, value);
   }

   public static void setValue(Object fieldInstance, Object value, Class<?> expectedType) throws NoSuchFieldException, IllegalAccessException {
      for (Field field : fieldInstance.getClass().getDeclaredFields()) {
         if (field.getType() == expectedType) {
            setValue(fieldInstance, field.getName(), value);
         }
      }
   }

   public static Object getValue(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
      Field f = instance.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      return f.get(instance);
   }

   public static void sendPackets(ZUser user, Object... packets) {
      try {
         for (Object packet : packets) {
            if (packet != null) {
               CacheRegistry.SEND_PACKET_METHOD.load().invoke(user.getPlayerConnection(), packet);
            }
         }
      } catch (IllegalAccessException | InvocationTargetException e) {
         e.printStackTrace();
      }
   }
}
