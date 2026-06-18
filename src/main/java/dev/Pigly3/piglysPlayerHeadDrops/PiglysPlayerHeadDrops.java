package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PiglysPlayerHeadDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getConfig().getBoolean("enabled")) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        boolean replaceConfig = false;
        if (!Objects.equals(getConfig().getString("version"), getPluginMeta().getVersion())){
            replaceConfig = true;
            getLogger().severe("Config is outdated, applying latest config.");
        }

        saveResource("cooldowns.yml", true);
        saveResource("lives.yml", false);
        saveResource("config.yml", replaceConfig);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamagedListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        if (getConfig().getBoolean("disguise.craftingEnabled")) {
            Material material = Material.ECHO_SHARD;
            if (getConfig().getBoolean("disguise.cheaperCraft")) material = Material.AMETHYST_SHARD;

            NBTCraftingRecipe disguiseRecipe = new NBTCraftingRecipe("DisguiseHead", CustomCraftingRecipes::resolveDisguiseHead, this, new Material[]{
                    material, material, material,
                    material, Material.PLAYER_HEAD, material,
                    material, material, material
            });
            disguiseRecipe.register();
        }
        if (getConfig().getBoolean("lifeSystem.enabled")){
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
