package de.polarwolf.libsequence.api;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

import java.util.Set;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceAction;
import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.chains.LibSequenceChain;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.orchestrator.LibSequenceStartOptions;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholder;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunResult;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceSequencer {

	protected final LibSequenceOrchestrator orchestrator;
	
	public LibSequenceSequencer(Plugin plugin, LibSequenceStartOptions startOptions) {
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
	public LibSequenceActionResult registerAction(String actionName, LibSequenceAction action) {
		return orchestrator.getActionManager().registerAction(actionName, action);		
	}
	
	protected LibSequenceActionValidator getActionValidator() {
		return orchestrator.getActionManager().getActionValidator();
	}

	
	// RunManager Interface
	public LibSequenceRunResult executeForeignSequence(LibSequenceCallback callback, String securityToken, LibSequenceRunOptions runOptions) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		if (sequence==null) {
			return new LibSequenceRunResult(null, null, LSRERR_NOT_FOUND, null);
		}
		return orchestrator.getRunManager().execute(callback, sequence, securityToken, runOptions);
	}
	
	public LibSequenceRunResult executeOwnSequence(LibSequenceCallback callback, String sequenceName, LibSequenceRunOptions runOptions) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findOwnSequence(callback, sequenceName);
		if (sequence==null) {
			return new LibSequenceRunResult(null, sequenceName, LSRERR_NOT_FOUND, null);
		}
		String securityToken = sequence.getSecurityToken(callback);
		return orchestrator.getRunManager().execute(callback, sequence, securityToken, runOptions);
	}

	public LibSequenceRunResult cancelSequence(LibSequenceRunningSequence runningSequence) {
		return orchestrator.getRunManager().cancel(runningSequence);
	}
	
	public LibSequenceRunResult cancelSequenceByName(LibSequenceCallback callback, String sequenceName) {
		return orchestrator.getRunManager().cancelByName(callback, sequenceName);
	}
	
	public Set<LibSequenceRunningSequence> queryRunningSequences(LibSequenceCallback callback) {
		return orchestrator.getRunManager().queryRunningSequences(callback);
	}


	// ConfigManager Interface
	public LibSequenceConfigResult loadSection(LibSequenceCallback callback) {
		return orchestrator.getConfigManager().loadSection(callback);
	}
	
	public LibSequenceConfigResult removeSection(LibSequenceCallback callback) {
		return orchestrator.getConfigManager().removeSection(callback);
	}
	
	public String getSecurityToken (LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findOwnSequence(callback, sequenceName);
		if (sequence != null) {
			return sequence.getSecurityToken(callback);
		}
		return null;
	}
	
	public boolean hasForeignSequence(String securityToken) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		return (sequence!=null);
	}
	
	public boolean hasOwnSequence(LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findOwnSequence(callback, sequenceName);
		return (sequence!=null);
	}
	
	public Set<String> getSequenceNames(LibSequenceCallback callback) {
		return orchestrator.getConfigManager().getSequenceNames (callback);
	}

	
	// PlaceholderManager
	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		orchestrator.getPlaceholderManager().registerPlaceholder(placeholder);
	}

	
	// ChainManager
	public void registerChain(LibSequenceChain chain) {
		orchestrator.getChainManager().registerChain(chain);
	}
	

	// Initialization
	protected LibSequenceOrchestrator createOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) {
		return new LibSequenceOrchestrator(plugin, startOptions);
	}
	
}

