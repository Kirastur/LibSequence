package de.polarwolf.libsequence.directories;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.reload.LibSequenceReloadedEvent;

/**
 * Helper for listening on LibSequenceReloaded Events
 *
 */
public class LibSequenceDirectoryListener implements Listener {

	protected final Plugin plugin;
	protected final LibSequenceDirectoryManager directoryManager;

	public LibSequenceDirectoryListener(Plugin plugin, LibSequenceDirectoryManager directoryManager) {
		this.plugin = plugin;
		this.directoryManager = directoryManager;
	}

	public void registerListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void unregisterListener() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLibSequencereloadedEvent(LibSequenceReloadedEvent event) {
		try {
			directoryManager.onReloadedEvent(event.getactionValidator(), event.getLastException());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
