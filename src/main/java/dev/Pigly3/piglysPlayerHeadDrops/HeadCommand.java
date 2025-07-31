package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Objects;

public class HeadCommand implements BasicCommand {
    Plugin plugin;
    public HeadCommand(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Player cause = (Player) commandSourceStack.getExecutor();
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
        playerHeadMeta.setOwningPlayer(cause);
        playerHead.setItemMeta(playerHeadMeta);
        assert cause != null;
        cause.give(playerHead);
    }
}
