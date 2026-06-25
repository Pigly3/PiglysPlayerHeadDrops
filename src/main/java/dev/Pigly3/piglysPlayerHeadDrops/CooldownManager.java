package dev.Pigly3.piglysPlayerHeadDrops;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private static final HashMap<String, Instant> headCooldowns = new HashMap<>();
    private static final HashMap<String, Instant> disguiseCooldowns = new HashMap<>();

    public static Instant getNormalHeadCooldown(Player player){
        return getNormalHeadCooldown(player.getUniqueId());
    }

    public static Instant getNormalHeadCooldown(UUID uuid){
        return headCooldowns.get(uuid.toString().replace("-", ""));
    }

    public static void setNormalHeadCooldown(UUID uuid, Instant end){
        headCooldowns.put(uuid.toString().replace("-", ""), end);
    }

    public static boolean hasNormalHeadCooldown(Player player){
        return hasNormalHeadCooldown(player.getUniqueId());
    }

    public static boolean hasNormalHeadCooldown(UUID uuid){
        return getNormalHeadCooldown(uuid) != null && Instant.now().isAfter(getNormalHeadCooldown(uuid));
    }

    public static void setNormalHeadUseCooldownTime(Player player, long seconds){
        setNormalHeadUseCooldownTime(player.getUniqueId(), seconds);
    }

    public static void setNormalHeadUseCooldownTime(UUID uuid, long seconds) {
        setNormalHeadCooldown(uuid, Instant.now().plusSeconds(seconds));
    }

    public static Instant getDisguiseCooldown(Player player){
        return getDisguiseCooldown(player.getUniqueId());
    }

    public static Instant getDisguiseCooldown(UUID uuid){
        return disguiseCooldowns.get(uuid.toString().replace("-", ""));
    }

    public static void setDisguiseCooldown(UUID uuid, Instant end){
        disguiseCooldowns.put(uuid.toString().replace("-", ""), end);
    }

    public static boolean hasDisguiseCooldown(Player player){
        return hasDisguiseCooldown(player.getUniqueId());
    }

    public static boolean hasDisguiseCooldown(UUID uuid){
        return getDisguiseCooldown(uuid) != null && Instant.now().isAfter(getDisguiseCooldown(uuid));
    }

    public static void setDisguiseCooldownTime(Player player, long seconds){
        setDisguiseCooldownTime(player.getUniqueId(), seconds);
    }

    public static void setDisguiseCooldownTime(UUID uuid, long seconds){
        setDisguiseCooldown(uuid, Instant.now().plusSeconds(seconds));
    }

}
