package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import java.util.UUID;

public class PlayerInteractListener implements Listener {
    Plugin plugin;
    File file;
    YamlConfiguration config;
    public PlayerInteractListener(Plugin plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if (item == null || item.getItemMeta() == null || !item.hasItemMeta()){
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD){
            SkullMeta headMeta = (SkullMeta) item.getItemMeta();
            PlayerProfile headOwner = Objects.requireNonNull(headMeta.getPlayerProfile());
            if (headOwner == null){
                return;
            }
            if (headOwner.getName().equals(event.getPlayer().getName())){
                return;
            }
            NamespacedKey key = new NamespacedKey(plugin, "is_disguise_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                plugin.getLogger().info("used disguise head");
                this.disguiseHeadInteract(event, headOwner);
                return;
            }
            Player owner = Bukkit.getPlayer(headOwner.getName());
            if (owner == null || !owner.canSee(event.getPlayer())){
                return;
            }
            key = new NamespacedKey(plugin, "is_sterile_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                return;
            }
            YamlConfiguration cooldowns = YamlConfiguration.loadConfiguration(file);
            if (cooldowns.get("players." + event.getPlayer().getName()) != null){
                if (Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + event.getPlayer().getName()))).isAfter(Instant.now())){
                    event.getPlayer().sendActionBar(Component.text(String.format("§cCooldown: %ss", Math.round(Duration.between(Instant.now(), Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + event.getPlayer().getName())))).getSeconds()))));
                    return;
                }
                owner.hidePlayer(plugin, event.getPlayer());
                if (config.getBoolean("removeHeadOnUse")){
                    item.setAmount(item.getAmount() - 1);
                }
                event.setCancelled(true);
                event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!owner.canSee(event.getPlayer())){
                        owner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                        cooldowns.set("players." + event.getPlayer().getName(), Instant.now().plusSeconds(20).toString());
                        try {
                            cooldowns.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 20L * 60);
            } else {
                owner.hidePlayer(plugin, event.getPlayer());
                item.setAmount(item.getAmount() - 1);
                event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!owner.canSee(event.getPlayer())){
                        owner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                        cooldowns.set("players." + event.getPlayer().getName(), Instant.now().plusSeconds(config.getInt("headUseCooldown")).toString());
                        try {
                            cooldowns.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 20L * config.getInt("headUseDuration"));
            }
        }
    }
    public void disguiseHeadInteract(PlayerInteractEvent event, PlayerProfile owner){
        Player player = event.getPlayer();
        PlayerProfile profile = player.getPlayerProfile();
        PlayerProfile returnProfile = (PlayerProfile) profile.clone();
        String playerName = player.getName();
        plugin.getLogger().info(String.format("%s disguised as %s", playerName, owner.getName()));
        for (ProfileProperty property : owner.getProperties()) {
            if ("textures".equals(property.getName())) {
                profile.setProperty(property);
            }
        }
        profile.setName(owner.getName());
        UUID playerUUID = player.getUniqueId();
        player.setPlayerProfile(profile);
        player.displayName(Component.text(owner.getName()));
        player.playerListName(Component.text(owner.getName()));
        ItemStack item = event.getItem();
        item.setAmount(item.getAmount() - 1);
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(plugin, player);
            p.showPlayer(plugin, player);
        });
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && !Objects.equals(player.getPlayerProfile().getName(), playerName)){
                player.setPlayerProfile(Bukkit.createProfile(playerUUID, playerName));
                player.displayName(Component.text(playerName));
                player.playerListName(Component.text(playerName));
                profile.setName(playerName);
                for (ProfileProperty property : returnProfile.getProperties()) {
                    if ("textures".equals(property.getName())) {
                        profile.setProperty(property);
                    }
                }
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.hidePlayer(plugin, player);
                    p.showPlayer(plugin, player);
                });
            }
        }, 20L * config.getInt("disguiseDuration"));
    }
}
