package de.polarwolf.libsequence.integrations;

import org.bukkit.plugin.Plugin;

public class LibSequenceIntegrationManager {
	
	protected final LibSequenceIntegrationPlaceholderAPI integrationPlaceholderAPI;
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	

	public LibSequenceIntegrationManager(Plugin plugin) {
		if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			integrationPlaceholderAPI = new LibSequenceIntegrationPlaceholderAPI();
		}	else {
			integrationPlaceholderAPI = null;
		}
		if ((plugin.getServer().getPluginManager().getPlugin("WorldEdit") != null) && (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null)) {
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
