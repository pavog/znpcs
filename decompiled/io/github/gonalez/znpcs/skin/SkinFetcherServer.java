package io.github.gonalez.znpcs.skin;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.annotation.Nullable;

public abstract class SkinFetcherServer {
   public abstract String getName();

   abstract HttpURLConnection createConnection(String var1) throws IOException;

   @Nullable
   abstract SkinProperties decodeProperties(SkinFetchHttp.HttpResponse var1);

   SkinFetchHttp.HttpResponse readHttpResponse(HttpURLConnection urlConnection) throws IOException {
      int code = urlConnection.getResponseCode();
      if (code != 200) {
         throw new IOException();
      } else {
         return new SkinFetchHttp.HttpResponse(code, ByteStreams.toByteArray(urlConnection.getInputStream()));
      }
   }
}
