package dev.Pigly3.piglysPlayerHeadDrops;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class PiglysPlayerHeadDrops extends JavaPlugin {
    public APIManager api;
    @Override
    public void onEnable() {
        if (!getConfig().getBoolean("enabled")) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        api = new APIManager(this);
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
            Material material = getConfig().getBoolean("disguise.cheaperCraft") ? Material.AMETHYST_SHARD : Material.ECHO_SHARD;
            NBTCraftingRecipe disguiseRecipe = new NBTCraftingRecipe("DisguiseHead", CustomCraftingRecipes::resolveDisguiseHead, this, new Material[]{
                    material, material, material,
                    material, Material.PLAYER_HEAD, material,
                    material, material, material
            });
            disguiseRecipe.register();
        }
        if (getConfig().getBoolean("lifeSystem.revives.crafting") && getConfig().getBoolean("lifeSystem.enabled")) {
            if (getConfig().getBoolean("lifeSystem.revives.lightCraft")){
                NBTCraftingRecipe reviveRecipe = new NBTCraftingRecipe("ReviveHead", CustomCraftingRecipes::resolveReviveHead, this, new Material[]{
                        Material.NETHERITE_SCRAP, Material.NETHERITE_INGOT, Material.NETHERITE_SCRAP,
                        Material.DIAMOND, Material.PLAYER_HEAD, Material.DIAMOND,
                        Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
                });
                reviveRecipe.register();
            } else {
                NBTCraftingRecipe reviveRecipe = new NBTCraftingRecipe("ReviveHead", CustomCraftingRecipes::resolveReviveHead, this, new Material[]{
                        Material.NETHERITE_INGOT, Material.NETHERITE_INGOT, Material.NETHERITE_INGOT,
                        Material.NETHERITE_SCRAP, Material.PLAYER_HEAD, Material.NETHERITE_SCRAP,
                        Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
                });
                reviveRecipe.register();
            }
        }
        if (getConfig().getBoolean("lifeSystem.extraLives.crafting") && getConfig().getBoolean("lifeSystem.enabled")) {
            NBTCraftingRecipe lifeRecipe = new NBTCraftingRecipe("ExtraLifeHead", CustomCraftingRecipes::resolveLifeHead, this, new Material[]{
                    Material.DIAMOND, Material.NETHERITE_INGOT, Material.DIAMOND,
                    Material.DIAMOND, Material.PLAYER_HEAD, Material.DIAMOND,
                    Material.DIAMOND, Material.DIAMOND, Material.DIAMOND
            });
            lifeRecipe.register();
        }
        if (getConfig().getBoolean("deathBond.crafting")){
            NBTCraftingRecipe deathBondRecipe = new NBTCraftingRecipe("DeathBond", CustomCraftingRecipes::resolveDeathBond, this, new Material[]{
                    Material.PLAYER_HEAD, Material.PLAYER_HEAD, Material.PLAYER_HEAD,
                    Material.IRON_CHAIN, Material.CREAKING_HEART, Material.IRON_CHAIN,
                    Material.PLAYER_HEAD, Material.PLAYER_HEAD, Material.PLAYER_HEAD
            });
            deathBondRecipe.register();
        }
        if (!getConfig().getBoolean("headProtection.headsDespawn") || getConfig().getBoolean("headProtection.invulnerableHeads")){
            getServer().getPluginManager().registerEvents(new HeadInvulnerabilityListeners(this), this);
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
