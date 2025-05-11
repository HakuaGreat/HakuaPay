package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class AddMoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("hakuapay.addmoney")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§c使い方: /addmoney <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || target.getName() == null) {
            sender.sendMessage("§cプレイヤーが見つかりません。");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("§c金額は正の数値で入力してください。");
            return true;
        }

        UUID uuid = target.getUniqueId();
        HakuaPay.getDatabaseManager().addBalance(uuid, amount);

        sender.sendMessage("§a" + target.getName() + " の所持金に " + amount + " を追加しました。");
        return true;
    }
}
