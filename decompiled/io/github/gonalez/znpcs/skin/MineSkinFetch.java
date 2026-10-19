package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import org.jetbrains.annotations.Nullable;

public class MineSkinFetch extends SkinFetcherServer {
   @Override
   public String getName() {
      return "MineSkin";
   }

   @Override
   HttpURLConnection createConnection(String name) throws IOException {
      HttpURLConnection connection = SkinFetchHttp.openConnection(SkinFetchHttp.RequestMethod.POST, "https://api.mineskin.org/generate/url");
      connection.setDoOutput(true);
      DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

      try {
         outputStream.writeBytes("url=" + URLEncoder.encode(name, "UTF-8"));
      } catch (Throwable var7) {
         try {
            outputStream.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      outputStream.close();
      return connection;
   }

   @Nullable
   @Override
   SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response) {
      JsonObject properties = response.toJson().getAsJsonObject().getAsJsonObject("data").getAsJsonObject("texture");
      return new SkinProperties(properties.get("value").getAsString(), properties.get("signature").getAsString());
   }
}
