package dev.Pigly3.piglysPlayerHeadDrops;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PlayerDeathListener implements Listener {
    Plugin plugin;
    public PlayerDeathListener(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        DamageSource source = event.getDamageSource();
        Player deadPlayer = event.getPlayer();
        Player cause = deadPlayer.getKiller();
        if (cause == null){
            if (event.getDamageSource().getCausingEntity() instanceof Player playerCause){
                cause = playerCause;
            } else {
                return;
            }
        }
        if (cause.getName().equals(deadPlayer.getName())){
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            playerHeadMeta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            playerHeadMeta.lore(List.of(Component.text("Decorative")));
            playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(deadPlayer.getName()));
            playerHead.setItemMeta(playerHeadMeta);
            event.getDrops().add(playerHead);
            return;
        }
        cause.giveExp(event.getDroppedExp(), true);
        event.setDroppedExp(0);
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(Bukkit.getOfflinePlayer(deadPlayer.getName()));
        playerHead.setItemMeta(playerHeadMeta);
        cause.give(playerHead);
    }
}
