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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@NullMarked
public class DecorateCommand implements BasicCommand {
    Plugin plugin;
    APIManager api;
    public DecorateCommand(Plugin plugin){
        this.plugin = plugin;
        this.api = new APIManager(plugin);
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        ItemStack item = ((Player) Objects.requireNonNull(commandSourceStack.getExecutor())).getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey shadowKey = new NamespacedKey(plugin, "is_disguise_head");
        Boolean isShadow = meta.getPersistentDataContainer().get(shadowKey, PersistentDataType.BOOLEAN);
        if (isShadow != null && isShadow){
            commandSourceStack.getExecutor().sendMessage("Disguise heads cannot be made decorative.");
            return;
        }
        if (item.getType() == Material.PLAYER_HEAD){
            NamespacedKey key = new NamespacedKey(plugin, "is_sterile_head");
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            meta.lore(List.of(Component.text("Decorative")));
            item.setItemMeta(meta);
            try {
                api.addLife(((SkullMeta) item).getPlayerProfile().getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            commandSourceStack.getExecutor().sendMessage("This command can only be run on a head.");
        }
    }
}
