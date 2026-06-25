package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HeadTypeGenerators {
    private final PiglysPlayerHeadDrops plugin;
    private final File livesFile;
    private final APIManager api;

    public HeadTypeGenerators(PiglysPlayerHeadDrops plugin) {
        this.plugin = plugin;
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
        this.api = plugin.getAPIManager();
    }

    public void generateDisguiseHeadType() {
        NamespacedKey key = new NamespacedKey(plugin, "is_disguise_head");
        BiConsumer< PlayerInteractEvent, PlayerProfile > handler = (PlayerInteractEvent event, PlayerProfile owner) -> {
            Player player = event.getPlayer();
            PlayerProfile profile = player.getPlayerProfile();
            String playerName;
            try {
                playerName = APIManager.getRealUsername(player);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (CooldownManager.hasDisguiseCooldown(event.getPlayer())){
                event.getPlayer().sendActionBar(Component.text(String.format("§cCooldown: %ss", Math.round(Duration.between(Instant.now(), CooldownManager.getDisguiseCooldown(event.getPlayer())).toSeconds()))));
                return;
            }

            api.disguisePlayer(player, owner);
            CooldownManager.setDisguiseCooldownTime(player, plugin.getConfig().getInt("disguise.duration") + plugin.getConfig().getInt("disguise.cooldown"));

            ItemStack item = event.getItem();
            if (plugin.getConfig().getBoolean("disguise.consumeHead")){
                item.setAmount(item.getAmount() - 1);
            }
            if (plugin.getConfig().getInt("disguise.duration") > 30) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendActionBar(Component.text("Your disguise will end in 30s."));
                }, 20L * (plugin.getConfig().getInt("disguise.duration") - 30));
            }
            if (plugin.getConfig().getInt("disguise.duration") > 10) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendActionBar(Component.text("Your disguise will end in 10s."));
                }, 20L * (plugin.getConfig().getInt("disguise.duration") - 10));
            }
            if (plugin.getConfig().getInt("disguise.duration") > 5) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendActionBar(Component.text("Your disguise will end in 5s."));
                }, 20L * (plugin.getConfig().getInt("disguise.duration") - 5));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                CooldownManager.setDisguiseCooldownTime(player, plugin.getConfig().getLong("disguise.cooldown"));
                player.sendActionBar(Component.text("Your disguise has ended."));
                api.clearDisguise(player);
            }, 20L * plugin.getConfig().getInt("disguise.duration"));
        };
        Supplier<Boolean> enabledTest = () -> plugin.getConfig().getBoolean("disguise.enabled");
        plugin.getAPIManager().addHeadType(new HeadType("Disguise", key, handler, enabledTest));
    }

    public void generateReviveHeadType() {
        NamespacedKey key = new NamespacedKey(plugin, "is_revive_head");
        Supplier<Boolean> enabledTest = () -> plugin.getConfig().getBoolean("lifeSystem.enabled") && plugin.getConfig().getBoolean("lifeSystem.revives.enabled");
        BiConsumer<PlayerInteractEvent, PlayerProfile> handler = (PlayerInteractEvent event, PlayerProfile owner) -> {
            YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
            if (lives.getInt("lives." + owner.getName()) == 0){
                try {
                    plugin.getAPIManager().revive(event.getPlayer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                event.getPlayer().sendActionBar(Component.text(String.format("§4%s is not dead.", owner.getName())));
            }
        };
        plugin.getAPIManager().addHeadType(new HeadType("Revive", key, handler, enabledTest));
    }

    public void generateLifeHeadType() {
        NamespacedKey key = new NamespacedKey(plugin, "is_life_head");
        Supplier<Boolean> enabledTest = () -> plugin.getConfig().getBoolean("lifeSystem.enabled");

        BiConsumer<PlayerInteractEvent, PlayerProfile> handler = (PlayerInteractEvent event, PlayerProfile owner) -> {
            YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
            if (!plugin.getConfig().getBoolean("lifeSystem.enabled")) return;
            if (!plugin.getConfig().getBoolean("lifeSystem.extraLives.enabled")) return;
            if (lives.getInt("lives." + event.getPlayer().getName()) != plugin.getConfig().getInt("lifeSystem.maxLives") && !(lives.get("lives." + event.getPlayer().getName()) == null)){
                try {
                    plugin.getAPIManager().addLife(event.getPlayer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                event.getPlayer().sendActionBar(Component.text("§4You have the maximum number of lives."));
            }
        };

        plugin.getAPIManager().addHeadType(new HeadType("Life", key, handler, enabledTest));
    }
}
