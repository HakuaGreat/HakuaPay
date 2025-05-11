package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

public class NPCManager {

    // NPCを生成
    public static void createJobNPC(String npcName, String skinName, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, npcName);
        npc.spawn(location);
        npc.data().setPersistent("player-skin-name", skinName); // スキンを設定
    }

    // NPCを削除
    public static boolean deleteJobNPC(String npcName) {
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                npc.destroy();
                return true;
            }
        }
        return false;
    }
}
