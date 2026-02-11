package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

public class CustomCraftingRecipes {
    public static ItemStack resolveDisguiseHead(Plugin bukkitPlugin, ItemStack[] items){
        if (bukkitPlugin instanceof PiglysPlayerHeadDrops plugin) {
            NamespacedKey disguiseKey = new NamespacedKey(plugin, "is_disguise_head");
            SkullMeta inputMeta = (SkullMeta) items[4].getItemMeta();
            ItemStack result = ItemStack.of(Material.PLAYER_HEAD);
            PlayerProfile profile = inputMeta.getPlayerProfile();
            SkullMeta resultMeta = (SkullMeta) result.getItemMeta();
            resultMeta.setPlayerProfile(profile);
            result.setItemMeta(resultMeta);
            ItemMeta meta = result.getItemMeta();
            if (plugin.api.isSpecialized(meta)) return null;
            meta.getPersistentDataContainer().set(disguiseKey, PersistentDataType.BOOLEAN, true);
            meta.lore(List.of(Component.text("Disguise")));
            result.setItemMeta(meta);
            return result;
        }
        return null;
    }
    public static ItemStack resolveReviveHead(Plugin bukkitPlugin, ItemStack[] items){
        if (bukkitPlugin instanceof PiglysPlayerHeadDrops plugin) {
            NamespacedKey reviveKey = new NamespacedKey(plugin, "is_revive_head");
            SkullMeta inputMeta = (SkullMeta) items[4].getItemMeta();
            ItemStack result = ItemStack.of(Material.PLAYER_HEAD);
            PlayerProfile profile = inputMeta.getPlayerProfile();
            SkullMeta resultMeta = (SkullMeta) result.getItemMeta();
            resultMeta.setPlayerProfile(profile);
            result.setItemMeta(resultMeta);
            ItemMeta meta = result.getItemMeta();
            if (plugin.api.isSpecialized(meta)) return null;
            meta.getPersistentDataContainer().set(reviveKey, PersistentDataType.BOOLEAN, true);
            meta.lore(List.of(Component.text("Revive")));
            result.setItemMeta(meta);
            return result;
        }
        return null;
    }
    public static ItemStack resolveLifeHead(Plugin bukkitPlugin, ItemStack[] items){
        if (bukkitPlugin instanceof PiglysPlayerHeadDrops plugin) {
            NamespacedKey lifeKey = new NamespacedKey(plugin, "is_life_head");
            SkullMeta inputMeta = (SkullMeta) items[4].getItemMeta();
            ItemStack result = ItemStack.of(Material.PLAYER_HEAD);
            PlayerProfile profile = inputMeta.getPlayerProfile();
            SkullMeta resultMeta = (SkullMeta) result.getItemMeta();
            resultMeta.setPlayerProfile(profile);
            result.setItemMeta(resultMeta);
            ItemMeta meta = result.getItemMeta();
            if (plugin.api.isSpecialized(meta)) return null;
            meta.getPersistentDataContainer().set(lifeKey, PersistentDataType.BOOLEAN, true);
            meta.lore(List.of(Component.text("Extra Life")));
            result.setItemMeta(meta);
            return result;
        }
        return null;
    }
    public static ItemStack resolveDeathBond(Plugin bukkitPlugin, ItemStack[] items){
        if (bukkitPlugin instanceof PiglysPlayerHeadDrops plugin) {
            NamespacedKey bondKey = new NamespacedKey(plugin, "is_death_bond");
            NamespacedKey linkedWithKey = new NamespacedKey(plugin, "linked_with");
            if (!Objects.equals(plugin.api.getOwnerName(items[0]), plugin.api.getOwnerName(items[1])) || !Objects.equals(plugin.api.getOwnerName(items[1]), plugin.api.getOwnerName(items[2])))
                return null;
            if (!Objects.equals(plugin.api.getOwnerName(items[6]), plugin.api.getOwnerName(items[7])) || !Objects.equals(plugin.api.getOwnerName(items[7]), plugin.api.getOwnerName(items[8])))
                return null;

            OfflinePlayer player0 = APIManager.getOwner(items[0]);

            PlayerProfile p0 = player0.getPlayerProfile();

            ItemStack returnItem = ItemStack.of(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) returnItem.getItemMeta();
            meta.setPlayerProfile(p0);
            meta.itemName(Component.text("Death Bond"));
            meta.lore(List.of(Component.text("Bound to " + plugin.api.getOwnerName(items[6]))));
            meta.getPersistentDataContainer().set(linkedWithKey, PersistentDataType.STRING, plugin.api.getOwnerName(items[6]));
            meta.getPersistentDataContainer().set(bondKey, PersistentDataType.BOOLEAN, true);
            returnItem.setItemMeta(meta);

            return returnItem;
        }
        return null;
    }

}
