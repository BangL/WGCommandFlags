package de.bangl.wgcf.listener;

import com.mewin.WGCustomFlags.flags.CustomSetFlag;
import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import de.bangl.wgcf.Utils;
import de.bangl.wgcf.WGCommandFlagsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author BangL, mewin
 */
public class PlayerListener implements Listener {
    private WGCommandFlagsPlugin plugin;

    // Command flags
    public static final CustomSetFlag FLAG_CMDS_BLOCK = new CustomSetFlag("cmds-block", new CommandStringFlag("cmd-block", RegionGroup.ALL));
    public static final CustomSetFlag FLAG_CMDS_ALLOW = new CustomSetFlag("cmds-allow", new CommandStringFlag("cmd-allow", RegionGroup.ALL));

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
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String commandName = event.getMessage().toLowerCase().split(" ")[0];
        if (!Utils.cmdAllowedAtLocation(plugin.getWGP(), commandName, loc))
        {
            String msg = plugin.getConfig().getString("messages.blocked");
            player.sendMessage(ChatColor.RED + msg);
            event.setMessage("/nothingtodohere"); //Block even if other plugin tries to parse it
            event.setCancelled(true);
        }
    }
}
