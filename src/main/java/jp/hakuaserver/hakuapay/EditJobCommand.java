package jp.hakuaserver.hakuapay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EditJobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("hakuapay.editjob")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 4) {
            sender.sendMessage("§c使い方: /editjob <職業名> <アクション> <対象> <報酬>");
            return true;
        }

        String jobName = args[0].toLowerCase();
        String action = args[1].toLowerCase();
        String target = args[2].toLowerCase();
        int reward;

        try {
            reward = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c報酬は数値で指定してください。");
            return true;
        }

        String path = "jobs." + jobName + ".actions." + action + "." + target;
        if (!HakuaPay.getInstance().getConfig().contains("jobs." + jobName)) {
            sender.sendMessage("§c職業 \"" + jobName + "\" は存在しません。");
            return true;
        }

        HakuaPay.getInstance().getConfig().set(path, reward);
        HakuaPay.getInstance().saveConfig();
        sender.sendMessage("§a職業 \"" + jobName + "\" のアクションを更新しました: " + action + " -> " + target + " = " + reward);
        return true;
    }
}
