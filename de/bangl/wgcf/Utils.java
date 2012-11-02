/*
 * Copyright (C) 2012 BangL <henno.rickowski@googlemail.com>
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
package de.bangl.wgcf;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.mewin.util.Util;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author BangL <henno.rickowski@googlemail.com>
 */
public final class Utils {
    
    public static WGCustomFlagsPlugin getWGCustomFlags(WGCommandFlagsPlugin plugin) {
        Plugin wgcf = plugin.getServer().getPluginManager().getPlugin("WGCustomFlags");
        if (wgcf == null || !(wgcf instanceof WGCustomFlagsPlugin)) {
            return null;
        }
        return (WGCustomFlagsPlugin)wgcf;
    }

    public static WorldGuardPlugin getWorldGuard(WGCommandFlagsPlugin plugin) {
        Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin)wg;
    }

    public static void loadConfig(WGCommandFlagsPlugin plugin) {
        plugin.getConfig().addDefault("messages.blocked", "You are not allowed to execute this command in this region.");
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    public static boolean cmdAllowedAtLocation(WGCommandFlagsPlugin plugin, String cmd, Location loc) {
        return Util.flagAllowedAtLocation(plugin.getWGP(), cmd, loc, WGCommandFlagsPlugin.FLAG_CMDS_ALLOW, WGCommandFlagsPlugin.FLAG_CMDS_BLOCK, null);
    }
}
