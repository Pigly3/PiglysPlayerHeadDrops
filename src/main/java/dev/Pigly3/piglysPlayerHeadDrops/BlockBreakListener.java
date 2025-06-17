package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;


import java.lang.reflect.Field;
import java.util.List;

public class BlockBreakListener implements Listener {
    Plugin plugin;
    public BlockBreakListener(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws NoSuchFieldException {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
            return;
        }
        event.setDropItems(false);
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (state instanceof Skull skull){
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            Boolean value = skull.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
            if (value != null && value){
                ItemStack drop = new ItemStack(Material.PLAYER_HEAD);
                PlayerProfile profile = skull.getPlayerProfile();
                SkullMeta dropMeta = (SkullMeta) drop.getItemMeta();
                dropMeta.setPlayerProfile(profile);
                drop.setItemMeta(dropMeta);
                ItemMeta meta = drop.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
                meta.lore(List.of(Component.text("Decorative")));
                drop.setItemMeta(meta);
                block.getWorld().dropItem(block.getLocation().add(0.5, 0.5, 0.5), drop);

            } else {
                event.setDropItems(true);
            }
        } else {
            event.setDropItems(true);
        }

    }
}
