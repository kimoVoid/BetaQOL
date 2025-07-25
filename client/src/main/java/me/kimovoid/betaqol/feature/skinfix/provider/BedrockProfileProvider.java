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

public class BedrockProfileProvider implements ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            XUIDResponse xuidResp = this.getRequest(httpClient, "https://api.geysermc.org/v2/$xbox/xuid/" + username.substring(1), XUIDResponse.class);
            Response resp = this.getRequest(httpClient, "https://api.geysermc.org/v2/skin/" + xuidResp.xuid, Response.class);

            PlayerProfile profile = new PlayerProfile(
                    "https://textures.minecraft.net/texture/" + resp.texture_id,
                    null,
                    resp.is_steve.equals("false"));

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

    private static class Response {
        private String is_steve;
        private String texture_id;
    }

    private static class XUIDResponse {
        private String xuid;
    }
}