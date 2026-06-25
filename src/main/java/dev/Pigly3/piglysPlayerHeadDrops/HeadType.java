package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HeadType {
    private final NamespacedKey key;
    private final BiConsumer<PlayerInteractEvent, PlayerProfile> handler;
    private final Supplier<Boolean> enabledTest;
    private final String name;

    public HeadType(String name, NamespacedKey key, BiConsumer<PlayerInteractEvent, PlayerProfile> handler, Supplier<Boolean> enabledTest) {
        this.key = key;
        this.handler = handler;
        this.enabledTest = enabledTest;
        this.name = name;
    }

    public boolean testFor(ItemStack stack){
        return Boolean.TRUE.equals(stack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN));
    }

    public boolean enabled(){
        return enabledTest.get();
    }

    public void handleUse(PlayerInteractEvent event, PlayerProfile owner){
        handler.accept(event, owner);
    }

    public String getName() {
        return name;
    }
}
