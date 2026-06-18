package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Objects;

public class PlayerDeathListener implements Listener {
    Plugin plugin;
    APIManager api;
    public PlayerDeathListener(Plugin plugin){
        this.plugin = plugin;
        this.api = new APIManager(plugin);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws IOException {
        Player deadPlayer = event.getPlayer();
        String realName = APIManager.getRealUserame(deadPlayer);
        PlayerProfile profile = Bukkit.createProfileExact(deadPlayer.getUniqueId(), realName);
        profile.setProperty(new ProfileProperty("textures", Objects.requireNonNull(MojangAPIAccess.getSkin(deadPlayer.getUniqueId()))));
        if (!Objects.equals(deadPlayer.getPlayerProfile().getName(), APIManager.getRealUserame(deadPlayer))){
            deadPlayer.setPlayerProfile(profile);

            deadPlayer.displayName(Component.text(APIManager.getRealUserame(deadPlayer)));
            deadPlayer.playerListName(Component.text(APIManager.getRealUserame(deadPlayer)));

            Bukkit.getOnlinePlayers().forEach(p -> {
                p.hidePlayer(plugin, deadPlayer);
                p.showPlayer(plugin, deadPlayer);
            });
        }
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        plugin.getLogger().info(String.valueOf(deadPlayer.getUniqueId()));
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(deadPlayer.getUniqueId()));
        playerHeadMeta.setPlayerProfile(profile);
        playerHead.setItemMeta(playerHeadMeta);
        event.getDrops().add(playerHead);
        api.removeLife(APIManager.getRealUserame(deadPlayer));
    }
}
