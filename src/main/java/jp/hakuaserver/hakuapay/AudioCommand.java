package jp.hakuaserver.hakuapay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.net.URL;
import java.net.HttpURLConnection;


public class AudioCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("audio")) {
            if(sender instanceof Player){
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                String url = "https://hakuaserver.jp:3000/audio?uuid=" + uuid;
                player.sendMessage("オーディオ用URL: " + url);
                return true;
            }
        }
        return false;
    }

    public void playMusic(Player player, String musicUrl) {
        try {
            String uuid = player.getUniqueId().toString();
            URL url = new URL("https://hakuaserver.jp:3000/audio?uuid=" + uuid + "&track=" + musicUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.getInputStream().close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
