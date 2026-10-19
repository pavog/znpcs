package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class SkinFetchHttp {
   private SkinFetchHttp() {
   }

   public static HttpURLConnection openConnection(SkinFetchHttp.RequestMethod requestMethod, String url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
      connection.setRequestMethod(requestMethod.name());
      connection.setInstanceFollowRedirects(false);
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(10000);
      return connection;
   }

   public static final class HttpResponse {
      private final int statusCode;
      private final byte[] bodyBytes;

      public HttpResponse(int statusCode, byte[] bodyBytes) {
         this.statusCode = statusCode;
         this.bodyBytes = bodyBytes;
      }

      public int getStatusCode() {
         return this.statusCode;
      }

      public byte[] getBodyBytes() {
         return this.bodyBytes;
      }

      public String getBodyAsString() {
         return new String(this.getBodyBytes());
      }

      public JsonElement toJson() {
         return new JsonParser().parse(this.getBodyAsString());
      }
   }

   public enum RequestMethod {
      GET,
      POST;
   }
}
