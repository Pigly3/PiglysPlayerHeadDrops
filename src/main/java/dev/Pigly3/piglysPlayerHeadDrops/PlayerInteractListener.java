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
    Plugin plugin;
    File file;
    YamlConfiguration config;
    File livesFile;
    APIManager api;
    public PlayerInteractListener(Plugin plugin){
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
            if (headOwner.getName().equals(APIManager.getRealUserame(event.getPlayer()))){
                return;
            }
            NamespacedKey key = new NamespacedKey(plugin, "is_disguise_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN)) && plugin.getConfig().getBoolean("disguise.enabled")) {
                this.disguiseHeadInteract(event, headOwner);
                return;
            }
            key = new NamespacedKey(plugin, "is_revive_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                this.revivePlayer(event, headOwner);
            }
            key = new NamespacedKey(plugin, "is_life_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                this.addExtraLife(event, headOwner);
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
            if (cooldowns.get("players." + APIManager.getRealUserame(event.getPlayer())) != null){
                if (Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + APIManager.getRealUserame(event.getPlayer())))).isAfter(Instant.now())){
                    event.getPlayer().sendActionBar(Component.text(String.format("§cCooldown: %ss", Math.round(Duration.between(Instant.now(), Instant.parse((CharSequence) Objects.requireNonNull(cooldowns.get("players." + APIManager.getRealUserame(event.getPlayer()))))).getSeconds()))));
                    return;
                }
                owner.hidePlayer(plugin, event.getPlayer());
                if (config.getBoolean("headUse.removeHead")){
                    item.setAmount(item.getAmount() - 1);
                }
                event.setCancelled(true);
                event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!owner.canSee(event.getPlayer())){
                        owner.showPlayer(plugin, event.getPlayer());
                        event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                        try {
                            cooldowns.set("players." + APIManager.getRealUserame(event.getPlayer()), Instant.now().plusSeconds(20).toString());
                        } catch (IOException e) {plugin.getLogger().severe("Couldn't get a real username properly.");}
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
                        try {
                            cooldowns.set("players." + APIManager.getRealUserame(event.getPlayer()), Instant.now().plusSeconds(config.getInt("headUse.cooldown")).toString());
                        } catch (IOException e) {plugin.getLogger().severe("Couldn't get a real username properly.");}
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
    public void disguiseHeadInteract(PlayerInteractEvent event, PlayerProfile owner) throws IOException {
        Player player = event.getPlayer();
        PlayerProfile profile = player.getPlayerProfile();
        String playerName = APIManager.getRealUserame(player);
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
        if (config.getBoolean("disguise.removeHead")){
            item.setAmount(item.getAmount() - 1);
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(plugin, player);
            p.showPlayer(plugin, player);
        });
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (player.isOnline() && !Objects.equals(player.getPlayerProfile().getName(), playerName)){
                PlayerProfile realProfile = Bukkit.createProfileExact(player.getUniqueId(), playerName);
                try {
                    realProfile.setProperty(new ProfileProperty("textures", Objects.requireNonNull(MojangAPIAccess.getSkin(player.getUniqueId()))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.setPlayerProfile(realProfile);
                player.displayName(Component.text(playerName));
                player.playerListName(Component.text(playerName));
                profile.setName(playerName);

                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.hidePlayer(plugin, player);
                    p.showPlayer(plugin, player);
                });
            }
        }, 20L * config.getInt("disguise.duration"));
    }
    public void revivePlayer(PlayerInteractEvent event, PlayerProfile owner) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        if (!plugin.getConfig().getBoolean("lifeSystem.revives.enabled")) return;
        if (lives.getInt("lives." + owner.getName()) == 0){
            api.revive(event.getPlayer().getName());
        } else {
            event.getPlayer().sendActionBar(Component.text(String.format("§4%s is not dead.", owner.getName())));
        }
    }

    public void addExtraLife(PlayerInteractEvent event, PlayerProfile owner) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        if (!plugin.getConfig().getBoolean("lifeSystem.extraLives.enabled")) return;
        if (lives.getInt("lives." + event.getPlayer().getName()) != plugin.getConfig().getInt("lifeSystem.maxLives") && !(lives.get("lives." + event.getPlayer().getName()) == null)){
            api.addLife(event.getPlayer().getName());
        } else {
            event.getPlayer().sendActionBar(Component.text("§4You have the maximum number of lives."));
        }
    }
}
