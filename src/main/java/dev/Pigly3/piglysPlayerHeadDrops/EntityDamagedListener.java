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
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) throws IOException {
        if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player victim)){
            String damagerName = APIManager.getRealUserame(damager);
            if (!((Player) event.getEntity()).canSee(event.getDamager())){
                if (plugin.getConfig().getBoolean("headUse.targetProtection.disableDamage")){
                    event.setCancelled(true);
                } else if (plugin.getConfig().getBoolean("headUse.targetProtection.damageCap.enabled")){
                    if (plugin.getConfig().getBoolean("headUse.targetProtection.damageCap.capFinalDamage")){
                        if (event.getFinalDamage() > plugin.getConfig().getDouble("headUse.targetProtection.damageCap.value")){
                            event.setCancelled(true);
                            victim.setHealth(victim.getHealth() > plugin.getConfig().getDouble("headUse.targetProtection.damageCap.value") ? victim.getHealth() -  plugin.getConfig().getDouble("headUse.targetProtection.damageCap.value") : 0);
                        }
                    } else {
                        if (event.getDamage() > plugin.getConfig().getDouble("headUse.targetProtection.damageCap.value")){
                            event.setDamage(plugin.getConfig().getDouble("headUse.targetProtection.damageCap.value"));
                        }
                    }
                }
                ((Player) event.getEntity()).showPlayer(plugin, (Player) event.getDamager());
                YamlConfiguration cooldowns = YamlConfiguration.loadConfiguration(file);
                cooldowns.set("players." + damagerName, Instant.now().plusSeconds(config.getInt("headUseCooldown")).toString());
                try {
                    cooldowns.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
