package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class BlockPlaceListener implements Listener {
    Plugin plugin;
    public BlockPlaceListener(Plugin plugin){
        this.plugin = plugin;
    }
    @EventHandler(priority= EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlockPlaced();
        BlockState state = block.getState();
        if (state instanceof TileState tileState) {
            ItemStack item = event.getItemInHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
                Boolean value = meta.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN);
                NamespacedKey disguiseKey = new NamespacedKey(plugin, "is_disguise_head");
                Boolean disguiseValue = meta.getPersistentDataContainer().get(disguiseKey, PersistentDataType.BOOLEAN);
                if (value != null) {
                    tileState.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, value);
                    tileState.update(true, false);
                }
                if (Boolean.TRUE.equals(disguiseValue)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
