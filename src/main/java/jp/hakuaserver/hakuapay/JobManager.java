package jp.hakuaserver.hakuapay;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobManager {

    private static final Map<UUID, String> playerJobs = new HashMap<>();

    // プレイヤーの職業を取得
    public static String getPlayerJob(UUID playerUUID) {
        return playerJobs.get(playerUUID);
    }

    // プレイヤーの職業を設定
    public static void setPlayerJob(UUID playerUUID, String job) {
        playerJobs.put(playerUUID, job);
    }

    // プレイヤーの職業を削除
    public static void removePlayerJob(UUID playerUUID) {
        playerJobs.remove(playerUUID);
    }

    // 全プレイヤーの職業を取得
    public static Map<UUID, String> getAllJobs() {
        return new HashMap<>(playerJobs);
    }
}
