package de.bangl.wgcf.listener;

import com.mewin.WGCustomFlags.flags.CustomSetFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StringFlag;
import de.bangl.wgcf.Utils;
import de.bangl.wgcf.WGCommandFlagsPlugin;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;

/**
 *
 * @author BangL, mewin
 */
public class PlayerListener implements Listener {
    private WGCommandFlagsPlugin plugin;

    // Command flags
    public static final CustomSetFlag FLAG_CMDS_BLOCK = new CustomSetFlag("cmds-block", new StringFlag("cmd-block", RegionGroup.ALL));
    public static final CustomSetFlag FLAG_CMDS_ALLOW = new CustomSetFlag("cmds-allow", new StringFlag("cmd-allow", RegionGroup.ALL));

    public PlayerListener(WGCommandFlagsPlugin plugin) {
        this.plugin = plugin;

        // Register custom flags
        plugin.getWGCFP().addCustomFlag(FLAG_CMDS_BLOCK);
        plugin.getWGCFP().addCustomFlag(FLAG_CMDS_ALLOW);

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        this.handleCommand(event);
    }

    private void handleCommand(PlayerCommandPreprocessEvent event) {
        
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        
        String commandName = event.getMessage().toLowerCase().split(" ")[0];
        Set<String> blocked = Utils.getMergedFlag(plugin.getWGP(), FLAG_CMDS_BLOCK, player, loc);
        Set<String> allowed = Utils.getMergedFlag(plugin.getWGP(), FLAG_CMDS_ALLOW, player, loc);

        if (blocked.contains(commandName) && !allowed.contains(commandName))
        {
            String msg = plugin.getConfig().getString("messages.blocked");
            player.sendMessage(ChatColor.RED + msg);
            event.setMessage("/nothingtodohere"); //Block even if other plugin tries to parse it
            event.setCancelled(true);
        }
    }
}
