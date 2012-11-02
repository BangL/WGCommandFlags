/*
 * Copyright (C) 2012 BangL <henno.rickowski@googlemail.com>
 *                    mewin <mewin001@hotmail.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.bangl.wgcf.listener;

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
 * @author BangL <henno.rickowski@googlemail.com>
 * @author mewin <mewin001@hotmail.de>
 */
public class PlayerListener implements Listener {
    private WGCommandFlagsPlugin plugin;

    public PlayerListener(WGCommandFlagsPlugin plugin) {
        this.plugin = plugin;

        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        String commandName = event.getMessage().toLowerCase().split(" ")[0];
        if (!Utils.cmdAllowedAtLocation(this.plugin, commandName, loc)) {
            String msg = plugin.getConfig().getString("messages.blocked");
            player.sendMessage(ChatColor.RED + msg);
            event.setMessage("/nothingtodohere"); //Block even if other plugin tries to parse it
            event.setCancelled(true);
        }
    }
}
