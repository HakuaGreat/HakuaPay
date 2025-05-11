package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class JobMobListener implements Listener {

    private final FileConfiguration config;

    public JobMobListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());

        Player player = event.getPlayer();

        // GUIを作成
        Inventory gui = Bukkit.createInventory(null, 27, "§a職業を選択してください");

        // Configから職業リストを取得
        Map<String, Object> jobs = config.getConfigurationSection("jobs").getValues(false);
        int slot = 0;

        for (String job : jobs.keySet()) {
            if (slot >= 27) break; // GUIのスロット数を超えないようにする

            ItemStack jobItem = new ItemStack(Material.EMERALD);
            ItemMeta meta = jobItem.getItemMeta();
            meta.setDisplayName("§a職業: " + job);
            jobItem.setItemMeta(meta);

            gui.setItem(slot, jobItem);
            slot++;
        }

        player.openInventory(gui);
    }
}
