package de.polarwolf.libsequence.integrations;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Integration to Worldguard
 *
 */
public class LibSequenceIntegrationWorldguard {

	public static final String WORLDGUARD_NAME = "WorldGuard";
	public static final String ERR_GENERIC = "generic region error";
	public static final String ERR_NOWORLD = "world not found";
	public static final String ERR_NOREGION = "region not found";

	protected RegionManager getRegionManager(World world) throws LibSequenceIntegrationException {
		try {
			return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
		} catch (Exception e) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, LibSequenceException.JAVA_EXCEPTION,
					e.getMessage(), e);
		}
	}

	protected ProtectedRegion getRegion(RegionManager regionManager, String regionName)
			throws LibSequenceIntegrationException {
		try {
			return regionManager.getRegion(regionName);
		} catch (Exception e) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, LibSequenceException.JAVA_EXCEPTION,
					e.getMessage(), e);
		}
	}

	protected boolean contains(ProtectedRegion protectedRegion, BlockVector3 vector)
			throws LibSequenceIntegrationException {
		try {
			return protectedRegion.contains(vector);
		} catch (Exception e) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, LibSequenceException.JAVA_EXCEPTION,
					e.getMessage(), e);
		}
	}

	public boolean testPlayer(Player player, String regionName) throws LibSequenceIntegrationException {
		if ((player == null) || (regionName == null) || (regionName.isEmpty())) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, ERR_GENERIC, null);
		}

		World world = player.getWorld();
		RegionManager regionManager = getRegionManager(world);
		if (regionManager == null) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, ERR_NOWORLD, null);
		}

		ProtectedRegion protectedRegion = getRegion(regionManager, regionName);
		if (protectedRegion == null) {
			throw new LibSequenceIntegrationException(WORLDGUARD_NAME, ERR_NOREGION, regionName);
		}

		Location location = player.getLocation();
		BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

		return contains(protectedRegion, vector);
	}

}
