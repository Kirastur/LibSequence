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
import de.polarwolf.libsequence.checks.LibSequenceCheckList;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckOperator;
import de.polarwolf.libsequence.checks.LibSequenceCheckPermission;
import de.polarwolf.libsequence.checks.LibSequenceCheckRegion;
import de.polarwolf.libsequence.checks.LibSequenceCheckSendertype;
import de.polarwolf.libsequence.conditions.LibSequenceConditionBoolean;
import de.polarwolf.libsequence.conditions.LibSequenceConditionManager;
import de.polarwolf.libsequence.conditions.LibSequenceConditionNumeric;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.includes.LibSequenceIncludeAll;
import de.polarwolf.libsequence.includes.LibSequenceIncludeCondition;
import de.polarwolf.libsequence.includes.LibSequenceIncludeConsole;
import de.polarwolf.libsequence.includes.LibSequenceIncludeInitiator;
import de.polarwolf.libsequence.includes.LibSequenceIncludeList;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;
import de.polarwolf.libsequence.includes.LibSequenceIncludeOperator;
import de.polarwolf.libsequence.includes.LibSequenceIncludePermission;
import de.polarwolf.libsequence.includes.LibSequenceIncludeRegion;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationManager;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderAPI;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderInternal;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;
import de.polarwolf.libsequence.runnings.LibSequenceRunManager;

public class LibSequenceOrchestrator {

	protected final LibSequenceIntegrationManager integrationManager;
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceConditionManager conditionManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceConfigManager configManager;
	protected final LibSequenceChainManager chainManager;
	protected final LibSequenceRunManager runManager;
	

	public LibSequenceOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) {
		if (startOptions == null) {
			startOptions = new LibSequenceStartOptions();
		}
		integrationManager = createIntegrationManager(plugin);
		placeholderManager = createPlaceholderManager();
		conditionManager = createConditionManager();
		checkManager = createCheckManager();
		includeManager = createIncludeManager();
		actionManager = createActionManager();
		configManager = createConfigManager();
		chainManager = createChainManager();
		runManager = createRunManager();
		registerPredefinedPlaceholders();
		registerPredefinedConditions();
		registerPredefinedChecks();
		registerPredefinedIncludes();
		registerPredefinedActions(startOptions.getOption(LibSequenceStartOptions.OPTION_INCLUDE_COMMAND));
		registerPredefinedChains(plugin, startOptions.getOption(LibSequenceStartOptions.OPTION_ENABLE_CHAIN_EVENTS));
	}
	

	// Getters
	public LibSequenceIntegrationManager getIntegrationManager() {
		return integrationManager;
	}
	
	public LibSequencePlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public LibSequenceConditionManager getConditionManager() {
		return conditionManager;
	}
	
	public LibSequenceCheckManager getCheckManager() {
		return checkManager;
	}
	
	public LibSequenceIncludeManager getIncludeManager() {
		return includeManager;
	}
	
	public LibSequenceActionManager getActionManager() {
		return actionManager;
	}

	public LibSequenceConfigManager getConfigManager() {
		return configManager;
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
	
	protected LibSequencePlaceholderManager createPlaceholderManager() {
		return new LibSequencePlaceholderManager();
	}

	protected LibSequenceConditionManager createConditionManager() {
		return new LibSequenceConditionManager();
	}
	
	protected LibSequenceCheckManager createCheckManager() {
		return new LibSequenceCheckManager();
	}
	
	protected LibSequenceIncludeManager createIncludeManager() {
		return new LibSequenceIncludeManager();
	}
	
	protected LibSequenceActionManager createActionManager() {
		return new LibSequenceActionManager();
	}

	protected LibSequenceConfigManager createConfigManager() {
		return new LibSequenceConfigManager(getActionManager().getActionValidator());
	}

	protected LibSequenceChainManager createChainManager() {
		return new LibSequenceChainManager();
	}

	protected LibSequenceRunManager createRunManager() {
		return new LibSequenceRunManager (this);
	}
		

	// register PreDefinedElemets
	// Override this if you want to customize registration
	protected void registerPredefinedPlaceholders() {
		getPlaceholderManager().registerPlaceholder(new LibSequencePlaceholderInternal());
		if (getIntegrationManager().hasPlaceholderAPI()) {
			getPlaceholderManager().registerPlaceholder(new LibSequencePlaceholderAPI(getIntegrationManager().getPlaceholderAPI()));
		}
	}
	
	protected void registerPredefinedConditions() {
		getConditionManager().registerCondition(new LibSequenceConditionBoolean());
		getConditionManager().registerCondition(new LibSequenceConditionNumeric());		
	}
	
	protected void registerPredefinedChecks() {
		getCheckManager().registerCheck("check_sendertype", new LibSequenceCheckSendertype());
		getCheckManager().registerCheck("check_operator", new LibSequenceCheckOperator());
		getCheckManager().registerCheck("check_permission", new LibSequenceCheckPermission());
		if (getIntegrationManager().hasWorldguard()) {
			getCheckManager().registerCheck("check_region", new LibSequenceCheckRegion(getIntegrationManager().getWorldguard()));
		}
		getCheckManager().registerCheck("check_condition", new LibSequenceCheckCondition());
		getCheckManager().registerCheck("check_list", new LibSequenceCheckList());
	}
	
	protected void registerPredefinedIncludes() {
		getIncludeManager().registerInclude("include_console", new LibSequenceIncludeConsole());
		getIncludeManager().registerInclude("include_operator", new LibSequenceIncludeOperator());
		getIncludeManager().registerInclude("include_initiator", new LibSequenceIncludeInitiator());
		getIncludeManager().registerInclude("include_permission", new LibSequenceIncludePermission());
		if (getIntegrationManager().hasWorldguard()) {
			getIncludeManager().registerInclude("include_region", new LibSequenceIncludeRegion(getIntegrationManager().getWorldguard()));
		}
		getIncludeManager().registerInclude("include_condition", new LibSequenceIncludeCondition(getPlaceholderManager()));
		getIncludeManager().registerInclude("include_list", new LibSequenceIncludeList());
		getIncludeManager().registerInclude("include_all", new LibSequenceIncludeAll());
	}
	
	protected void registerPredefinedActions(Boolean includeCommand) {
		getActionManager().registerAction("broadcast", new LibSequenceActionBroadcast());
		getActionManager().registerAction("info", new LibSequenceActionInfo());
		getActionManager().registerAction("notify", new LibSequenceActionNotify());
		getActionManager().registerAction("title", new LibSequenceActionTitle());
		getActionManager().registerAction("check", new LibSequenceActionCheck());
		if ((includeCommand == null) || (includeCommand)) {
			getActionManager().registerAction("command", new LibSequenceActionCommand());
		}
	}
	
	protected void registerPredefinedChains(Plugin plugin, Boolean enableChainEvents) {
		if ((enableChainEvents == null) || (enableChainEvents)) {
			getChainManager().registerChain(new LibSequenceChainCommandblock(plugin));
		}
	}
	
}
