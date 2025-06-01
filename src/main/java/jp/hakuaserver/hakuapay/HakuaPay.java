package jp.hakuaserver.hakuapay;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

import java.sql.SQLException;
import java.util.UUID;

public class HakuaPay extends JavaPlugin implements CommandExecutor {

    private static HakuaPay instance;

    private static Economy econ = null;
    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("HakuaPay: onEnable メソッドが呼び出されました。");
        try {
            // HakuaPayフォルダを作成
            File pluginFolder = new File(getDataFolder().getPath());
            if (!pluginFolder.exists()) {
                pluginFolder.mkdirs();
            }

            // config.ymlを生成
            File configFile = new File(pluginFolder, "config.yml");
            if (!configFile.exists()) {
                saveDefaultConfig(); // デフォルトのconfig.ymlをコピー
                getLogger().info("config.yml を生成しました。");
            }

            reloadConfig();

            if (!setupEconomy()) {
                getLogger().severe("Vault 経済プラグインが見つかりません。プラグインを無効化します。");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            databaseManager = new DatabaseManager();
            databaseManager.connect();
            getLogger().info("データベースに接続しました。");

            getServer().getServicesManager().register(Economy.class, new VaultEconomyProvider(databaseManager), this, ServicePriority.Normal);
            getLogger().info("Vault 経済プロバイダーとして登録しました。");

            getCommand("hakuapay").setExecutor(new HakuaPayCommand());
            getCommand("job").setExecutor(new JobCommand());
            getCommand("audio").setExecutor(new AudioCommand());
            getServer().getPluginManager().registerEvents(new JobRewardListener(databaseManager, getConfig()), this);
            getServer().getPluginManager().registerEvents(new JobGUIListener(), this);
            getServer().getPluginManager().registerEvents(new NPCManager(), this);

            getLogger().info("HakuaPay が有効になりました。");
        } catch (Exception e) {
            getLogger().severe("プラグインの起動中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
            getLogger().info("データベース接続を閉じました。");
        }
        getLogger().info("HakuaPay が無効になりました。");
    }

    public static HakuaPay getInstance(){
        if (instance == null) {
            throw new IllegalStateException("HakuaPay instance is not initialized yet!");
        }
        return instance;
    }
    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        UUID uuid = player.getUniqueId();

        switch (command.getName().toLowerCase()) {
            case "money":
                double balance = databaseManager.getBalance(uuid);
                player.sendMessage("あなたの残高: " + balance + " 円");
                return true;

            case "pay":
                if (args.length != 2) {
                    player.sendMessage("/pay <プレイヤー> <金額>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage("プレイヤーが見つかりません。");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("金額が無効です。");
                    return true;
                }

                if (amount <= 0) {
                    player.sendMessage("金額は正の数でなければなりません。");
                    return true;
                }

                if (!databaseManager.subtractBalance(uuid, amount)) {
                    player.sendMessage("残高不足です。");
                    return true;
                }

                databaseManager.addBalance(target.getUniqueId(), amount);
                player.sendMessage(target.getName() + " に " + amount + " 円支払いました。");
                target.sendMessage(player.getName() + " から " + amount + " 円を受け取りました。");
                return true;

            case "setmoney":
                if (!sender.hasPermission("hakuapay.setmoney")) {
                    player.sendMessage("§c権限がありません。");
                    return true;
                }

                if (args.length != 2) {
                    player.sendMessage("/setmoney <プレイヤー> <金額>");
                    return true;
                }

                Player targetSet = Bukkit.getPlayer(args[0]);
                if (targetSet == null || !targetSet.isOnline()) {
                    player.sendMessage("プレイヤーが見つかりません。");
                    return true;
                }

                int setAmount;
                try {
                    setAmount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("金額が無効です。");
                    return true;
                }

                databaseManager.setBalance(targetSet.getUniqueId(), setAmount);
                player.sendMessage(targetSet.getName() + " の残高を " + setAmount + " 円に設定しました。");
                targetSet.sendMessage("あなたの残高が " + setAmount + " 円に設定されました。");
                return true;
        }

        return false;
    }
}
