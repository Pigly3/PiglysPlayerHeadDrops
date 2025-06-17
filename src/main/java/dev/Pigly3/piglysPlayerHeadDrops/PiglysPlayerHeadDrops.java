package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class PiglysPlayerHeadDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("cooldowns.yml", false);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamagedListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        final DecorateCommand decorateCommand = new DecorateCommand(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("decorate", decorateCommand);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
