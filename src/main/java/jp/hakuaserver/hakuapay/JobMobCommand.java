package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobMobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用可能です。");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§c使い方: /jobmob <名前> <スキン名>");
            return true;
        }

        String mobName = args[0];
        String skinName = args[1];
        Location location = player.getLocation();

        // NPCを生成
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, mobName);
        npc.spawn(location);

        // スキンを適用
        npc.data().setPersistent("player-skin-name", skinName);

        player.sendMessage("§a名前 " + mobName + "、スキン " + skinName + " のプレイヤーモブを生成しました！");
        return true;
    }
}
