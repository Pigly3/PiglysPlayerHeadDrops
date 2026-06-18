package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;

public class EntityDamagedListener implements Listener {
    Plugin plugin;
    File file;
    YamlConfiguration config;
    public EntityDamagedListener(Plugin plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event){
        if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)){
            if (!((Player) event.getEntity()).canSee(event.getDamager())){
                ((Player) event.getEntity()).showPlayer(plugin, (Player) event.getDamager());
                YamlConfiguration cooldowns = YamlConfiguration.loadConfiguration(file);
                cooldowns.set("players." + event.getDamager().getName(), Instant.now().plusSeconds(config.getInt("headUseCooldown")).toString());
                try {
                    cooldowns.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
