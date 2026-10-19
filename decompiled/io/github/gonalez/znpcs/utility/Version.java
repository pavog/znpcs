package io.github.gonalez.znpcs.utility;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class Version implements Comparable<Version>, Serializable {
   private static final Pattern VERSION_PATTERN = Pattern.compile("1\\.(\\d+)(?:\\.(\\d+))?");
   public static final Version SERVER_VERSION = fromString(Bukkit.getBukkitVersion());
   static Version MAX_VERSION = new Version(Integer.MAX_VALUE, 0);
   final int minor;
   final int patch;

   private Version(int minor, int patch) {
      this.minor = minor;
      this.patch = patch;
   }

   static Version fromBukkit() {
      String v = Bukkit.getBukkitVersion();
      Matcher m = VERSION_PATTERN.matcher(v);
      if (!m.find()) {
         throw new IllegalArgumentException("Invalid version: " + v);
      }

      int minor = Integer.parseInt(m.group(1));
      int patch = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
      return new Version(minor, patch);
   }

   public static Version fromString(String versionString) {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(versionString));
      fromBukkit();
      String[] parts = versionString.replaceAll("[^0-9.]", "").split("\\.");
      int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
      int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      return new Version(major, minor);
   }

   public int compareTo(Version other) {
      return this.minor != other.minor ? Integer.compare(this.minor, other.minor) : Integer.compare(this.patch, other.patch);
   }

   @Override
   public String toString() {
      return "Version{minor=" + this.minor + ", patch=" + this.patch + '}';
   }

   public enum Type {
      SPIGOT,
      PAPER_SPIGOT;
   }
}
