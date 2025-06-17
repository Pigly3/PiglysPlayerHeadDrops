package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@NullMarked
public class DecorateCommand implements BasicCommand {
    Plugin plugin;
    public DecorateCommand(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        ItemStack item = ((Player) Objects.requireNonNull(commandSourceStack.getExecutor())).getInventory().getItemInMainHand();
        if (item != null && item.getType() == Material.PLAYER_HEAD){
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            meta.lore(List.of(Component.text("Decorative")));
            item.setItemMeta(meta);
        } else {
            ((Player) commandSourceStack.getExecutor()).sendMessage("This command can only be run on a head.");
        }
    }
}
