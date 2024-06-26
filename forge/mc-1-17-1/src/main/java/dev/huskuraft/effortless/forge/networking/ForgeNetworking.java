package dev.huskuraft.effortless.forge.networking;

import org.apache.commons.lang3.tuple.Pair;

import com.google.auto.service.AutoService;

import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.networking.NetByteBuf;
import dev.huskuraft.effortless.api.networking.NetByteBufReceiver;
import dev.huskuraft.effortless.api.networking.Networking;
import dev.huskuraft.effortless.vanilla.core.MinecraftPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.event.EventNetworkChannel;

@AutoService(Networking.class)
public class ForgeNetworking implements Networking {

    public static final EventNetworkChannel CHANNEL;

    static {
        CHANNEL = NetworkRegistry.ChannelBuilder.named(Networking.getChannelId().reference())
                .clientAcceptedVersions(Networking.getCompatibilityVersionStr()::equals)
                .serverAcceptedVersions(Networking.getCompatibilityVersionStr()::equals)
                .networkProtocolVersion(Networking::getCompatibilityVersionStr)
                .eventNetworkChannel();
    }

    @Override
    public void registerClientReceiver(NetByteBufReceiver receiver) {
        ClientNetworking.registerReceiver(receiver);
    }

    @Override
    public void registerServerReceiver(NetByteBufReceiver receiver) {
        ServerNetworking.registerReceiver(receiver);
    }

    @Override
    public void sendToClient(NetByteBuf byteBuf, Player player) {
        ServerNetworking.send(byteBuf, player);
    }

    @Override
    public void sendToServer(NetByteBuf byteBuf, Player player) {
        ClientNetworking.send(byteBuf, player);
    }

    static class ClientNetworking {
        public static void registerReceiver(NetByteBufReceiver receiver) {
            CHANNEL.addListener(event1 -> {
                if (event1.getPayload() != null && event1.getSource().get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
                    receiver.receiveBuffer(new NetByteBuf(event1.getPayload()), MinecraftPlayer.ofNullable(Minecraft.getInstance().player));
                }
            });

        }

        public static void send(NetByteBuf byteBuf, Player player) {
            var minecraftPacket = NetworkDirection.PLAY_TO_SERVER.buildPacket(Pair.of(new FriendlyByteBuf(byteBuf), 0), Networking.getChannelId().reference()).getThis();
            Minecraft.getInstance().getConnection().send(minecraftPacket);
        }
    }

    static class ServerNetworking {
        public static void registerReceiver(NetByteBufReceiver receiver) {
            CHANNEL.addListener(event1 -> {
                if (event1.getPayload() != null && event1.getSource().get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                    receiver.receiveBuffer(new NetByteBuf(event1.getPayload()), MinecraftPlayer.ofNullable(event1.getSource().get().getSender()));
                }
            });
        }

        public static void send(NetByteBuf byteBuf, Player player) {
            var minecraftPacket = NetworkDirection.PLAY_TO_CLIENT.buildPacket(Pair.of(new FriendlyByteBuf(byteBuf), 0), Networking.getChannelId().reference()).getThis();
            ((ServerPlayer) player.reference()).connection.send(minecraftPacket);
        }
    }

}
