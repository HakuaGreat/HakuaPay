package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CheckJobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("hakuapay.checkjob")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§c使い方: /checkjob <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || target.getName() == null) {
            sender.sendMessage("§cプレイヤーが見つかりません。");
            return true;
        }

        UUID uuid = target.getUniqueId();
        String job = HakuaPay.getDatabaseManager().getJob(uuid);

        if (job == null || job.isEmpty()) {
            sender.sendMessage("§a" + target.getName() + " は現在職業に就いていません。");
        } else {
            sender.sendMessage("§a" + target.getName() + " の現在の職業: " + job);
        }

        return true;
    }
}
