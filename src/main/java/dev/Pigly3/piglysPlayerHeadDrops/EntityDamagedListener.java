package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;

public class EntityDamagedListener implements Listener {
    PiglysPlayerHeadDrops plugin;
    File file;
    YamlConfiguration config;
    APIManager api;
    public EntityDamagedListener(PiglysPlayerHeadDrops plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        api = plugin.api;
    }
    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            int minHealth = plugin.getConfig().getInt("deathBond.minHealth");
            String boundPlayer = plugin.api.getDeathBond(event.getEntity().getName());
            if (((Player) event.getEntity()).getHealth() - event.getFinalDamage() <= minHealth){
                api.removeDeathBond(event.getEntity().getName());
            }
            if (boundPlayer != null) {
                OfflinePlayer boundOfflinePlayer = Bukkit.getOfflinePlayer(boundPlayer);
                if (boundOfflinePlayer.isOnline()){
                    double boundDamage = event.getFinalDamage()/2;
                    Player victim = boundOfflinePlayer.getPlayer();
                    if (victim.getHealth()-boundDamage <= minHealth){
                        victim.setHealth(minHealth);
                        api.removeDeathBond(boundPlayer);
                    } else {
                        victim.setHealth(victim.getHealth()-boundDamage);
                    }
                }
            }
        }
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
                if (plugin.getConfig().getBoolean("headUse.victimProtection.ignoreDamage")){
                    event.setCancelled(true);
                    return;
                }
                if (plugin.getConfig().getBoolean("headUse.victimProtection.antiOneTap")){
                    if (event.getFinalDamage() > 14){
                        if (((Player) event.getEntity()).getHealth() > 14){
                            event.setDamage(0);
                            ((Player) event.getEntity()).setHealth(((Player) event.getEntity()).getHealth()-14);
                        }
                    }
                }
            }
        }
    }
}
