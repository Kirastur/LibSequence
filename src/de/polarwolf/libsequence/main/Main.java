package de.polarwolf.libsequence.main;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.libsequence.api.LibSequenceAPI;
import de.polarwolf.libsequence.api.LibSequenceController;
import de.polarwolf.libsequence.api.LibSequenceProvider;
import de.polarwolf.libsequence.commands.LibSequenceCommand;
import de.polarwolf.libsequence.directories.LibSequenceDirectory;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.logger.LibSequenceLoggerDefault;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.orchestrator.LibSequenceSequencer;
import de.polarwolf.libsequence.orchestrator.LibSequenceStartOptions;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Minecraft plugin wrapper for the orchestrator.
 *
 */
public final class Main extends JavaPlugin {

	public static final int PLUGINID_LIBSEQUENCE = 10835;
	public static final String COMMAND_NAME = "sequence";

	protected LibSequenceToken apiToken = null;
	protected LibSequenceAPI lsAPI = null;

	@Override
	public void onEnable() {

		// Copy config from .jar if it dosn't exists
		saveDefaultConfig();

		// Generate our API Token
		apiToken = new LibSequenceToken();

		// Read modules-to-enable from config
		boolean startupEnableCommands = getConfig().getBoolean("startup.enableCommands", true);
		boolean startupEnableAPI = getConfig().getBoolean("startup.enableAPI", true);
		int startupMaxCurrentSequences = getConfig().getInt("startup.maxCurrentSequences",
				LibSequenceOrchestrator.DEFAULT_MAX_RUNNING_SEQUENCES);

		// Enable bStats Metrics
		new Metrics(this, PLUGINID_LIBSEQUENCE);

		// Register Command and TabCompleter
		if (startupEnableCommands) {
			new LibSequenceCommand(this, COMMAND_NAME);
		}

		// Check if we should start the shared sequencer
		if (!startupEnableAPI) {
			if (startupEnableCommands) {
				getLogger().info("Starting of the shared sequencer is supressed and the API is not set.");
				getLogger().info("The command cannot be used until a 3rd party plugin initializes the API.");
			} else {
				getLogger().info("LibSequence is in passive mode. Only private sequencers are possible");
			}
			return;
		}

		// read other flags from config
		boolean orchestratorEnableCommandAction = getConfig().getBoolean("orchestrator.enableCommandAction", true);
		boolean orchestratorEnableChainEvents = getConfig().getBoolean("orchestrator.enableChainEvents", true);
		boolean controllerPublishLocalSequences = getConfig().getBoolean("controller.publishLocalSequences", true);
		boolean controllerEnableDebugOutput = getConfig().getBoolean("controller.enableDebugOutput", true);

		// Start Sequencer
		LibSequenceStartOptions startOptions = new LibSequenceStartOptions(startupMaxCurrentSequences,
				orchestratorEnableCommandAction, orchestratorEnableChainEvents);
		LibSequenceSequencer sequencer;
		try {
			sequencer = new LibSequenceSequencer(this, apiToken, startOptions);
		} catch (LibSequenceException e) {
			e.printStackTrace();
			getLogger().warning("Failed to start LibSequence orchestrator");
			return;
		}

		// Print Info about integrations
		if (sequencer.hasIntegrationWorldguard()) {
			getLogger().info("Link to WorldGuard established");
		}
		if (sequencer.hasIntegrationPlaceholderAPI()) {
			getLogger().info("Link to PlaceholderAPI established");
		}

		// Create the public directory
		LibSequenceDirectory directory = new LibSequenceDirectory(this, apiToken, sequencer);

		// Start Controller
		LibSequenceLoggerDefault logger = new LibSequenceLoggerDefault(this);
		logger.setEnableConsoleNotifications(controllerEnableDebugOutput);
		logger.setEnableInitiatorNotifications(controllerEnableDebugOutput);
		LibSequenceController controller;
		if (controllerPublishLocalSequences) {
			controller = new LibSequenceController(this, directory, logger);
		} else {
			controller = new LibSequenceController(null, directory, logger);
		}

		// Build the API
		lsAPI = new LibSequenceAPI(apiToken, sequencer, directory, controller);
		LibSequenceProvider.setAPI(lsAPI);

		// Now initialization is done and we can print the finish message
		getLogger().info("LibSequence has successfully started");

	}

	@Override
	public void onDisable() {
		if (lsAPI != null) {
			boolean result = lsAPI.disable(apiToken);
			lsAPI = null;
			if (!result) {
				getLogger().warning("Could not shutdown orchestrator");
			}
		}
		LibSequenceProvider.setAPI(null);
	}

}