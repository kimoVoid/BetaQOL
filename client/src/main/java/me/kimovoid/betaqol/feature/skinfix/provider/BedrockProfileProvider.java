package me.kimovoid.betaqol.feature.skinfix.provider;

import com.google.gson.Gson;
import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.betaqol.feature.skinfix.PlayerProfile;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.executor.ExecutorType;
import net.lenni0451.commons.httpclient.model.HttpHeader;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class BedrockProfileProvider implements ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            //BetaQOL.LOGGER.info("Trying to fetch bedrock player {}...", username.substring(1));
            HttpClient httpClient = new HttpClient(ExecutorType.AUTO);
            Response resp = this.getRequest(httpClient, "https://mcprofile.io/api/v1/bedrock/gamertag/" + username.substring(1), Response.class);

            String skin = resp.skin;
            PlayerProfile profile = new PlayerProfile(
                    skin,
                    null,
                    false);

            return CompletableFuture.completedFuture(profile);
        } catch (Exception e) {
            CompletableFuture<PlayerProfile> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private <T> T getRequest(HttpClient httpClient, String URL, Class<T> classOfT) throws IOException {
        return new Gson().fromJson(httpClient
                        .get(URL)
                        .appendHeader(new HttpHeader(
                                "x-api-key",
                                "b2f2f684-750c-49db-8a1c-e28e59df6fec"
                        )).execute()
                        .getContentAsString(),
                classOfT);
    }

    private static class Response {
        private String skin;
    }
}