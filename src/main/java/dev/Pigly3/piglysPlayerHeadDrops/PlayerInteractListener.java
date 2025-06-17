package dev.Pigly3.piglysPlayerHeadDrops;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class PlayerInteractListener implements Listener {
    Plugin plugin;
    File file;
    public PlayerInteractListener(Plugin plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null || !item.hasItemMeta()){
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD){
            SkullMeta headMeta = (SkullMeta) item.getItemMeta();
            Player headOwner = Objects.requireNonNull(headMeta.getOwningPlayer()).getPlayer();
            if (headOwner == null){
                return;
            }
            if (headOwner.getName().equals(event.getPlayer().getName())){
                return;
            }
            if (!headOwner.canSee(event.getPlayer())){
                return;
            }
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                return;
            }
            YamlConfiguration cooldowns = YamlConfiguration.loadConfiguration(file);
            if (cooldowns.get("players." + event.getPlayer().getName()) != null){
                if (Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + event.getPlayer().getName()))).isAfter(Instant.now())){
                    System.out.println("On cooldown");
                    event.getPlayer().sendActionBar(Component.text(String.format("§cCooldown: %ss", Math.round(Duration.between(Instant.now(), Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + event.getPlayer().getName())))).getSeconds()))));
                    return;
                }
                headOwner.hidePlayer(plugin, event.getPlayer());
                item.setAmount(item.getAmount() - 1);
                event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!headOwner.canSee(event.getPlayer())){
                        headOwner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                        cooldowns.set("players." + event.getPlayer().getName(), Instant.now().plusSeconds(20).toString());
                        try {
                            cooldowns.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 20L * 60);
            }
        }
    }
}
