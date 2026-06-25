package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

public class APIManager {
    private final Plugin plugin;
    private final File livesFile;
    private final ArrayList<HeadType> headTypes = new ArrayList<>();
    private final ArrayList<NBTCraftingRecipe> recipes = new ArrayList<>();

    public APIManager (Plugin plugin){
        this.plugin = plugin;
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
    }
    public void removeLife(Player player) throws IOException {
        removeLife(player.getUniqueId());
    }
    public void removeLife(UUID playerUUID) throws IOException {
        if (lifeSystemDisabled()) return;
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        int playerLives = getLives(playerUUID);
        setLives(playerUUID, Math.clamp(playerLives - 1, 0, plugin.getConfig().getInt("lives.maxLives")));
    }
    public void addLife(Player player) throws IOException {
        addLife(player.getUniqueId());
    }
    public void addLife(UUID playerUUID) throws IOException {
        if (lifeSystemDisabled()) return;
        int playerLives = getLives(playerUUID);
        setLives(playerUUID, Math.clamp(playerLives + 1, 0, plugin.getConfig().getInt("lives.maxLives")));
    }
    public int getLives(Player player) {
        return getLives(player.getUniqueId());
    }
    public int getLives(UUID playerUUID){
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        return lives.get("lives." + playerUUID.toString().replace("-", "")) != null ? lives.getInt("lives." + playerUUID.toString().replace("-", "")) : plugin.getConfig().getInt("lifeSystem.startingLives");
    }

    public void setLives(Player player, int newLives) throws IOException {
        setLives(player.getUniqueId(), newLives);
    }

    public void setLives(UUID playerUUID, int playerLives) throws IOException {
        YamlConfiguration lives = YamlConfiguration.loadConfiguration(livesFile);
        lives.set("lives." + playerUUID.toString().replace("-", ""), Math.clamp(playerLives, 0, plugin.getConfig().getInt("lives.maxLives")));
        lives.save(livesFile);
    }

    public void revive(Player player) throws IOException {
        revive(player.getUniqueId());
    }

    public void revive(UUID playerUUID) throws IOException {
        if (lifeSystemDisabled()) return;
        setLives(playerUUID, plugin.getConfig().getInt("lifeSystem.maxLives"));
    }

    public static String getRealUsername(Player player) throws IOException {
        return MojangAPIAccess.uuidToUsername(player.getUniqueId());
    }
    public boolean lifeSystemDisabled(){
        return !plugin.getConfig().getBoolean("lifeSystem.enabled");
    }

    public static UUID parseUUID(String uuidString) {
        String formatted = uuidString.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
        return UUID.fromString(formatted);
    }

    public ItemStack createHead(UUID uuid, String name) throws IOException {
        PlayerProfile profile = Bukkit.createProfileExact(uuid, name);
        profile.setProperty(new ProfileProperty("textures", Objects.requireNonNull(MojangAPIAccess.getSkin(uuid))));
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        playerHeadMeta.setPlayerProfile(profile);
        playerHead.setItemMeta(playerHeadMeta);
        return playerHead;
    }

    public ItemStack createHead(Player player) throws IOException {
        return createHead(player.getUniqueId(), getRealUsername(player));
    }

    public void addHeadType(HeadType type) {
        headTypes.add(type);
    }

    public ArrayList<HeadType> getHeadTypes() {
        return headTypes;
    }

    public ArrayList<NBTCraftingRecipe> getRecipes() {
        return recipes;
    }

    public void disguisePlayer(Player player, PlayerProfile disguise) {
        PlayerProfile profile = player.getPlayerProfile();

        for (ProfileProperty property : disguise.getProperties()) {
            if ("textures".equals(property.getName())) {
                profile.setProperty(property);
            }
        }
        profile.setName(disguise.getName());
        player.setPlayerProfile(profile);
        player.displayName(Component.text(Objects.requireNonNull(disguise.getName())));
        player.playerListName(Component.text(disguise.getName()));

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(plugin, player);
            p.showPlayer(plugin, player);
        });
    }

    public void clearDisguise(Player player) {
        try {
            String realName = getRealUsername(player);

            PlayerProfile profile = Bukkit.createProfileExact(player.getUniqueId(), realName);
            profile.setProperty(new ProfileProperty("textures", Objects.requireNonNull(MojangAPIAccess.getSkin(player.getUniqueId()))));
            player.setPlayerProfile(profile);

            player.displayName(Component.text(APIManager.getRealUsername(player)));
            player.playerListName(Component.text(APIManager.getRealUsername(player)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(plugin, player);
            p.showPlayer(plugin, player);
        });
    }

    public void addNBTCraftingRecipe(String pluginSpecificID, BiFunction<Plugin, ItemStack[], ItemStack> resolve, Plugin plugin, Material[] pattern){
        NBTCraftingRecipe recipe = new NBTCraftingRecipe(pluginSpecificID, resolve, plugin, pattern);
        recipe.register();
        recipes.add(recipe);
    }
}
