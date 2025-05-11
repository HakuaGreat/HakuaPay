package jp.hakuaserver.hakuapay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AddJobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("hakuapay.addjob")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§c使い方: /addjob <職業名>");
            return true;
        }

        String jobName = args[0].toLowerCase();
        if (HakuaPay.getInstance().getConfig().contains("jobs." + jobName)) {
            sender.sendMessage("§c職業 \"" + jobName + "\" は既に存在します。");
            return true;
        }

        HakuaPay.getInstance().getConfig().set("jobs." + jobName + ".actions", null);
        HakuaPay.getInstance().saveConfig();
        sender.sendMessage("§a職業 \"" + jobName + "\" を追加しました。");
        return true;
    }
}
