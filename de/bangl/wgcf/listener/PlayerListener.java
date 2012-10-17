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
 * @author BangL
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
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
                        event.getPlayer().sendMessage(ChatColor.RED + "onPlayerChat");
        this.handleCommand(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
                        event.getPlayer().sendMessage(ChatColor.RED + "onPlayerCommandPreprocess");
        this.handleCommand(event);
    }

    private void handleCommand(PlayerEvent event) {
        
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        
        String commandName = "";
        if (event instanceof AsyncPlayerChatEvent) {
            commandName = ((AsyncPlayerChatEvent)event).getMessage().toLowerCase();
        } else if (event instanceof PlayerCommandPreprocessEvent) {
            commandName = ((PlayerCommandPreprocessEvent)event).getMessage().toLowerCase();
        }

                        player.sendMessage(ChatColor.RED + commandName);
        Set<String> blocked = Utils.getFlag(plugin.getWGP(), FLAG_CMDS_BLOCK, player, loc);
                        player.sendMessage(ChatColor.RED + blocked.toString());
        Set<String> allowed = Utils.getFlag(plugin.getWGP(), FLAG_CMDS_ALLOW, player, loc);
                        player.sendMessage(ChatColor.RED + allowed.toString());

        for (String blockedcmd : blocked) {
                        player.sendMessage(ChatColor.RED + blockedcmd);
            if (blocked != null && blockedcmd.startsWith(commandName)) {
                        player.sendMessage(ChatColor.RED + "blocked");
                for (String allowedcmd : allowed) {
                        player.sendMessage(ChatColor.RED + allowedcmd);
                    if (allowed == null || !allowedcmd.startsWith(commandName)) {
                        player.sendMessage(ChatColor.RED + "allowed");
                        String msg = plugin.getConfig().getString("messages.blocked");
                        player.sendMessage(ChatColor.RED + msg);
                        if (event instanceof AsyncPlayerChatEvent) {
                            ((AsyncPlayerChatEvent)event).setCancelled(true);
                        } else if (event instanceof PlayerCommandPreprocessEvent) {
                            ((PlayerCommandPreprocessEvent)event).setCancelled(true);
                        }
                        return;
                    }
                }
            }
        }
    }
}
