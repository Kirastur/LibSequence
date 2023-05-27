package de.polarwolf.libsequence.reload;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Reload config later, so other plugins can register additional actions before
 * config gets loaded
 *
 */
public class LibSequenceReloaderConfigFileScheduler extends BukkitRunnable {

	protected final Plugin customPlugin;
	protected final LibSequenceReloaderConfigFile reloaderConfigFile;
	protected final LibSequenceToken ownerToken;
	protected final String fileName;
	protected final String fileSection;

	public LibSequenceReloaderConfigFileScheduler(Plugin customPlugin, LibSequenceReloaderConfigFile reloaderConfigFile,
			LibSequenceToken ownerToken, String fileName, String fileSection) {
		this.customPlugin = customPlugin;
		this.reloaderConfigFile = reloaderConfigFile;
		this.ownerToken = ownerToken;
		this.fileName = fileName;
		this.fileSection = fileSection;
		reloaderConfigFile.getReloaderHelper().runTask(this);
	}

	@Override
	public void run() {
		try {
			int count = reloaderConfigFile.add(customPlugin, ownerToken, fileName, fileSection);
			reloaderConfigFile.getReloaderHelper().printSequencesLoaded(customPlugin, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
