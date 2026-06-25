package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
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

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class PlayerInteractListener implements Listener {
    PiglysPlayerHeadDrops plugin;
    YamlConfiguration config;
    File livesFile;
    APIManager api;
    public PlayerInteractListener(PiglysPlayerHeadDrops plugin){
        this.plugin = plugin;
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        api = plugin.getAPIManager();
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
            if (headOwner.getName().equals(APIManager.getRealUsername(event.getPlayer()))){
                return;
            }

            for (HeadType type : api.getHeadTypes()){
                if (!type.testFor(item)) continue;
                if (!type.enabled()) return;
                type.handleUse(event, headOwner);
            }

            if (!plugin.getConfig().getBoolean("headUse.enabled")){
                return;
            }
            Player owner = Bukkit.getPlayer(headOwner.getName());
            if (owner == null || !owner.canSee(event.getPlayer())){
                return;
            }
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN))){
                return;
            }

            if (CooldownManager.hasNormalHeadCooldown(event.getPlayer())){
                event.getPlayer().sendActionBar(Component.text(String.format("§cCooldown: %ss", Math.round(Duration.between(Instant.now(), CooldownManager.getNormalHeadCooldown(event.getPlayer())).toSeconds()))));
                return;
            }

            owner.hidePlayer(plugin, event.getPlayer());

            if (plugin.getConfig().getBoolean("headUse.consumeHead")) item.setAmount(item.getAmount() - 1);

            event.getPlayer().sendActionBar(Component.text(String.format("You are invisible to %s", headOwner.getName())));
            if (config.getInt("headUse.duration") > 30) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    event.getPlayer().sendActionBar(Component.text("You will lose invisibility in 30s."));
                }, 20L * (config.getInt("headUse.duration") - 30));
            }
            if (config.getInt("headUse.duration") > 10) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    event.getPlayer().sendActionBar(Component.text("You will lose invisibility in 10s."));
                }, 20L * (config.getInt("headUse.duration") - 10));
            }
            if (config.getInt("headUse.duration") > 5) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    event.getPlayer().sendActionBar(Component.text("You will lose invisibility in 5s."));
                }, 20L * (config.getInt("headUse.duration") - 5));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!owner.canSee(event.getPlayer())) {
                    owner.showPlayer(plugin, event.getPlayer());
                    event.getPlayer().sendActionBar(Component.text(String.format("%s can now see you", headOwner.getName())));
                    CooldownManager.setNormalHeadUseCooldownTime(event.getPlayer(), config.getLong("headUse.cooldown"));
                }
            }, 20L * config.getInt("headUse.duration"));
        }
    }
}
