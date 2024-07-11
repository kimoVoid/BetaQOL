package me.kimovoid.betaqol.feature.networking;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;

public class PlayerInfoHandler implements ClientPlayNetworking.PayloadListener<PlayerInfoPayload> {

    @Override
    public boolean handle(Minecraft minecraft, ClientNetworkHandler handler, PlayerInfoPayload payload) {
        if (payload.isOnline) {
            BetaQOL.INSTANCE.tabPlayers.put(payload.username, payload.ping);
        } else {
            BetaQOL.INSTANCE.tabPlayers.remove(payload.username);
        }
        //minecraft.gui.addChatMessage("Received player " + payload.username + " (" + payload.ping + " ms)");
        return false;
    }
}
