package io.github.gonalez.znpcs.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class PlaceholderUtils {
   public static String formatPlaceholders(String str) {
      return !Utils.PLACEHOLDER_SUPPORT
         ? str
         : Bukkit.getOnlinePlayers().stream().findAny().map(player -> PlaceholderAPI.setPlaceholders(player, str)).orElse(str);
   }

   public static String formatPlaceholder(OfflinePlayer offlinePlayer, String str) {
      return !Utils.PLACEHOLDER_SUPPORT ? Utils.toColor(str) : Utils.toColor(PlaceholderAPI.setPlaceholders(offlinePlayer, str));
   }
}
