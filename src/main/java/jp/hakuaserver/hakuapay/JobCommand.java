package jp.hakuaserver.hakuapay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用可能です。");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§c使い方: /job <職業名>");
            return true;
        }

        String job = args[0];
        UUID uuid = player.getUniqueId();

        HakuaPay.getDatabaseManager().setJob(uuid, job);
        player.sendMessage("§aあなたの職業を " + job + " に設定しました！");
        return true;
    }
}