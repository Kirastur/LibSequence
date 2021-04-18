package de.polarwolf.libsequence.integrations;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class LibSequenceIntegrationWorldguard {
	
	public static final int ERR_OK = 0;
	public static final int ERR_PLAYEROUTSIDE = 1;
	public static final int ERR_GENERIC = -1;
	public static final int ERR_NOWORLD = -2;
	public static final int ERR_NOREGION = -3;
	
	protected RegionManager getRegionManager(World world) {
		try {
			return WorldGuard
				.getInstance()
				.getPlatform()
				.getRegionContainer()
				.get(BukkitAdapter.adapt(world));
		} catch (Exception e) {
			return null;
		}		
	}
	
	protected ProtectedRegion getRegion(RegionManager regionManager, String regionName) {
		try {
			return regionManager.getRegion(regionName);
		} catch (Exception e) {
			return null;
		}		
	}
	
	public int testPlayer(Player player, String regionName) {
		if ((player == null) || (regionName == null) || (regionName.isEmpty())) {
			return ERR_GENERIC;
		}

		World world = player.getWorld();
		RegionManager regionManager = getRegionManager(world);
		if (regionManager == null) {
			return ERR_NOWORLD;
		}
		
		ProtectedRegion protectedRegion = getRegion(regionManager, regionName);
		if (protectedRegion == null) {
			return ERR_NOREGION;
		}
		
    	Location location = player.getLocation();
    	BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
    	if (!protectedRegion.contains (vector)) {
    		return ERR_PLAYEROUTSIDE;
    	}
    	
    	return ERR_OK;
	}

}
