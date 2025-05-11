package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCManager implements Listener{

    // NPCを生成
    public static void createJobNPC(String npcName, String skinName, Location location) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, npcName);
        npc.spawn(location);

        // スキンを設定
        npc.getOrAddTrait(SkinTrait.class).setSkinName(skinName);
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

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        if (npc == null) return; // NPCでない場合は処理を終了

        Player player = event.getPlayer();
        player.performCommand("job gui"); // GUIを開くコマンドを実行
    }
}


