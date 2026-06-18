package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class PiglysPlayerHeadDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getConfig().getBoolean("enabled")) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveResource("cooldowns.yml", true);
        saveResource("lives.yml", false);
        saveResource("config.yml", false);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamagedListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        if (getConfig().getBoolean("disguise.craftingEnabled")) {
            NBTCraftingRecipe disguiseRecipe = new NBTCraftingRecipe("DisguiseHead", CustomCraftingRecipes::resolveDisguiseHead, this, new Material[]{
                    Material.AMETHYST_SHARD, Material.AMETHYST_SHARD, Material.AMETHYST_SHARD,
                    Material.AMETHYST_SHARD, Material.PLAYER_HEAD, Material.AMETHYST_SHARD,
                    Material.AMETHYST_SHARD, Material.AMETHYST_SHARD, Material.AMETHYST_SHARD
            });
            disguiseRecipe.register();
        }
        if (getConfig().getBoolean("lifeSystem.revives.crafting")) {
            NBTCraftingRecipe reviveRecipe = new NBTCraftingRecipe("ReviveHead", CustomCraftingRecipes::resolveReviveHead, this, new Material[]{
                    Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT, Material.NETHERITE_SCRAP,
                    Material.DIAMOND, Material.PLAYER_HEAD, Material.DIAMOND,
                    Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
            });
            reviveRecipe.register();
        }
        if (getConfig().getBoolean("lifeSystem.extraLives.crafting")) {
            NBTCraftingRecipe lifeRecipe = new NBTCraftingRecipe("ExtraLifeHead", CustomCraftingRecipes::resolveLifeHead, this, new Material[]{
                    Material.DIAMOND, Material.NETHERITE_INGOT, Material.DIAMOND,
                    Material.DIAMOND, Material.PLAYER_HEAD, Material.DIAMOND,
                    Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
            });
            lifeRecipe.register();
        }
        final DecorateCommand decorateCommand = new DecorateCommand(this);
        final HeadCommand headCommand = new HeadCommand(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            if (getConfig().getBoolean("commands.decorate")){
                commands.registrar().register("decorate", decorateCommand);
            }
            if (getConfig().getBoolean("commands.head")) {
                commands.registrar().register("head", headCommand);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
