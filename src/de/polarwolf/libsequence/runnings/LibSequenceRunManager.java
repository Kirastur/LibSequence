package de.polarwolf.libsequence.runnings;

//The tree is: RunManager ==> RunningSequence
//
//The RunManager is the controlling instance on all currently running sequences
//There is only one Manager running except you are using private sequencers
//It acts as an interface between the scheduler and the API

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigManager;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

public class LibSequenceRunManager {
	
	public static final Integer MAX_RUNNING_SEQUENCES = 20;

	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceConfigManager configManager;
	protected final Set<LibSequenceRunningSequence> sequences = new HashSet<>();
	
	public LibSequenceRunManager(LibSequenceActionManager actionManager, LibSequenceConfigManager configManager) {
		this.actionManager = actionManager;
		this.configManager = configManager;
	}
	
	public Integer getMaxRunningSequences() {
		return MAX_RUNNING_SEQUENCES;
	}
	
	public Integer getNumberOfRunningSequences() {
		Integer i=0;
		for (LibSequenceRunningSequence sequence : sequences) {
			if (!sequence.isFinished()) {
				i = i +1;
			}
		}
		return i;
	}
	
	// Start a sequence
	// For authorization you must provide the authorization-token
	// The new RunningSequence-Object is included in the Result-Object
	public LibSequenceRunResult execute(LibSequenceCallback callback, @Nonnull LibSequenceConfigSequence configSequence, String securityToken) {
		removeOldSequences();
		LibSequenceConfigResult result = configSequence.checkSyntax();
		if (result.hasError()) {
			return new LibSequenceRunResult(null, configSequence.getSequenceName(), LSRERR_CONFIG, result);
		}
		if (!configSequence.verifySecurityToken(securityToken)) {
			return new LibSequenceRunResult(null, configSequence.getSequenceName(), LSRERR_NOT_AUTHORIZED, null);
		}
		if (getNumberOfRunningSequences() > getMaxRunningSequences()) {
			return new LibSequenceRunResult(null, configSequence.getSequenceName(), LSRERR_TOO_MANY, null);
		}
		LibSequenceRunningSequence runningSequence = new LibSequenceRunningSequence(callback, this, configSequence);
		sequences.add(runningSequence);
		return new LibSequenceRunResult(runningSequence, configSequence.getSequenceName(), LSRERR_OK, null);
	}

	// Cancel a running sequence
	public LibSequenceRunResult cancel(LibSequenceRunningSequence runningSequence) {
		if (!(sequences.contains(runningSequence))) {
			return new LibSequenceRunResult(null, null, LSRERR_NOT_RUNNING, null);
		}
		runningSequence.cancel();
		return new LibSequenceRunResult(null, runningSequence.getName(), LSRERR_OK, null);
	}
	
	// Cancel all running sequences with a given name
	// This is an Admin-Function, so you will net to provide the callback-object for verification
	public LibSequenceRunResult cancelByName (LibSequenceCallback callback, String sequenceName) {
		Integer i = 0;
		for (LibSequenceRunningSequence runningSequence : sequences) {
			if ((runningSequence.getName().equalsIgnoreCase(sequenceName)) && 
				(runningSequence.configSequence.verifyAccess(callback)) &&
				(!cancel(runningSequence).hasError())) {
				i = i +1;
			}
		}
		if (i==0) {
			return new LibSequenceRunResult(null, sequenceName, LSRERR_NOT_FOUND, null);			
		}
		return new LibSequenceRunResult(null, sequenceName, LSRERR_OK, null);
	}
	
	public Set<LibSequenceRunningSequence> queryRunningSequences(LibSequenceCallback callback) {
		HashSet<LibSequenceRunningSequence> result = new HashSet<>();
		for (LibSequenceRunningSequence sequence: sequences) {
			if ((sequence.configSequence.verifyAccess(callback)) && (!sequence.isFinished())) {
				result.add(sequence);
			}
		}
		return result;
	}
	
	protected void onInit(LibSequenceRunningSequence runningSequence) {
		actionManager.onInit(runningSequence);
	}
	
	protected void onCancel(LibSequenceRunningSequence runningSequence) {
		actionManager.onCancel(runningSequence);
	}

	protected void onFinish(LibSequenceRunningSequence runningSequence) {
		actionManager.onFinish(runningSequence);
	}

	protected LibSequenceActionResult doExecute(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) {
		return actionManager.doExecute(runningSequence, configStep);
	}

	private void removeOldSequences() {
		Integer c=0;
		Iterator<LibSequenceRunningSequence> i = sequences.iterator();
		while (i.hasNext()) {
			LibSequenceRunningSequence sequence = i.next();
			if (sequence.isFinished()) {
				i.remove();
				c = c+1;
			}
		}
	}
	
}

