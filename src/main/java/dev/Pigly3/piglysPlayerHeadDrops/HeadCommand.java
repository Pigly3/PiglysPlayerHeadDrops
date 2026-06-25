package dev.Pigly3.piglysPlayerHeadDrops;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Objects;

public class HeadCommand implements BasicCommand {
    PiglysPlayerHeadDrops plugin;
    public HeadCommand(PiglysPlayerHeadDrops plugin){
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Player cause = (Player) commandSourceStack.getExecutor();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            assert cause != null;
            if (args.length == 0) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        cause.give(plugin.getAPIManager().createHead(cause));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else if (args.length == 1 && cause.isOp()){
                String playerName = args[0];
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        cause.give(plugin.getAPIManager().createHead(MojangAPIAccess.getUUID(playerName), playerName));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}
