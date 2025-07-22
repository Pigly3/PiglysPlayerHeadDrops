package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class CommandListener implements Listener {
    Plugin plugin;
    public CommandListener(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSent(PlayerCommandPreprocessEvent event) throws IOException {
        if (event.getMessage().toLowerCase().startsWith("/msg") || event.getMessage().toLowerCase().startsWith("/tell") || event.getMessage().toLowerCase().startsWith("/w")){
            String[] commandParts = event.getMessage().split(" ");
            UUID recipientUUID = Bukkit.getOfflinePlayer(commandParts[1]).getUniqueId();
            for (Player currPlayer : Bukkit.getOnlinePlayers()){
                if (currPlayer.getName().equals(commandParts[1])){
                    if (currPlayer.getUniqueId() != recipientUUID){
                        commandParts[1] = MojangAPIAccess.uuidToUsername(currPlayer.getUniqueId());
                        event.setMessage(String.join(" ", commandParts));
                    }
                }
            }
        }
    }
}
