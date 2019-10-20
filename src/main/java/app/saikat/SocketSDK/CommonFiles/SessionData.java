package app.saikat.SocketSDK.CommonFiles;

import java.util.UUID;

public final class SessionData {

    private static ThreadLocal<UUID> sessionInfo = new ThreadLocal<>();

    public static UUID getCurrentSession() {
        return sessionInfo.get();
    }

    public static void setCurentSession(UUID uuid) {
        sessionInfo.set(uuid);
    }

    public static void removeSession() {
        sessionInfo.remove();
    }
}