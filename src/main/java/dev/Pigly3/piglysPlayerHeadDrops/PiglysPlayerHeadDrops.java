package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class PiglysPlayerHeadDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("cooldowns.yml", true);
        saveResource("config.yml", false);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamagedListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        NBTCraftingRecipe disguiseRecipe = new NBTCraftingRecipe("DisguiseHead", CustomCraftingRecipes::resolveDisguiseHead, this, new Material[]{
                Material.ECHO_SHARD, Material.ECHO_SHARD, Material.ECHO_SHARD,
                Material.ECHO_SHARD, Material.PLAYER_HEAD, Material.ECHO_SHARD,
                Material.ECHO_SHARD, Material.ECHO_SHARD, Material.ECHO_SHARD
        });
        disguiseRecipe.register();
        final DecorateCommand decorateCommand = new DecorateCommand(this);
        final HeadCommand headCommand = new HeadCommand(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("decorate", decorateCommand);
            commands.registrar().register("head", headCommand);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
