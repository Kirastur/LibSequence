package de.polarwolf.libsequence.api;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

import java.util.Set;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceAction;
import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.chains.LibSequenceChain;
import de.polarwolf.libsequence.checks.LibSequenceCheck;
import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.includes.LibSequenceInclude;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.orchestrator.LibSequenceStartOptions;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholder;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceSequencer {

	protected final LibSequenceOrchestrator orchestrator;
	

	public LibSequenceSequencer(Plugin plugin, LibSequenceStartOptions startOptions) throws LibSequenceException {
		orchestrator = createOrchestrator(plugin, startOptions);
	}
	

	// IntegrationManager Interface
	public boolean hasIntegrationPlaceholderAPI() {
		return orchestrator.getIntegrationManager().hasPlaceholderAPI();		
	}
	

	public boolean hasIntegrationWorldguard() {
		return orchestrator.getIntegrationManager().hasWorldguard();		
	}

	
	// ActionManager Interface
	public void registerAction(String actionName, LibSequenceAction action) throws LibSequenceActionException {
		orchestrator.getActionManager().registerAction(actionName, action);		
	}
	

	protected LibSequenceActionValidator getActionValidator() {
		return orchestrator.getActionManager().getActionValidator();
	}


	// RunManager Interface
	public LibSequenceRunningSequence executeForeignSequence(LibSequenceCallback callback, String securityToken, LibSequenceRunOptions runOptions) throws LibSequenceRunException {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		if (sequence==null) {
			throw new LibSequenceRunException(null, 0, LSRERR_NOT_FOUND, null);
		}
		return orchestrator.getRunManager().execute(callback, sequence, securityToken, runOptions);
	}
	

	public LibSequenceRunningSequence executeOwnSequence(LibSequenceCallback callback, String sequenceName, LibSequenceRunOptions runOptions) throws LibSequenceRunException  {
		try {
			LibSequenceConfigSequence sequence = orchestrator.getConfigManager().getOwnSequence(callback, sequenceName);
			String securityToken = sequence.getSecurityToken(callback);
			return orchestrator.getRunManager().execute(callback, sequence, securityToken, runOptions);
		} catch (LibSequenceConfigException e) {
			throw new LibSequenceRunException(null, 0, LSRERR_NOT_FOUND, null);
		}
	}


	public void cancelSequence(LibSequenceRunningSequence runningSequence) throws LibSequenceRunException {
		orchestrator.getRunManager().cancel(runningSequence);
	}
	

	public int cancelSequenceByName(LibSequenceCallback callback, String sequenceName) {
		return orchestrator.getRunManager().cancelByName(callback, sequenceName);
	}
	

	public Set<LibSequenceRunningSequence> findRunningSequences(LibSequenceCallback callback) {
		return orchestrator.getRunManager().findRunningSequences(callback);
	}


	// ConfigManager Interface
	public void loadSection(LibSequenceCallback callback) throws LibSequenceConfigException {
		orchestrator.getConfigManager().loadSection(callback);
	}
	

	public void removeSection(LibSequenceCallback callback) throws LibSequenceConfigException {
		orchestrator.getConfigManager().removeSection(callback);
	}
	

	public String getSecurityToken (LibSequenceCallback callback, String sequenceName) throws LibSequenceConfigException {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().getOwnSequence(callback, sequenceName);
		return sequence.getSecurityToken(callback);
	}
	

	public boolean hasForeignSequence(String securityToken) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		return (sequence!=null);
	}
	

	public boolean hasOwnSequence(LibSequenceCallback callback, String sequenceName) {
		return orchestrator.getConfigManager().hasOwnSequence(callback, sequenceName);
	}
	

	public Set<String> getSequenceNames(LibSequenceCallback callback) throws LibSequenceConfigException {
		return orchestrator.getConfigManager().getSequenceNames(callback);
	}

	
	// PlaceholderManager
	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		orchestrator.getPlaceholderManager().registerPlaceholder(placeholder);
	}

	
	// CheckManager
	public void registerCheck(String checkName, LibSequenceCheck check) throws LibSequenceCheckException {
		orchestrator.getCheckManager().registerCheck(checkName, check);
	}

	
	// IncludeManager
	public void registerInclude(String includeName, LibSequenceInclude include) throws LibSequenceIncludeException {
		orchestrator.getIncludeManager().registerInclude(includeName, include);
	}

	
	// ChainManager
	public void registerChain(LibSequenceChain chain) {
		orchestrator.getChainManager().registerChain(chain);
	}
	

	// Initialization
	protected LibSequenceOrchestrator createOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) throws LibSequenceException {
		return new LibSequenceOrchestrator(plugin, startOptions);
	}
	
}
