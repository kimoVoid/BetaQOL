package me.kimovoid.betaqol.feature.skinfix.provider;

import com.google.gson.Gson;
import me.kimovoid.betaqol.feature.skinfix.PlayerProfile;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.executor.ExecutorType;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BedrockProfileProvider implements ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            HttpClient httpClient = new HttpClient(ExecutorType.AUTO);
            XUIDResponse xuidResp = this.getRequest(httpClient, "https://api.geysermc.org/v2/xbox/xuid/" + username.substring(1), XUIDResponse.class);
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
        return new Gson().fromJson(httpClient.get(URL).execute().getContentAsString(), classOfT);
    }

    private static class Response {
        private String is_steve;
        private String texture_id;
    }

    private static class XUIDResponse {
        private String xuid;
    }
}