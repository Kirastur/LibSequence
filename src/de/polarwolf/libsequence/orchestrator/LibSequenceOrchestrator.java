package de.polarwolf.libsequence.orchestrator;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceActionBroadcast;
import de.polarwolf.libsequence.actions.LibSequenceActionCheck;
import de.polarwolf.libsequence.actions.LibSequenceActionCommand;
import de.polarwolf.libsequence.actions.LibSequenceActionInfo;
import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.actions.LibSequenceActionNotify;
import de.polarwolf.libsequence.actions.LibSequenceActionTitle;
import de.polarwolf.libsequence.chains.LibSequenceChainCommandblock;
import de.polarwolf.libsequence.chains.LibSequenceChainManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckCondition;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckPermission;
import de.polarwolf.libsequence.checks.LibSequenceCheckRegion;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationManager;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderAPI;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderInternal;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;
import de.polarwolf.libsequence.runnings.LibSequenceRunManager;

public class LibSequenceOrchestrator {

	protected final LibSequenceIntegrationManager integrationManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceConfigManager configManager;
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceChainManager chainManager;
	protected final LibSequenceRunManager runManager;
	
	public LibSequenceOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) {
		if (startOptions == null) {
			startOptions = new LibSequenceStartOptions();
		}
		integrationManager = createIntegrationManager(plugin);
		checkManager = createCheckManager();
		actionManager = createActionManager();
		configManager = createConfigManager();
		placeholderManager = createPlaceholderManager();
		chainManager = createChainManager();
		runManager = createRunManager();
		registerPredefinedChecks();
		registerPredefinedActions(startOptions.getOption(LibSequenceStartOptions.OPTION_INCLUDE_COMMAND));
		registerPredefinedPlaceholders();
		registerPredefinedChains(plugin, startOptions.getOption(LibSequenceStartOptions.OPTION_ENABLE_CHAIN_EVENTS));
	}
	
	// Getters
	public LibSequenceIntegrationManager getIntegrationManager() {
		return integrationManager;
	}
	
	public LibSequenceCheckManager getCheckManager() {
		return checkManager;
	}
	
	public LibSequenceActionManager getActionManager() {
		return actionManager;
	}

	public LibSequenceConfigManager getConfigManager() {
		return configManager;
	}

	public LibSequencePlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public LibSequenceRunManager getRunManager() {
		return runManager;
	}

	public LibSequenceChainManager getChainManager() {
		return chainManager;
	}


	// Initialization
	// Override this if you want to use custom Managers
	protected LibSequenceIntegrationManager createIntegrationManager(Plugin plugin) {
		return new LibSequenceIntegrationManager(plugin);
	}
	
	protected LibSequenceCheckManager createCheckManager() {
		return new LibSequenceCheckManager();
	}
	
	protected LibSequenceActionManager createActionManager() {
		return new LibSequenceActionManager();
	}

	protected LibSequenceConfigManager createConfigManager() {
		return new LibSequenceConfigManager(getActionManager().getActionValidator());
	}

	protected LibSequencePlaceholderManager createPlaceholderManager() {
		return new LibSequencePlaceholderManager();
	}

	protected LibSequenceChainManager createChainManager() {
		return new LibSequenceChainManager();
	}

	protected LibSequenceRunManager createRunManager() {
		return new LibSequenceRunManager (this);
	}
		

	// register PreDefinedElemets
	// Override this if you want to customize registration
	protected void registerPredefinedChecks() {
		getCheckManager().registerCheck("check_condition", new LibSequenceCheckCondition());
		getCheckManager().registerCheck("check_permission", new LibSequenceCheckPermission());
		if (getIntegrationManager().hasWorldguard()) {
			getCheckManager().registerCheck("check_region", new LibSequenceCheckRegion(getIntegrationManager().getWorldguard()));
		}
	}
	
	protected void registerPredefinedActions(Boolean includeCommand) {
		getActionManager().registerAction("broadcast", new LibSequenceActionBroadcast());
		getActionManager().registerAction("info", new LibSequenceActionInfo());
		getActionManager().registerAction("notify", new LibSequenceActionNotify());
		getActionManager().registerAction("title", new LibSequenceActionTitle());
		getActionManager().registerAction("check", new LibSequenceActionCheck(getCheckManager()));
		if ((includeCommand == null) || (includeCommand)) {
			getActionManager().registerAction("command", new LibSequenceActionCommand());
		}
	}
	protected void registerPredefinedPlaceholders() {
		getPlaceholderManager().registerPlaceholder(new LibSequencePlaceholderInternal());
		if (getIntegrationManager().hasPlaceholderAPI()) {
			getPlaceholderManager().registerPlaceholder(new LibSequencePlaceholderAPI(getIntegrationManager().getPlaceholderAPI()));
		}
	}
	
	protected void registerPredefinedChains(Plugin plugin, Boolean enableChainEvents) {
		if ((enableChainEvents == null) || (enableChainEvents)) {
			getChainManager().registerChain(new LibSequenceChainCommandblock(plugin));
		}
	}
	
}
