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
import com.mewin.WGCustomFlags.flags.CustomSetFlag;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import de.bangl.wgcf.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author BangL <henno.rickowski@googlemail.com>
 */
public class WGCommandFlagsPlugin extends JavaPlugin {

    // Command flags
    public static final CustomSetFlag FLAG_CMDS_BLOCK = new CustomSetFlag("cmds-block", new CommandStringFlag("cmd-block", RegionGroup.ALL));
    public static final CustomSetFlag FLAG_CMDS_ALLOW = new CustomSetFlag("cmds-allow", new CommandStringFlag("cmd-allow", RegionGroup.ALL));

    // Plugins
    private WorldGuardPlugin pluginWorldGuard;
    private WGCustomFlagsPlugin pluginWGCustomFlags;

    // Listeners
    private PlayerListener listenerPlayer;

    public WGCustomFlagsPlugin getWGCFP() {
        return pluginWGCustomFlags;
    }

    public WorldGuardPlugin getWGP() {
        return pluginWorldGuard;
    }

    @Override
    public void onEnable() {

        // Load config
        Utils.loadConfig(this);

        // Init WorldGuard
        this.pluginWorldGuard = Utils.getWorldGuard(this);
        //TODO: Check for null

        // Init and register custom flags
        this.pluginWGCustomFlags = Utils.getWGCustomFlags(this);

        // Register custom flags
        this.pluginWGCustomFlags.addCustomFlag(FLAG_CMDS_BLOCK);
        this.pluginWGCustomFlags.addCustomFlag(FLAG_CMDS_ALLOW);

        // Register all listeners
        this.listenerPlayer = new PlayerListener(this);
        
    }

    @Override
    public void onDisable() {

        // we nullify all vars, cause it could be a server reload and we don't wanna leave trash in our expensive RAM.
        this.pluginWGCustomFlags = null;
        this.pluginWorldGuard = null;
        this.listenerPlayer = null;

        //saveConfig();
    }
}
