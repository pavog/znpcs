package io.github.gonalez.znpcs.utility;

import com.google.common.base.Preconditions;
import javax.annotation.CheckForNull;

public final class VersionRange {
   private final Version minVersion;
   private final Version maxVersion;

   private VersionRange(Version minVersion, Version maxVersion) {
      this.minVersion = (Version)Preconditions.checkNotNull(minVersion);
      this.maxVersion = (Version)Preconditions.checkNotNull(maxVersion);
   }

   public boolean contains() {
      return this.rangeContains(Version.SERVER_VERSION);
   }

   public static VersionRange min(Version version) {
      return of(version, Version.MAX_VERSION);
   }

   public static VersionRange of(Version version) {
      return of(version, version);
   }

   public static VersionRange of(Version minVersion, Version maxVersion) {
      return new VersionRange(minVersion, maxVersion);
   }

   int compare(Version version1, Version version2) {
      return version1.compareTo(version2);
   }

   public boolean rangeContains(@CheckForNull Version version) {
      return version != null && this.compare(this.minVersion, version) <= 0 && this.compare(this.maxVersion, version) >= 0;
   }
}
