package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.jetbrains.annotations.Nullable;

public class MojangNameSkinFetch extends SkinFetcherServer {
   private static final String QUERY_PLAYER_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
   private static final String QUERY_PLAYER_SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

   @Override
   public String getName() {
      return "Mojang";
   }

   @Override
   HttpURLConnection createConnection(String name) throws IOException {
      SkinFetchHttp.HttpResponse response = this.readHttpResponse(
         SkinFetchHttp.openConnection(SkinFetchHttp.RequestMethod.GET, String.format("https://api.mojang.com/users/profiles/minecraft/%s", name))
      );
      return SkinFetchHttp.openConnection(
         SkinFetchHttp.RequestMethod.GET,
         String.format(
            "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", response.toJson().getAsJsonObject().get("id").getAsString()
         )
      );
   }

   @Nullable
   @Override
   SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response) {
      JsonObject properties = response.toJson().getAsJsonObject().getAsJsonArray("properties").get(0).getAsJsonObject();
      return new SkinProperties(properties.get("value").getAsString(), properties.get("signature").getAsString());
   }
}
