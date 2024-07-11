package me.kimovoid.betaqol.feature.skinfix;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.util.HTTP;

import java.net.Proxy;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ProfileProvider {

    public Future<PlayerProfile> getProfile(String username) {
        try {
            UUIDResponse uuidresponse = HTTP.makeRequest(Proxy.NO_PROXY, URI.create("https://api.minetools.eu/uuid/" + username), null, UUIDResponse.class);
            Response mtresponse = HTTP.makeRequest(Proxy.NO_PROXY, URI.create("https://api.minetools.eu/profile/" + uuidresponse.id), null, Response.class);

            GameProfile.TextureModel model = GameProfile.TextureModel.NORMAL;
            try {
                if (mtresponse.decoded.textures.SKIN.metadata.model.equals("slim")) {
                    model = GameProfile.TextureModel.SLIM;
                }
            } catch (Exception ignored) {}

            /* Some profiles don't have a skin or cape */
            String skin = processUrl(mtresponse, false);
            String cape = processUrl(mtresponse, true);

            PlayerProfile profile = new PlayerProfile(
                    skin,
                    cape,
                    model);

            return CompletableFuture.completedFuture(profile);
        } catch (RequestException e) {
            CompletableFuture<PlayerProfile> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private String processUrl(Response response, boolean cape) {
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
