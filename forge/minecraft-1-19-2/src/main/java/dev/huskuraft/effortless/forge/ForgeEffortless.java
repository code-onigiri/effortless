package dev.huskuraft.effortless.forge;

import dev.huskuraft.effortless.Effortless;
import dev.huskuraft.effortless.api.platform.Platform;
import dev.huskuraft.effortless.vanilla.adapters.*;
import dev.huskuraft.effortless.vanilla.platform.MinecraftCommonPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.EventNetworkChannel;
import net.minecraftforge.network.NetworkDirection;

import java.nio.file.Path;

@Mod(Effortless.MOD_ID)
@Mod.EventBusSubscriber(modid = Effortless.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeEffortless extends Effortless {

    public static EventNetworkChannel CHANNEL;

//    public static EventNetworkChannel networkChannel;
//
//    public static EventNetworkChannel getNetworkChannel() {
//        return networkChannel;
//    }

    public ForgeEffortless() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        CHANNEL = ChannelBuilder.named(MinecraftResource.toMinecraftResource(getChannel().getChannelId()))
                .acceptedVersions((status, version) -> true)
                .optional()
                .networkProtocolVersion(getChannel().getCompatibilityVersion())
                .eventNetworkChannel();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgeEffortlessClient::new);
    }

    @Override
    public String getLoaderName() {
        return "Forge";
    }

    @Override
    public String getLoaderVersion() {
        return FMLLoader.versionInfo().forgeVersion();
    }

    @Override
    public String getGameVersion() {
        return FMLLoader.versionInfo().mcVersion();
    }

    @Override
    public Path getGameDir() {
        return FMLLoader.getGamePath();
    }

    @Override
    public Path getConfigDir() {
        return FMLLoader.getGamePath().resolve("config");
    }

    @Override
    public Platform getPlatform() {
        return new MinecraftCommonPlatform();
    }

    @Override
    public Environment getEnvironment() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> Environment.CLIENT;
            case DEDICATED_SERVER -> Environment.SERVER;
        };
    }

    @Override
    public boolean isDevelopment() {
        return !FMLLoader.isProduction();
    }


    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        getEventRegistry().getRegisterNetworkEvent().invoker().onRegisterNetwork(receiver -> {
            ForgeEffortless.CHANNEL.addListener(event1 -> {
                if (event1.getPayload() != null && event1.getSource().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                    try {
                        receiver.receiveBuffer(MinecraftBuffer.fromMinecraftBuffer(event1.getPayload()), MinecraftPlayer.fromMinecraftPlayer(event1.getSource().getSender()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return (buffer, player) -> {
                ((ServerPlayer) MinecraftPlayer.toMinecraftPlayer(player)).connection.send(NetworkDirection.PLAY_TO_CLIENT.buildPacket(MinecraftBuffer.toMinecraftBuffer(buffer), ForgeEffortless.CHANNEL.getName()).getThis());
            };
        });
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        getEventRegistry().getPlayerChangeWorldEvent().invoker().onPlayerChangeWorld(MinecraftPlayer.fromMinecraftPlayer(event.getEntity()), MinecraftWorld.fromMinecraftWorld(event.getEntity().getServer().getLevel(event.getFrom())), MinecraftWorld.fromMinecraftWorld(event.getEntity().getServer().getLevel(event.getTo())));
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        getEventRegistry().getPlayerRespawnEvent().invoker().onPlayerRespawn(MinecraftPlayer.fromMinecraftPlayer(event.getEntity()), MinecraftPlayer.fromMinecraftPlayer(event.getEntity()), event.isEndConquered());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        getEventRegistry().getPlayerLoggedInEvent().invoker().onPlayerLoggedIn(MinecraftPlayer.fromMinecraftPlayer(event.getEntity()));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        getEventRegistry().getPlayerLoggedOutEvent().invoker().onPlayerLoggedOut(MinecraftPlayer.fromMinecraftPlayer(event.getEntity()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        getEventRegistry().getServerStartingEvent().invoker().onServerStarting(MinecraftServer.fromMinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onSeverrStarted(ServerStartedEvent event) {
        getEventRegistry().getServerStartedEvent().invoker().onServerStarted(MinecraftServer.fromMinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        getEventRegistry().getServerStoppingEvent().invoker().onServerStopping(MinecraftServer.fromMinecraftServer(event.getServer()));
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        getEventRegistry().getServerStoppedEvent().invoker().onServerStopped(MinecraftServer.fromMinecraftServer(event.getServer()));
    }


}