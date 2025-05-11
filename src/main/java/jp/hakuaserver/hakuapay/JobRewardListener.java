package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.UUID;

public class JobRewardListener implements Listener {

    private final DatabaseManager db;
    private final FileConfiguration config;

    public JobRewardListener(DatabaseManager db, FileConfiguration config) {
        this.db = db;
        this.config = config;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String job = db.getJob(player.getUniqueId());
        if (job == null) return;

        Material block = event.getBlock().getType();
        double reward = config.getDouble("jobs." + job + ".actions.break." + block.name().toLowerCase(), 0);
        if (reward > 0) {
            db.addBalance(player.getUniqueId(), reward);
            player.sendMessage("§a" + block.name() + " を壊して " + reward + " 円を獲得しました！");
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        String job = db.getJob(player.getUniqueId());
        if (job == null) return;

        EntityType entity = event.getEntityType();
        double reward = config.getDouble("jobs." + job + ".actions.kill." + entity.name().toLowerCase(), 0);
        if (reward > 0) {
            db.addBalance(player.getUniqueId(), reward);
            player.sendMessage("§a" + entity.name() + " を倒して " + reward + " 円を獲得しました！");
        }
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String job = db.getJob(player.getUniqueId());
        if (job == null) return;

        Material item = event.getRecipe().getResult().getType();
        double reward = config.getDouble("jobs." + job + ".actions.craft." + item.name().toLowerCase(), 0);
        if (reward > 0) {
            db.addBalance(player.getUniqueId(), reward);
            player.sendMessage("§a" + item.name() + " を作成して " + reward + " 円を獲得しました！");
        }
    }
}
