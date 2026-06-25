package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;
import java.util.Objects;

public class PlayerDeathListener implements Listener {
    PiglysPlayerHeadDrops plugin;
    APIManager api;
    public PlayerDeathListener(PiglysPlayerHeadDrops plugin){
        this.plugin = plugin;
        this.api = plugin.getAPIManager();
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {
        Player deadPlayer = event.getPlayer();
        if (!Objects.equals(deadPlayer.getPlayerProfile().getName(), APIManager.getRealUsername(deadPlayer))){
            api.clearDisguise(deadPlayer);
            CooldownManager.setDisguiseCooldownTime(deadPlayer, plugin.getConfig().getInt("disguise.duration"));
        }

        event.getDrops().add(api.createHead(deadPlayer));
        api.removeLife(deadPlayer);
    }
}
