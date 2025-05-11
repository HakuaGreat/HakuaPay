package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HakuaPayCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            // 引数なしの場合は `check` サブコマンドを実行
            return handleCheckMoney(sender, new String[]{sender.getName()});
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "add" -> handleAddMoney(sender, args);
            case "set" -> handleSetMoney(sender, args);
            case "check" -> handleCheckMoney(sender, args);
            case "send" -> handleSendMoney(sender, args);
            default -> {
                sender.sendMessage("§c不明なサブコマンドです: " + subCommand);
                yield true;
            }
        };
    }

    private boolean handleAddMoney(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hakuapay.addmoney")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("§c使い方: /hakuapay add <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || target.getName() == null) {
            sender.sendMessage("§cプレイヤーが見つかりません。");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
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

    private boolean handleSetMoney(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hakuapay.setmoney")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("§c使い方: /hakuapay set <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || target.getName() == null) {
            sender.sendMessage("§cプレイヤーが見つかりません。");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("§c金額は0以上の数値で入力してください。");
            return true;
        }

        UUID uuid = target.getUniqueId();
        HakuaPay.getDatabaseManager().setBalance(uuid, amount);

        sender.sendMessage("§a" + target.getName() + " の所持金を " + amount + " に設定しました。");
        return true;
    }

    private boolean handleCheckMoney(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hakuapay.checkmoney")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§c使い方: /hakuapay check <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || target.getName() == null) {
            sender.sendMessage("§cプレイヤーが見つかりません。");
            return true;
        }

        UUID uuid = target.getUniqueId();
        int balance = HakuaPay.getDatabaseManager().getBalance(uuid);

        sender.sendMessage("§a" + target.getName() + " の残高: " + balance + " 円");
        return true;
    }

    private boolean handleSendMoney(CommandSender sender, String[] args) {

        Player player = (Player) sender;

        if (!player.hasPermission("hakuapay.sendmoney")) {
            player.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 3) {
            player.sendMessage("§c使い方: /hakuapay send <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§c指定されたプレイヤーが見つかりません。");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("§c金額は正の数値で入力してください。");
            return true;
        }

        UUID senderUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (!HakuaPay.getDatabaseManager().subtractBalance(senderUUID, amount)) {
            player.sendMessage("§c残高が不足しています。");
            return true;
        }

        HakuaPay.getDatabaseManager().addBalance(targetUUID, amount);

        player.sendMessage("§a" + target.getName() + " に " + amount + " 円を送金しました。");
        target.sendMessage("§a" + player.getName() + " から " + amount + " 円を受け取りました。");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("hakuapay.addmoney")) completions.add("add");
            if (sender.hasPermission("hakuapay.setmoney")) completions.add("set");
            if (sender.hasPermission("hakuapay.checkmoney")) completions.add("check");
            if (sender.hasPermission("hakuapay.sendmoney")) completions.add("send");
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }
        return completions;
    }
}