package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

public class APIManager {
    Plugin plugin;
    File livesFile;
    HashMap<String, String[]> deathBonds = new HashMap<String, String[]>();
    public APIManager (Plugin plugin){
        this.plugin = plugin;
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
    }
    public void removeLife(String playerName) throws IOException {
        if (lifeSystemDisabled()) return;
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        int playerLives = getLives(playerName);
        setLives(playerName, Math.max(playerLives - 1, 0));
    }
    public void addLife(String playerName) throws IOException {
        if (lifeSystemDisabled()) return;
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        int playerLives = getLives(playerName);
        setLives(playerName, Math.max(playerLives + 1, 0));
    }
    public int getLives(String playerName){
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        return lives.get("lives." + playerName) != null ? lives.getInt("lives." + playerName) : plugin.getConfig().getInt("lifeSystem.maxLives");
    }
    public void setLives(String playerName, int playerLives) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        lives.set("lives." + playerName, playerLives);
        lives.save(livesFile);
    }
    public void revive(String playerName) throws IOException {
        if (lifeSystemDisabled()) return;
        setLives(playerName, plugin.getConfig().getInt("lifeSystem.maxLives"));
    }
    public boolean lifeSystemDisabled(){
        return !plugin.getConfig().getBoolean("lifeSystem.enabled");
    }
    public void addExtraLife(Player player) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        if (!plugin.getConfig().getBoolean("lifeSystem.extraLives.enabled")) return;
        if (lives.getInt("lives." + player.getName()) != plugin.getConfig().getInt("lifeSystem.maxLives") && !(lives.get("lives." + player.getName()) == null)){
            this.addLife(player.getName());
        } else {
            player.sendActionBar(Component.text("§4You have the maximum number of lives."));
        }
    }
    public void revivePlayer(Player user, PlayerProfile owner) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        if (!plugin.getConfig().getBoolean("lifeSystem.revives.enabled")) return;
        if (lives.getInt("lives." + owner.getName()) == 0) {
            this.revive(owner.getName());
        } else {
            if (user != null){
                user.sendActionBar(Component.text(String.format("§4%s is not dead.", owner.getName())));
            }
        }
    }
    public static SkullMeta getSkullMeta(ItemStack stack){
        return (SkullMeta) stack.getItemMeta();
    }
    public static PlayerProfile getPlayerProfile(ItemStack stack){
        return getSkullMeta(stack).getPlayerProfile();
    }
    public static OfflinePlayer getOwner(ItemStack stack){
        return getSkullMeta(stack).getOwningPlayer();
    }
    public boolean isSpecialized(ItemMeta meta){
        SkullMeta inputMeta = (SkullMeta) meta;
        NamespacedKey disguiseKey = new NamespacedKey(plugin, "is_disguise_head");
        NamespacedKey sterileKey = new NamespacedKey(plugin, "is_sterile_head");
        NamespacedKey reviveKey = new NamespacedKey(plugin, "is_revive_head");
        NamespacedKey lifeKey = new NamespacedKey(plugin, "is_life_head");
        NamespacedKey bondKey = new NamespacedKey(plugin, "is_death_bond");
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(disguiseKey, PersistentDataType.BOOLEAN))) return true;
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(sterileKey, PersistentDataType.BOOLEAN))) return true;
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(reviveKey, PersistentDataType.BOOLEAN))) return true;
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(lifeKey, PersistentDataType.BOOLEAN))) return true;
        return Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(bondKey, PersistentDataType.BOOLEAN));
    }
    public String getOwnerName(ItemStack stack){
        return getOwner(stack).getName();
    }
    public void deathBind(Player p0, Player p1){
        deathBonds.put(p0.getName(), new String[]{p1.getName(), Instant.now().plusSeconds(plugin.getConfig().getInt("deathBond.duration")).toString()});
        deathBonds.put(p1.getName(), new String[]{p0.getName(), Instant.now().plusSeconds(plugin.getConfig().getInt("deathBond.duration")).toString()});
    }
    public String getDeathBond(String playerName){
        if (deathBonds.get(playerName) == null) return null;
        if (Instant.parse(deathBonds.get(playerName)[1]).isAfter(Instant.now())){
            return deathBonds.get(playerName)[0];
        }
        return null;
    }
    public void removeDeathBond(String playerName){
        if (deathBonds.get(playerName) == null) return;
        if (Instant.parse(deathBonds.get(playerName)[1]).isAfter(Instant.now())){
            deathBonds.get(playerName)[1] = Instant.now().toString();
        }
        if (Instant.parse(deathBonds.get(deathBonds.get(playerName)[0])[1]).isAfter(Instant.now())){
            deathBonds.get(deathBonds.get(playerName)[0])[1] = Instant.now().toString();
        }
    }
}
