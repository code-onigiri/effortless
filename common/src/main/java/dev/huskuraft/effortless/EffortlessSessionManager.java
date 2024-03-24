package dev.huskuraft.effortless;

import java.util.concurrent.atomic.AtomicReference;

import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.core.World;
import dev.huskuraft.effortless.api.platform.Platform;
import dev.huskuraft.effortless.api.platform.Server;
import dev.huskuraft.effortless.networking.packets.session.SessionConfigPacket;
import dev.huskuraft.effortless.networking.packets.session.SessionPacket;
import dev.huskuraft.effortless.session.Session;
import dev.huskuraft.effortless.session.SessionManager;
import dev.huskuraft.effortless.session.config.SessionConfig;

public final class EffortlessSessionManager implements SessionManager {

    private final Effortless entrance;

    private final AtomicReference<Session> serverSession = new AtomicReference<>();
    private final AtomicReference<Session> clientSession = new AtomicReference<>();

    public EffortlessSessionManager(Effortless entrance) {
        this.entrance = entrance;

        getEntrance().getEventRegistry().getPlayerChangeWorldEvent().register(this::onPlayerChangeWorld);
        getEntrance().getEventRegistry().getPlayerRespawnEvent().register(this::onPlayerRespawn);
        getEntrance().getEventRegistry().getPlayerLoggedInEvent().register(this::onPlayerLoggedIn);
        getEntrance().getEventRegistry().getPlayerLoggedOutEvent().register(this::onPlayerLoggedOut);
    }

    private Effortless getEntrance() {
        return entrance;
    }


    @Override
    public void onSession(Session session, Player player) {
        serverSession.set(session);
    }

    @Override
    public void onSessionConfig(SessionConfig sessionConfig, Player player) {
        getEntrance().getSessionConfigStorage().set(sessionConfig);

        Server server = player.getServer();
        for (var serverPlayer : server.getPlayerList().getPlayers()) {
            updateSessionConfig(serverPlayer);
        }
    }

    @Override
    public boolean isServerSessionValid() {
        return serverSession.get() != null;
    }

    @Override
    public SessionStatus getSessionStatus() {
        if (serverSession.get() == null && clientSession.get() == null) {
            return SessionStatus.BOTH_NOT_LOADED;
        }
        if (serverSession.get() == null) {
            return SessionStatus.SERVER_NOT_LOADED;
        }
        if (clientSession.get() == null) {
            return SessionStatus.CLIENT_NOT_LOADED;
        }
        var serverMod = serverSession.get().mods().stream().filter(mod -> mod.getId().equals(Effortless.MOD_ID)).findFirst().orElseThrow();
        var clientMod = clientSession.get().mods().stream().filter(mod -> mod.getId().equals(Effortless.MOD_ID)).findFirst().orElseThrow();

        if (!serverMod.getVersionStr().equals(clientMod.getVersionStr())) {
            return SessionStatus.PROTOCOL_VERSION_MISMATCH;
        }
        return SessionStatus.SUCCESS;
    }

    @Override
    public Session getLastSession() {
        var platform = Platform.getInstance();
        var protocolVersion = Effortless.PROTOCOL_VERSION;
        return new Session(
                platform.getLoaderType(),
                platform.getLoaderVersion(),
                platform.getGameVersion(),
                platform.getRunningMods(),
                protocolVersion

        );
    }

    @Override
    public SessionConfig getLastSessionConfig() {
        return getEntrance().getSessionConfigStorage().get();
    }

    private void onPlayerChangeWorld(Player player, World origin, World destination) {
    }

    private void onPlayerRespawn(Player oldPlayer, Player newPlayer, boolean alive) {
    }

    private void onPlayerLoggedIn(Player player) {
        updateSession(player);
        updateSessionConfig(player);
    }

    private void onPlayerLoggedOut(Player player) {
    }

    private void updateSession(Player player) {
        getEntrance().getChannel().sendPacket(new SessionPacket(getLastSession()), player);
    }

    private void updateSessionConfig(Player player) {
        getEntrance().getChannel().sendPacket(new SessionConfigPacket(getLastSessionConfig()), player);
    }

}
