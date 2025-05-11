package jp.hakuaserver.hakuapay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class JobMobListener implements Listener {

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;

        // メタデータから職業を取得
        List<MetadataValue> metadata = villager.getMetadata("job");
        if (metadata.isEmpty()) return;

        String job = metadata.get(0).asString();
        Player player = event.getPlayer();

        // GUIを開く
        Inventory gui = Bukkit.createInventory(null, 9, "§a職業選択: " + job);

        // 職業を選択するアイテムを追加
        ItemStack jobItem = new ItemStack(Material.EMERALD);
        ItemMeta meta = jobItem.getItemMeta();
        meta.setDisplayName("§a職業: " + job);
        jobItem.setItemMeta(meta);

        gui.setItem(4, jobItem); // 中央に配置
        player.openInventory(gui);
    }
}
