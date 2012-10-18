package de.bangl.wgcf;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.mewin.WGCustomFlags.flags.CustomSetFlag;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bangl.wgcf.listener.PlayerListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author BangL
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

    public static HashSet<String> getFlag(WorldGuardPlugin wgp, CustomSetFlag flag, Player player, Location loc) {
        LocalPlayer wgPlayer = wgp.wrapPlayer(player);
        ApplicableRegionSet regions = wgp.getRegionManager(loc.getWorld())
                .getApplicableRegions(loc);
        Object result = null;
        try {
            result = (HashSet<String>)regions.getFlag(flag, wgPlayer);
        } catch(Exception e) {
            result = new HashSet<>();
        }
        if (result != null) {
            return (HashSet<String>)result;
        } else {
            return new HashSet<>();
        }
    }

    public static HashSet<String> getMergedFlag(WorldGuardPlugin wgp, CustomSetFlag flag, Player player, Location loc)
    {
        //LocalPlayer wgPlayer = wgp.wrapPlayer(player);
        ApplicableRegionSet regions = wgp.getRegionManager(loc.getWorld())
                .getApplicableRegions(loc);
        HashSet<String> result = new HashSet<>();
        
        Iterator<ProtectedRegion> itr = regions.iterator();
        
        while(itr.hasNext())
        {
            HashSet<String> values = (HashSet<String>) itr.next().getFlag(flag);
            
            if (values == null)
            {
                continue;
            }
            
            Iterator<String> itr2 = values.iterator();
            
            while(itr2.hasNext())
            {
                String value = itr2.next();
                
                if (!result.contains(value))
                {
                    result.add(value);
                }
            }
        }
        
        return result;
    }
    
    public static boolean cmdAllowedAtLocation(WorldGuardPlugin wgp, String cmd, Location loc)
    {
        RegionManager rm = wgp.getRegionManager(loc.getWorld());
        if (rm == null)
        {
            return true;
        }
        ApplicableRegionSet regions = rm.getApplicableRegions(loc);
        Iterator<ProtectedRegion> itr = regions.iterator();
        //Map<ProtectedRegion, Boolean> allowedInRegion = new HashMap<>();
        Map<ProtectedRegion, Boolean> regionsToCheck = new HashMap<>();
        Set<ProtectedRegion> ignoredRegions = new HashSet<>();
        
        while(itr.hasNext())
        {
            ProtectedRegion region = itr.next();
            
            if (ignoredRegions.contains(region))
            {
                continue;
            }
            
            Object allowed = cmdAllowedInRegion(region, cmd);
            
            if (allowed != null)
            {
                ProtectedRegion parent = region.getParent();
                
                while(parent != null)
                {
                    ignoredRegions.add(parent);
                    
                    parent = parent.getParent();
                }
                
                regionsToCheck.put(region, (boolean) allowed);
            }
        }
        
        if (regionsToCheck.size() >= 1)
        {
            Iterator<Map.Entry<ProtectedRegion, Boolean>> itr2 = regionsToCheck.entrySet().iterator();
            
            while(itr2.hasNext())
            {
                Map.Entry<ProtectedRegion, Boolean> entry = itr2.next();
                
                ProtectedRegion region = entry.getKey();
                boolean value = entry.getValue();
                
                if (ignoredRegions.contains(region))
                {
                    continue;
                }
                
                if (value) // allow > deny
                {
                    return true;
                }
            }
            
            return false;
        }
        else
        {
            Object allowed = cmdAllowedInRegion(rm.getRegion("__global__"), cmd);
            
            if (allowed != null)
            {
                return (boolean) allowed;
            }
            else
            {
                return true;
            }
        }
    }
    
    public static Object cmdAllowedInRegion(ProtectedRegion region, String cmd)
    {
        HashSet<String> allowedCmds = (HashSet<String>) region.getFlag(PlayerListener.FLAG_CMDS_ALLOW);
        HashSet<String> blockedCmds = (HashSet<String>) region.getFlag(PlayerListener.FLAG_CMDS_BLOCK);
        
        if (allowedCmds != null && allowedCmds.contains(cmd))
        {
            return true;
        }
        else if(blockedCmds != null && blockedCmds.contains(cmd))
        {
            return false;
        }
        else
        {
            return null;
        }
    }
}
