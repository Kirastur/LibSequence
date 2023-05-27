package de.polarwolf.libsequence.reload;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Gateway for a custom reloader to LibSequence functions
 *
 */
public class LibSequenceReloaderHelper {

	protected final Plugin plugin;
	protected final LibSequenceConfigManager configManager;
	protected final LibSequenceReloadManager reloadManager;
	protected final LibSequenceActionValidator actionValidator;

	public LibSequenceReloaderHelper(Plugin plugin, LibSequenceReloadManager reloadManager,
			LibSequenceConfigManager configManager, LibSequenceActionValidator actionValidator) {
		this.plugin = plugin;
		this.reloadManager = reloadManager;
		this.configManager = configManager;
		this.actionValidator = actionValidator;
	}

	public LibSequenceActionValidator getActionValidator() {
		return actionValidator;
	}

	public boolean hasSection(LibSequenceToken ownerToken) {
		return configManager.hasSection(ownerToken);
	}

	public void preregisterSection(LibSequenceToken ownerToken, String sectionName) {
		configManager.preregisterSection(ownerToken, sectionName);
	}

	public void setSection(LibSequenceToken ownerToken, LibSequenceConfigSection newSection)
			throws LibSequenceConfigException {
		configManager.setSection(ownerToken, newSection);
	}

	public void removeSection(LibSequenceToken ownerToken) {
		if (configManager.hasSection(ownerToken))
			try {
				configManager.unregisterSection(ownerToken);
			} catch (Exception e) {
				// Do nothing, because only exception could be "not found"
			}
	}

	public void sendReloadedEvent(int count, boolean isPartial, LibSequenceException lastException) {
		reloadManager.sendReloadedEvent(count, isPartial, lastException);
	}

	public void runTask(BukkitRunnable bukkitRunable) {
		bukkitRunable.runTask(plugin);
	}

	public Logger getLibSequenceLogger() {
		return plugin.getLogger();
	}

	public void printSequencesLoaded(Plugin customPlugin, int count) {
		if (count == 0) {
			String s = String.format("No sequences loaded from %s", customPlugin.getName());
			getLibSequenceLogger().warning(s);
		}
		if (count == 1) {
			String s = String.format("1 sequence loaded from %s", customPlugin.getName());
			getLibSequenceLogger().info(s);
		}
		if (count > 1) {
			String s = String.format("%d sequences loaded from %s", count, customPlugin.getName());
			getLibSequenceLogger().info(s);
		}
	}

}
