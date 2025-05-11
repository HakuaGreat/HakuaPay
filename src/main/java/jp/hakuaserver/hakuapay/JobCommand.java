package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c使い方: /job <add|remove|list|myjob|createnpc|deletenpc|addjob|gui>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "add" -> handleAddJob(sender, args);
            case "remove" -> handleRemoveJob(sender, args);
            case "list" -> handleListJobs(sender, args);
            case "myjob" -> handleMyJob(sender, args);
            case "createnpc" -> handleCreateNPC(sender, args);
            case "deletenpc" -> handleDeleteNPC(sender, args);
            case "addjob" -> handleAddJobCommand(sender, args);
            case "gui" -> handleOpenGUI(sender);
            default -> {
                sender.sendMessage("§c不明なサブコマンドです: " + subCommand);
                yield true;
            }
        };
    }

    private boolean handleAddJob(CommandSender sender, String[] args) {
        if (!sender.hasPermission("job.add")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c使い方: /job add <jobName>");
            return true;
        }

        String jobName = args[1];
        sender.sendMessage("§aジョブ " + jobName + " を追加しました。");
        return true;
    }

    private boolean handleRemoveJob(CommandSender sender, String[] args) {
        if (!sender.hasPermission("job.remove")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c使い方: /job remove <jobName>");
            return true;
        }

        String jobName = args[1];
        sender.sendMessage("§aジョブ " + jobName + " を削除しました。");
        return true;
    }

    private boolean handleListJobs(CommandSender sender, String[] args) {
        if (!sender.hasPermission("job.list")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        sender.sendMessage("§a登録されているジョブ: job1, job2, job3");
        return true;
    }

    private boolean handleMyJob(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行可能です。");
            return true;
        }

        String job = JobManager.getPlayerJob(player.getUniqueId());
        if (job == null) {
            player.sendMessage("§cあなたはまだ職業についていません。");
        } else {
            player.sendMessage("§aあなたの職業: " + job);
        }
        return true;
    }

    private boolean handleCreateNPC(CommandSender sender, String[] args) {
        if (!sender.hasPermission("job.createnpc")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行可能です。");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§c使い方: /job createnpc <npcName> <skinName>");
            return true;
        }

        String npcName = args[1];
        String skinName = args[2];
        NPCManager.createJobNPC(npcName, skinName, player.getLocation());
        sender.sendMessage("§aNPC " + npcName + " を生成しました。");
        return true;
    }

    private boolean handleDeleteNPC(CommandSender sender, String[] args) {
        if (!sender.hasPermission("job.deletenpc")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§c使い方: /job deletenpc <npcName>");
            return true;
        }

        String npcName = args[1];
        if (NPCManager.deleteJobNPC(npcName)) {
            sender.sendMessage("§aNPC " + npcName + " を削除しました。");
        } else {
            sender.sendMessage("§cNPC " + npcName + " が見つかりません。");
        }
        return true;
    }

    private boolean handleAddJobCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("hakuapay.addjob")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません。");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§c使い方: /job addjob <職業名>");
            return true;
        }

        String jobName = args[1].toLowerCase();
        if (HakuaPay.getInstance().getConfig().contains("jobs." + jobName)) {
            sender.sendMessage("§c職業 \"" + jobName + "\" は既に存在します。");
            return true;
        }

        HakuaPay.getInstance().getConfig().set("jobs." + jobName + ".actions", null);
        HakuaPay.getInstance().saveConfig();
        sender.sendMessage("§a職業 \"" + jobName + "\" を追加しました。");
        return true;
    }

    private boolean handleOpenGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーのみ実行可能です。");
            return true;
        }

        Inventory gui = Bukkit.createInventory(null, 27, "§a職業を選択してください");

        Map<String, Object> jobs = HakuaPay.getInstance().getConfig().getConfigurationSection("jobs").getValues(false);
        int slot = 0;

        for (String job : jobs.keySet()) {
            if (slot >= 27) break;

            ItemStack jobItem = new ItemStack(Material.EMERALD);
            ItemMeta meta = jobItem.getItemMeta();
            meta.setDisplayName("§a職業: " + job);
            jobItem.setItemMeta(meta);

            gui.setItem(slot, jobItem);
            slot++;
        }

        player.openInventory(gui);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("job.add")) completions.add("add");
            if (sender.hasPermission("job.remove")) completions.add("remove");
            if (sender.hasPermission("job.list")) completions.add("list");
            if (sender.hasPermission("job.myjob")) completions.add("myjob");
            if (sender.hasPermission("job.createnpc")) completions.add("createnpc");
            if (sender.hasPermission("job.deletenpc")) completions.add("deletenpc");
            if (sender.hasPermission("hakuapay.addjob")) completions.add("addjob");
            completions.add("gui");
        }
        return completions;
    }
}