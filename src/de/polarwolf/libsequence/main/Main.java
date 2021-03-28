package de.polarwolf.libsequence.main;

import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.libsequence.actions.LibSequenceActionBroadcast;
import de.polarwolf.libsequence.actions.LibSequenceActionCommand;
import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.api.LibSequenceAPI;
import de.polarwolf.libsequence.api.LibSequenceController;
import de.polarwolf.libsequence.api.LibSequenceSequencer;
import de.polarwolf.libsequence.bstats.MetricsLite;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.callback.LibSequenceCallbackGeneric;
import de.polarwolf.libsequence.commands.LibSequenceCommand;
import de.polarwolf.libsequence.commands.LibSequenceCommandCompleter;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;

public final class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {

		// Copy config from .jar if it dosn't exists
		saveDefaultConfig();
		
		// Read modules-to-enable from config
		Boolean startupEnableCommands = getConfig().getBoolean("startup.enableCommands");
		Boolean startupEnableControlAPI = getConfig().getBoolean("startup.enableControlAPI");
		Boolean startupEnableSequencerAPI = getConfig().getBoolean("startup.enableSequencerAPI");
		Boolean commandsEnableCommandAction = getConfig().getBoolean("commands.enableCommandAction");
								
		// If no modules should be activated => goto passive mode
		if ((!startupEnableCommands) && (!startupEnableControlAPI) && (!startupEnableSequencerAPI)) {
			getLogger().info("LibSequence is in passive mode. Only private sequencers are possible");
			return;
		}

		// Create my personal callback, use default for all settings
		LibSequenceCallback callback = new LibSequenceCallbackGeneric(this);
		
		// Startup engine
		LibSequenceSequencer sequencer = new LibSequenceSequencer();
		LibSequenceController controller = new LibSequenceController(sequencer, callback);
		
		// Register actions
		LibSequenceActionResult actionResultBroadcast = sequencer.registerAction("broadcast", new LibSequenceActionBroadcast(this));
		if (actionResultBroadcast.hasError()) {
			getLogger().warning(actionResultBroadcast.toString());
		}
		
		if (commandsEnableCommandAction) {
			LibSequenceActionResult actionResultCommand = sequencer.registerAction("command", new LibSequenceActionCommand(this));
			if (actionResultCommand.hasError()) {
				getLogger().warning(actionResultCommand.toString());
			}
		}

		// Load sequences from config
		LibSequenceConfigResult configResult = sequencer.addSection(callback);
		if (configResult.hasError()) {
			getLogger().warning(configResult.toString());
		}

		// Register commands
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
		if (!startupEnableSequencerAPI) {
			sequencer=null;
		}
		
		if (!startupEnableControlAPI) {
			controller=null;
		}
		
		// create API and provider
		LibSequenceAPI lsAPI = new LibSequenceAPI(sequencer, controller);
		LibSequenceProvider.setAPI(lsAPI);
		
		// Now all is done and we can print the finish message
		getLogger().info("LibSequence has successfully started");
	}
	
}