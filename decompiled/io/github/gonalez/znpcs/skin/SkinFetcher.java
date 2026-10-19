package io.github.gonalez.znpcs.skin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.concurrent.Executor;

public class SkinFetcher {
   private final Executor executor;
   private final ImmutableList<SkinFetcherServer> skinFetcherServers;

   public SkinFetcher(Executor executor, ImmutableList<SkinFetcherServer> skinFetcherServers) {
      this.executor = executor;
      this.skinFetcherServers = (ImmutableList<SkinFetcherServer>)Preconditions.checkNotNull(skinFetcherServers);
   }

   public void fetchSkin(String name, SkinFetcher.SkinFetchListener listener) {
      this.executor.execute(() -> {
         LinkedList<SkinFetcherServer> serverQueue = new LinkedList<>(this.skinFetcherServers);
         this.fetchSkinInternal(serverQueue, name, listener);
      });
   }

   private void fetchSkinInternal(LinkedList<SkinFetcherServer> queue, String name, SkinFetcher.SkinFetchListener listener) {
      if (queue.isEmpty()) {
         listener.onError(new IOException("All SkinFetcherServers failed."));
      } else {
         SkinFetcherServer server = queue.poll();
         HttpURLConnection urlConnection = null;

         try {
            urlConnection = server.createConnection(name);
            SkinFetchHttp.HttpResponse response = server.readHttpResponse(urlConnection);
            SkinProperties skinProperties = server.decodeProperties(response);
            listener.onSuccess(server, skinProperties);
         } catch (IOException e) {
            if (urlConnection != null) {
               urlConnection.disconnect();
            }

            this.fetchSkinInternal(queue, name, listener);
         } finally {
            if (urlConnection != null) {
               urlConnection.disconnect();
            }
         }
      }
   }

   public interface SkinFetchListener {
      default void onSuccess(SkinFetcherServer server, SkinProperties skinProperties) {
      }

      default void onError(Throwable throwable) {
      }
   }
}
