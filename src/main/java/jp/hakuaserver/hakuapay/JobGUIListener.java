package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class JobGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§a職業を選択してください")) return;

        event.setCancelled(true); // アイテムを動かせないようにする

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.EMERALD) return;

        Player player = (Player) event.getWhoClicked();
        String job = event.getCurrentItem().getItemMeta().getDisplayName().replace("§a職業: ", "");

        // 職業をデータベースに保存
        HakuaPay.getDatabaseManager().setJob(player.getUniqueId(), job);

        player.sendMessage("§aあなたの職業が " + job + " に設定されました！");
        player.closeInventory();
    }
}
