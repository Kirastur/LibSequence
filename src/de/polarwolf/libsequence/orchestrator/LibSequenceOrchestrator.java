package de.polarwolf.libsequence.orchestrator;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceActionBroadcast;
import de.polarwolf.libsequence.actions.LibSequenceActionCheck;
import de.polarwolf.libsequence.actions.LibSequenceActionCommand;
import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionInfo;
import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.actions.LibSequenceActionNone;
import de.polarwolf.libsequence.actions.LibSequenceActionNotify;
import de.polarwolf.libsequence.actions.LibSequenceActionTitle;
import de.polarwolf.libsequence.chains.LibSequenceChainCommandblock;
import de.polarwolf.libsequence.chains.LibSequenceChainManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckCondition;
import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.checks.LibSequenceCheckList;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckOperator;
import de.polarwolf.libsequence.checks.LibSequenceCheckPermission;
import de.polarwolf.libsequence.checks.LibSequenceCheckRegion;
import de.polarwolf.libsequence.checks.LibSequenceCheckSendertype;
import de.polarwolf.libsequence.checks.LibSequenceCheckWorld;
import de.polarwolf.libsequence.conditions.LibSequenceConditionBoolean;
import de.polarwolf.libsequence.conditions.LibSequenceConditionManager;
import de.polarwolf.libsequence.conditions.LibSequenceConditionNumeric;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeAll;
import de.polarwolf.libsequence.includes.LibSequenceIncludeCondition;
import de.polarwolf.libsequence.includes.LibSequenceIncludeConsole;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeInitiator;
import de.polarwolf.libsequence.includes.LibSequenceIncludeList;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;
import de.polarwolf.libsequence.includes.LibSequenceIncludeOperator;
import de.polarwolf.libsequence.includes.LibSequenceIncludePermission;
import de.polarwolf.libsequence.includes.LibSequenceIncludeRegion;
import de.polarwolf.libsequence.includes.LibSequenceIncludeWorld;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationManager;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderAPI;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderInternal;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;
import de.polarwolf.libsequence.runnings.LibSequenceRunManager;
import de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager;

public class LibSequenceOrchestrator {
	
	public static final int DEFAULT_MAX_RUNNING_SEQUENCES = 20;

	protected final LibSequenceIntegrationManager integrationManager;
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceConditionManager conditionManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	protected final LibSequenceSyntaxManager syntaxManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceConfigManager configManager;
	protected final LibSequenceChainManager chainManager;
	protected final LibSequenceRunManager runManager;
	protected final int maxCurrentSequences;
	

	public LibSequenceOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) throws LibSequenceException {
		if (startOptions == null) {
			startOptions = new LibSequenceStartOptions();
		}
		
		int optionsMaxCurrentSequences = startOptions.getMaxRunningSequences();
		if (optionsMaxCurrentSequences < 1) {
			optionsMaxCurrentSequences = DEFAULT_MAX_RUNNING_SEQUENCES;
		}			
		maxCurrentSequences = optionsMaxCurrentSequences;
		
		integrationManager = createIntegrationManager(plugin);
		placeholderManager = createPlaceholderManager();
		conditionManager = createConditionManager();
		checkManager = createCheckManager();
		includeManager = createIncludeManager();
		syntaxManager = createSyntaxManager();
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
	
	public int getMaxCurrentSequences() {
		return maxCurrentSequences;
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
	
	public LibSequenceSyntaxManager getSyntaxManager() {
		return syntaxManager;
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
	
	protected LibSequenceSyntaxManager createSyntaxManager() {
		return new LibSequenceSyntaxManager(getCheckManager(), getIncludeManager());
	}
	
	protected LibSequenceActionManager createActionManager() {
		return new LibSequenceActionManager(getSyntaxManager());
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
	
	protected void registerPredefinedChecks() throws LibSequenceCheckException {
		getCheckManager().registerCheck("check_sendertype", new LibSequenceCheckSendertype());
		getCheckManager().registerCheck("check_operator", new LibSequenceCheckOperator());
		getCheckManager().registerCheck("check_permission", new LibSequenceCheckPermission());
		if (getIntegrationManager().hasWorldguard()) {
			getCheckManager().registerCheck("check_region", new LibSequenceCheckRegion(getIntegrationManager().getWorldguard()));
		}
		getCheckManager().registerCheck("check_condition", new LibSequenceCheckCondition());
		getCheckManager().registerCheck("check_list", new LibSequenceCheckList());
		getCheckManager().registerCheck("check_world", new LibSequenceCheckWorld());
	}
	
	protected void registerPredefinedIncludes() throws LibSequenceIncludeException {
		getIncludeManager().registerInclude("include_console", new LibSequenceIncludeConsole());
		getIncludeManager().registerInclude("include_operator", new LibSequenceIncludeOperator());
		getIncludeManager().registerInclude("include_initiator", new LibSequenceIncludeInitiator());
		getIncludeManager().registerInclude("include_permission", new LibSequenceIncludePermission());
		if (getIntegrationManager().hasWorldguard()) {
			getIncludeManager().registerInclude("include_region", new LibSequenceIncludeRegion(getIntegrationManager().getWorldguard()));
		}
		getIncludeManager().registerInclude("include_condition", new LibSequenceIncludeCondition(getPlaceholderManager()));
		getIncludeManager().registerInclude("include_list", new LibSequenceIncludeList());
		getIncludeManager().registerInclude("include_world", new LibSequenceIncludeWorld());
		getIncludeManager().registerInclude("include_all", new LibSequenceIncludeAll());
	}
	
	protected void registerPredefinedActions(Boolean includeCommand) throws LibSequenceActionException {
		getActionManager().registerAction("none", new LibSequenceActionNone());
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
