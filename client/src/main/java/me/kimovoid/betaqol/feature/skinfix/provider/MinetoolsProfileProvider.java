package me.kimovoid.betaqol.feature.skinfix.provider;

import com.google.gson.Gson;
import me.kimovoid.betaqol.feature.skinfix.PlayerProfile;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class MinetoolsProfileProvider implements ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            UUIDResponse uuidresponse = this.getRequest(httpClient, "https://api.minetools.eu/uuid/" + username, UUIDResponse.class);
            Response mtresponse = this.getRequest(httpClient, "https://api.minetools.eu/profile/" + uuidresponse.id, Response.class);

            boolean slim = false;
            try {
                if (mtresponse.decoded.textures.SKIN.metadata.model.equals("slim")) {
                    slim = true;
                }
            } catch (Exception ignored) {}

            /* Some profiles don't have a skin or cape */
            String skin = processProfile(mtresponse, false);
            String cape = processProfile(mtresponse, true);

            PlayerProfile profile = new PlayerProfile(
                    skin,
                    cape,
                    slim);

            return CompletableFuture.completedFuture(profile);
        } catch (Exception e) {
            CompletableFuture<PlayerProfile> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private <T> T getRequest(HttpClient httpClient, String URL, Class<T> classOfT) throws IOException {
        String response = EntityUtils.toString(httpClient.execute(new HttpGet(URL)).getEntity());
        return new Gson().fromJson(response, classOfT);
    }

    private String processProfile(MinetoolsProfileProvider.Response response, boolean cape) {
        try {
            if (cape) return response.decoded.textures.CAPE.url;
            else return response.decoded.textures.SKIN.url;
        } catch (Exception ex) {
            return null;
        }
    }

    private static class Response {
        private Decoded decoded;
        private static class Decoded {
            private Textures textures;
            private static class Textures {
                private SKIN SKIN;
                private CAPE CAPE;

                private static class SKIN {
                    private String url;
                    private Metadata metadata;

                    private static class Metadata {
                        private String model;
                    }
                }

                private static class CAPE {
                    private String url;
                }
            }
        }
    }

    private static class UUIDResponse {
        private String id;
        private String name;
    }
}