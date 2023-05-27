package de.polarwolf.libsequence.reload;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;

/**
 * Manages the reload of sequences
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/ReloadManager">Reload
 *      Manager</A> (WIKI)
 */
public class LibSequenceReloadManager {

	protected LibSequenceReloaderHelper reloaderHelper;
	protected List<LibSequenceReloader> reloaders = new ArrayList<>();

	public LibSequenceReloadManager(LibSequenceOrchestrator orchestrator) {
		Plugin plugin = orchestrator.getPlugin();
		LibSequenceActionManager actionManager = orchestrator.getActionManager();
		LibSequenceConfigManager configManager = orchestrator.getConfigManager();
		LibSequenceActionValidator actionValidator = actionManager.getActionValidator();
		reloaderHelper = new LibSequenceReloaderHelper(plugin, this, configManager, actionValidator);
	}

	protected LibSequenceReloaderHelper getReloaderHelper() {
		return reloaderHelper;
	}

	public void add(LibSequenceReloader newReloader) {
		newReloader.setHelper(getReloaderHelper());
		reloaders.add(newReloader);
	}

	public void remove(LibSequenceReloader oldReloader) {
		reloaders.remove(oldReloader);
		oldReloader.clear();
		oldReloader.setHelper(null);
	}

	protected void sendReloadedEvent(int count, boolean isPartial, LibSequenceException lastException) {
		LibSequenceReloadedEvent reloadedEvent = new LibSequenceReloadedEvent(reloaderHelper.getActionValidator(),
				count, isPartial, lastException);
		Bukkit.getPluginManager().callEvent(reloadedEvent);
	}

	public int reload() throws LibSequenceException {
		int count = 0;
		LibSequenceException lastException = null;
		for (LibSequenceReloader myReloader : reloaders)
			try {
				count = count + myReloader.reload();
			} catch (LibSequenceException e) {
				lastException = e;
			}
		sendReloadedEvent(count, true, lastException);
		if (lastException != null) {
			throw lastException;
		}
		return count;
	}

	public void disable() {
		for (LibSequenceReloader myReloader : new ArrayList<>(reloaders)) {
			remove(myReloader);
		}
		reloaderHelper = null;
	}

}
