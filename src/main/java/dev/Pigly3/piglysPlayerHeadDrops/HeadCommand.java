package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class HeadCommand implements BasicCommand {
    Plugin plugin;
    public HeadCommand(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Player cause = (Player) commandSourceStack.getExecutor();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerProfile profile;
            try {
                String realName = APIManager.getRealUserame(cause);
                profile = Bukkit.createProfileExact(cause.getUniqueId(), realName);
                profile.setProperty(new ProfileProperty("textures", Objects.requireNonNull(MojangAPIAccess.getSkin(cause.getUniqueId()))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
                playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(cause.getUniqueId()));
                playerHeadMeta.setPlayerProfile(profile);
                playerHead.setItemMeta(playerHeadMeta);
                cause.give(playerHead);
            });
        });
    }
}
