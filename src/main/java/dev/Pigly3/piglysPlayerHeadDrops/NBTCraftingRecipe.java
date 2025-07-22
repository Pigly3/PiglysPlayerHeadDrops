package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

public class NBTCraftingRecipe implements Listener {
    //only works in crafting table
    BiFunction<Plugin, ItemStack[], ItemStack> resolve;
    Plugin plugin;
    Material[] pattern;
    String pluginSpecificID;
    public NBTCraftingRecipe (String pluginSpecificID, BiFunction<Plugin, ItemStack[], ItemStack> resolve, Plugin plugin, Material[] pattern){
        this.resolve = resolve;
        this.plugin = plugin;
        this.pattern = pattern;
        this.pluginSpecificID = pluginSpecificID;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event){
        ItemStack[] craftingGrid = event.getInventory().getMatrix();
        for (int i = 0; i < 9; i++){
            ItemStack gridItem = craftingGrid[i];
            Material patternItem = pattern[i];
            if (gridItem == null & patternItem == null) continue;
            if (gridItem == null || patternItem == null) return;
            if (gridItem.getType() != patternItem) return;
        }
        event.getInventory().setResult(resolve.apply(plugin, craftingGrid));

    }
    public void register(){
        NamespacedKey key = new NamespacedKey(plugin, "WarriorSword");
        ItemStack item = ItemStack.of(Material.DIRT);
        ShapedRecipe recipe = new ShapedRecipe(key, item);

        recipe.shape("ABC", "DEF", "GHI");
        recipe.setIngredient('A', pattern[0]);
        recipe.setIngredient('B', pattern[1]);
        recipe.setIngredient('C', pattern[2]);
        recipe.setIngredient('D', pattern[3]);
        recipe.setIngredient('E', pattern[4]);
        recipe.setIngredient('F', pattern[5]);
        recipe.setIngredient('G', pattern[6]);
        recipe.setIngredient('H', pattern[7]);
        recipe.setIngredient('I', pattern[8]);

        Bukkit.getServer().addRecipe(recipe);
        Bukkit.getServer().getPluginManager().registerEvents(this ,plugin);
    }
}
