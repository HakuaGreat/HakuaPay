package jp.hakuaserver.hakuapay;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobMobCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用可能です。");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§c使い方: /jobmob <職業名> <スキン名>");
            return true;
        }

        String job = args[0];
        String skinName = args[1];
        Location location = player.getLocation();

        // NPCを生成
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, "§a職業: " + job);
        npc.spawn(location);

        // スキンを適用
        npc.data().setPersistent("player-skin-name", skinName);

        // 職業情報をTraitに保存
        npc.addTrait(new JobTrait(job));

        player.sendMessage("§a職業 " + job + " を与えるプレイヤーモブを生成しました！");
        return true;
    }

    public static class JobTrait extends Trait {
        private final String job;

        public JobTrait(String job) {
            super("JobTrait");
            this.job = job;
        }

        public String getJob() {
            return job;
        }
    }
}
