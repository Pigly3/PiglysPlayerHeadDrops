package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
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
import java.util.Objects;
import java.util.UUID;

public class PlayerInteractListener implements Listener {
    PiglysPlayerHeadDrops plugin;
    File file;
    YamlConfiguration config;
    File livesFile;
    APIManager api;
    public PlayerInteractListener(PiglysPlayerHeadDrops plugin){
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "cooldowns.yml");
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        api = new APIManager(plugin);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) throws IOException {
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
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN)) && plugin.getConfig().getBoolean("disguise.enabled")) {
                this.disguiseHeadInteract(event, headOwner);
                return;
            }
            key = new NamespacedKey(plugin, "is_revive_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                api.revivePlayer(event.getPlayer(), headOwner);
            }
            key = new NamespacedKey(plugin, "is_life_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                api.addExtraLife(event.getPlayer());
            }
            key = new NamespacedKey(plugin, "is_death_bond");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                this.deathBondInteract(event, headOwner);
                return;
            }
            if (!plugin.getConfig().getBoolean("headUse.enabled")){
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
                if (config.getBoolean("headUse.removeHead")){
                    item.setAmount(item.getAmount() - 1);
                }
                event.setCancelled(true);
                event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
                cooldowns.set("players." + event.getPlayer().getName(), Instant.now().plusSeconds(config.getInt("headUse.cooldown")).plusSeconds(config.getInt("headUse.duration")).toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!owner.canSee(event.getPlayer())){
                        owner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
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
                cooldowns.set("players." + event.getPlayer().getName(), Instant.now().plusSeconds(config.getInt("headUse.cooldown")).plusSeconds(config.getInt("headUse.duration")).toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!owner.canSee(event.getPlayer())){
                        owner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                        try {
                            cooldowns.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 20L * config.getInt("headUse.duration"));
            }
        }
    }
    public void disguiseHeadInteract(PlayerInteractEvent event, PlayerProfile owner) {
        Player player = event.getPlayer();
        PlayerProfile profile = player.getPlayerProfile();
        PlayerProfile returnProfile = profile.clone();
        String playerName = player.getName();
        returnProfile.getProperties().removeIf(p -> p.getName().equals("textures"));
        for (ProfileProperty property : owner.getProperties()) {
            if ("textures".equals(property.getName())) {
                returnProfile.setProperty(property);
            }
        }
        returnProfile.setName(owner.getName());
        UUID playerUUID = player.getUniqueId();
        player.setPlayerProfile(returnProfile);
        player.displayName(Component.text(owner.getName()));
        player.playerListName(Component.text(owner.getName()));
        ItemStack item = event.getItem();
        item.setAmount(item.getAmount() - 1);
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(plugin, player);
            p.showPlayer(plugin, player);
        });
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && !Objects.equals(player.getPlayerProfile().getName(), playerName)) {
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
        }, 20L * config.getInt("disguise.duration"));
    }
    public void deathBondInteract(PlayerInteractEvent event, PlayerProfile owner) {
        if (!plugin.getConfig().getBoolean("deathBond.allowUse")) return;
        NamespacedKey linkedWithKey = new NamespacedKey(plugin, "linked_with");
        ItemStack item = event.getItem();
        String linkedWith = event.getItem().getItemMeta().getPersistentDataContainer().get(linkedWithKey, PersistentDataType.STRING);

        if (!Bukkit.getOfflinePlayer(linkedWith).isOnline() || !Bukkit.getOfflinePlayer(owner.getName()).isOnline()) return;

        Player p0 = Bukkit.getPlayer(owner.getName());
        Player p1 = Bukkit.getPlayer(linkedWith);
        if (p0.getLocation().distance(p1.getLocation()) > plugin.getConfig().getInt("deathBond.activateDistance")){
            return;
        }

        item.setAmount(item.getAmount() - 1);
        plugin.api.deathBind(p0, p1) ;
    }
}
