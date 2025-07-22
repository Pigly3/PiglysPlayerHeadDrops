package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CustomCraftingRecipes {
    public static ItemStack resolveDisguiseHead(Plugin plugin, ItemStack[] items){
        NamespacedKey disguiseKey = new NamespacedKey(plugin, "is_disguise_head");
        NamespacedKey sterileKey = new NamespacedKey(plugin, "is_sterile_head");
        SkullMeta inputMeta = (SkullMeta) items[4].getItemMeta();
        ItemStack result = ItemStack.of(Material.PLAYER_HEAD);
        PlayerProfile profile = inputMeta.getPlayerProfile();
        SkullMeta resultMeta = (SkullMeta) result.getItemMeta();
        resultMeta.setPlayerProfile(profile);
        result.setItemMeta(resultMeta);
        ItemMeta meta = result.getItemMeta();
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(disguiseKey, PersistentDataType.BOOLEAN))) return null;
        if (Boolean.TRUE.equals(inputMeta.getPersistentDataContainer().get(sterileKey, PersistentDataType.BOOLEAN))) return null;
        meta.getPersistentDataContainer().set(disguiseKey, PersistentDataType.BOOLEAN, true);
        meta.lore(List.of(Component.text("Disguise")));
        result.setItemMeta(meta);
        return result;
    }
}
