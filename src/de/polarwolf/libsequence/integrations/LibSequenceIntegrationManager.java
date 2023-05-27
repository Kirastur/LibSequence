package de.polarwolf.libsequence.integrations;

import org.bukkit.Bukkit;

import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;

/**
 * Manages the integration to different 3rd party plugins
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/IntegrationManager">Integration
 *      Manager</A> (WIKI)
 */
public class LibSequenceIntegrationManager {

	protected final LibSequenceIntegrationPlaceholderAPI integrationPlaceholderAPI;
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;

	public LibSequenceIntegrationManager(LibSequenceOrchestrator orchestrator) { // NOSONAR
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			integrationPlaceholderAPI = new LibSequenceIntegrationPlaceholderAPI();
		} else {
			integrationPlaceholderAPI = null;
		}
		if ((Bukkit.getPluginManager().getPlugin("WorldEdit") != null)
				&& (Bukkit.getPluginManager().getPlugin("WorldGuard") != null)) {
			integrationWorldguard = new LibSequenceIntegrationWorldguard();
		} else {
			integrationWorldguard = null;
		}
	}

	public boolean hasPlaceholderAPI() {
		return (integrationPlaceholderAPI != null);
	}

	public boolean hasWorldguard() {
		return (integrationWorldguard != null);
	}

	public LibSequenceIntegrationPlaceholderAPI getPlaceholderAPI() {
		return integrationPlaceholderAPI;
	}

	public LibSequenceIntegrationWorldguard getWorldguard() {
		return integrationWorldguard;
	}

}
