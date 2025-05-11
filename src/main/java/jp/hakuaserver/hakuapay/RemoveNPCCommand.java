package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveNPCCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("hakuapay.removenpc")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§c使い方: /removenpc <NPC名>");
            return true;
        }

        String npcName = args[0];
        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                npc.destroy(); // NPCを削除
                sender.sendMessage("§aNPC \"" + npcName + "\" を削除しました。");
                return true;
            }
        }

        sender.sendMessage("§cNPC \"" + npcName + "\" が見つかりませんでした。");
        return true;
    }
}
