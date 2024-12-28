package me.kimovoid.betaqol.feature.skinfix.provider;

import com.google.gson.Gson;
import me.kimovoid.betaqol.feature.skinfix.PlayerProfile;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.executor.ExecutorType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class MojangProfileProvider implements ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            HttpClient httpClient = new HttpClient(ExecutorType.AUTO);
            UUIDResponse uuidresponse = this.getRequest(httpClient, "https://api.mojang.com/users/profiles/minecraft/" + username, UUIDResponse.class);
            Response response = this.getRequest(httpClient, "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidresponse.id, Response.class);
            TexturesResponse texturesResponse = this.getTextures(response);

            boolean slim = false;
            try {
                if (texturesResponse.textures.SKIN.metadata.model.equals("slim")) {
                    slim = true;
                }
            } catch (Exception ignored) {}

            /* Some profiles don't have a skin or cape */
            String skin = processProfile(texturesResponse, false);
            String cape = processProfile(texturesResponse, true);

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
        return new Gson().fromJson(httpClient.get(URL).execute().getContentAsString(), classOfT);
    }

    private TexturesResponse getTextures(Response response) {
        String decoded = new String(Base64.getDecoder().decode(response.properties[0].value), StandardCharsets.UTF_8);
        return new Gson().fromJson(decoded, TexturesResponse.class);
    }

    private String processProfile(TexturesResponse response, boolean cape) {
        try {
            if (cape) return response.textures.CAPE.url;
            else return response.textures.SKIN.url;
        } catch (Exception ex) {
            return null;
        }
    }

    private static class Response {
        private Properties[] properties;
        private static class Properties {
            private String name;
            private String value;
        }
    }

    private static class TexturesResponse {
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

    private static class UUIDResponse {
        private String id;
        private String name;
    }
}