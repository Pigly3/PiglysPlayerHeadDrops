package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class APIManager {
    Plugin plugin;
    File livesFile;
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
}
