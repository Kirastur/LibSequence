package de.polarwolf.libsequence.api;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

import java.util.Set;

import de.polarwolf.libsequence.actions.LibSequenceAction;
import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.runnings.LibSequenceRunManager;
import de.polarwolf.libsequence.runnings.LibSequenceRunResult;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceSequencer {

	protected final LibSequenceConfigManager configManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceRunManager runManager;
	
	public LibSequenceSequencer() {
		actionManager = createActionManager();
		configManager = createConfigManager();
		runManager = createRunManager();
	}
	
	// ActionManager Interface
	public LibSequenceActionResult registerAction(String actionName, LibSequenceAction action) {
		return actionManager.registerAction(actionName, action);		
	}
	
	protected LibSequenceActionValidator getActionValidator() {
		return actionManager.actionValidator;
	}
	
	// RunManager Interface
	public LibSequenceRunResult executeForeignSequence(LibSequenceCallback callback, String securityToken) {
		LibSequenceConfigSequence sequence = configManager.findForeignSequence(securityToken);
		if (sequence==null) {
			return new LibSequenceRunResult(null, null, LSRERR_NOT_FOUND, null);
		}
		return runManager.execute(callback, sequence, securityToken);
	}
	
	public LibSequenceRunResult executeOwnSequence(LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSequence sequence = configManager.findOwnSequence(callback, sequenceName);
		if (sequence==null) {
			return new LibSequenceRunResult(null, sequenceName, LSRERR_NOT_FOUND, null);
		}
		String securityToken = sequence.getSecurityToken(callback);
		return runManager.execute(callback, sequence, securityToken);
	}

	public LibSequenceRunResult cancelSequence(LibSequenceRunningSequence runningSequence) {
		return runManager.cancel(runningSequence);
	}
	
	public LibSequenceRunResult cancelSequenceByName(LibSequenceCallback callback, String sequenceName) {
		return runManager.cancelByName(callback, sequenceName);
	}
	
	public Set<LibSequenceRunningSequence> queryRunningSequences(LibSequenceCallback callback) {
		return runManager.queryRunningSequences(callback);
	}

	// ConfigManager Interface
	public LibSequenceConfigResult addSection(LibSequenceCallback callback) {
		return configManager.addSection(callback);
	}
	
	public LibSequenceConfigResult reloadSection(LibSequenceCallback callback) {
		return configManager.reloadSection(callback);
	}
	
	public LibSequenceConfigResult removeSection(LibSequenceCallback callback) {
		return configManager.removeSection(callback);
	}
	
	public String getSecurityToken (LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSequence sequence = configManager.findOwnSequence(callback, sequenceName);
		return sequence.getSecurityToken(callback);
	}
	
	public Boolean hasForeignSequence(String securityToken) {
		LibSequenceConfigSequence sequence = configManager.findForeignSequence(securityToken);
			return (sequence!=null);
	}
	
	public Boolean hasOwnSequence(LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSequence sequence = configManager.findOwnSequence(callback, sequenceName);
			return (sequence!=null);
	}
	
	public Set<String> getSequenceNames(LibSequenceCallback callback) {
		return configManager.getSequenceNames (callback);
	}
	
	// Initialization
	// Override this if you want to use custom Managers
	protected LibSequenceActionManager createActionManager() {
		return new LibSequenceActionManager();
	}

	protected LibSequenceConfigManager createConfigManager() {
		return new LibSequenceConfigManager(getActionValidator());
	}
	
	protected LibSequenceRunManager createRunManager() {
		return new LibSequenceRunManager (actionManager, configManager);
	}

}

