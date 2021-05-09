package de.polarwolf.libsequence.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.libsequence.api.LibSequenceAPI;
import de.polarwolf.libsequence.api.LibSequenceController;
import de.polarwolf.libsequence.api.LibSequenceSequencer;
import de.polarwolf.libsequence.bstats.MetricsLite;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.callback.LibSequenceCallbackGeneric;
import de.polarwolf.libsequence.commands.LibSequenceCommand;
import de.polarwolf.libsequence.commands.LibSequenceCommandCompleter;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceStartOptions;

public final class Main extends JavaPlugin {
	
	
	@Override
	public void onEnable() {
		
		// Copy config from .jar if it dosn't exists
		saveDefaultConfig();
		
		// Read modules-to-enable from config
		boolean startupEnableCommands = getConfig().getBoolean("startup.enableCommands");
		boolean startupEnableControlAPI = getConfig().getBoolean("startup.enableControlAPI");
		boolean startupEnableSequencerAPI = getConfig().getBoolean("startup.enableSequencerAPI");
		
		// If no modules should be activated => goto passive mode
		if ((!startupEnableCommands) && (!startupEnableControlAPI) && (!startupEnableSequencerAPI)) {
			getLogger().info("LibSequence is in passive mode. Only private sequencers are possible");
			return;
		}

		try {
			// Start Sequencer
			LibSequenceStartOptions startOptions = new LibSequenceStartOptions();
			startOptions.setOption(LibSequenceStartOptions.OPTION_INCLUDE_COMMAND, getConfig().getBoolean("orchestrator.enableCommandAction", true));
			startOptions.setOption(LibSequenceStartOptions.OPTION_ENABLE_CHAIN_EVENTS, getConfig().getBoolean("orchestrator.enableChainEvents", true));
			LibSequenceSequencer sequencer = new LibSequenceSequencer(this, startOptions);
								
			// Print Info about integrations
			if (sequencer.hasIntegrationWorldguard()) {
				getLogger().info("Link to WorldGuard established");			
			}
			if (sequencer.hasIntegrationPlaceholderAPI()) {
				getLogger().info("Link to PlaceholderAPI established");			
			}
		

			// Start Controller
			LibSequenceCallback callback = new LibSequenceCallbackGeneric(this, getConfig().getBoolean("controller.enableDebugOutput", false));
			LibSequenceController controller = new LibSequenceController(sequencer, callback);
		

			// Register minecraft commands
			if (startupEnableCommands) {
				LibSequenceCommand command = new LibSequenceCommand(this, controller);
				getCommand("libsequence").setExecutor(command);			
				getCommand("libsequence").setTabCompleter(new LibSequenceCommandCompleter(command));
			}
		
			// Enable bStats Metrics
			// Please download the bstats-code direct form their homepage
			// or disable the following instruction
			new MetricsLite(this, MetricsLite.PLUGINID_LIBSEQUENCE);

			// Now strip down for API
			LibSequenceSequencer apiSequencer = null;
			LibSequenceController apiController = null;
			if (startupEnableSequencerAPI) {
				apiSequencer = sequencer;
			}
		
			if (startupEnableControlAPI) {
				apiController=controller;
			}
		
			// Create API and Provider
			LibSequenceAPI lsAPI = new LibSequenceAPI(apiSequencer, apiController);
			LibSequenceProvider.setAPI(lsAPI);
		
			// Now initialization is done and we can print the finish message
			getLogger().info("LibSequence has successfully started");
			
			// Load sequences from config
			sequencer.loadSection(callback);
			int nrOfSequencesLoaded = controller.getNames().size();
			if (nrOfSequencesLoaded > 0) {
				getLogger().info("Number of sequences loaded: " + Integer.toString(nrOfSequencesLoaded));
			}

		} catch (LibSequenceException e) {
			getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "ERROR " + e.getMessageCascade());
			getLogger().info("Check your LibSequence plugin.yml or delete it, so a fresh one is created on the next server start");
			getLogger().warning("Cannot load sequences");
		}

	}
	
}