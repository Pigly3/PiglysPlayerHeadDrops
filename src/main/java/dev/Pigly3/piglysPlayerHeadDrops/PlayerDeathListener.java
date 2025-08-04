package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class PlayerDeathListener implements Listener {
    Plugin plugin;
    APIManager api;
    public PlayerDeathListener(Plugin plugin){
        this.plugin = plugin;
        this.api = new APIManager(plugin);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {
        Player deadPlayer = event.getPlayer();
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(deadPlayer.getUniqueId()));
        playerHead.setItemMeta(playerHeadMeta);
        event.getDrops().add(playerHead);
        api.removeLife(event.getPlayer().getName());
    }
}
