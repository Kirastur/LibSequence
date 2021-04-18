package de.polarwolf.libsequence.runnings;

//The tree is: RunManager ==> RunningSequence
//
//The RunManager is the controlling instance on all currently running sequences
//There is only one Manager running except you are using private sequencers
//It acts as an interface between the scheduler and the API

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

public class LibSequenceRunManager {
	
	public static final int MAX_RUNNING_SEQUENCES = 20;

	protected final LibSequenceOrchestrator orchestrator;
	
	protected final Set<LibSequenceRunningSequence> sequences = new HashSet<>();
	
	public LibSequenceRunManager(LibSequenceOrchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	public int getMaxRunningSequences() {
		return MAX_RUNNING_SEQUENCES;
	}
	
	public int getNumberOfRunningSequences() {
		int i=0;
		for (LibSequenceRunningSequence sequence : sequences) {
			if (!sequence.isFinished()) {
				i = i +1;
			}
		}
		return i;
	}
	
	public boolean isRunning(LibSequenceConfigSequence configSequence) {
		for (LibSequenceRunningSequence sequence : sequences) {
			if ((sequence.configSequence==configSequence) && (!sequence.isFinished())) {
				return true;
			}
		}
		return false;
	}
	
	// PlaceholderManager takes care if messageTest is null
	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions) {
		return orchestrator.getPlaceholderManager().resolvePlaceholder(messageText, runOptions);
	}
	
	
	// Start a sequence
	// For authorization you must provide the authorization-token
	// The new RunningSequence-Object is included in the Result-Object
	// We take care that the runOption object always exists
	public LibSequenceRunResult execute(LibSequenceCallback callback, LibSequenceConfigSequence configSequence, String securityToken, LibSequenceRunOptions runOptions) {
		if (runOptions == null) {
			runOptions = new LibSequenceRunOptions();
		}
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
		LibSequenceActionResult actionResult = orchestrator.getActionManager().checkAuthorization(runOptions, configSequence);
		if (actionResult.hasError()) {
			return new LibSequenceRunResult(null, configSequence.getSequenceName(), LSRERR_ACTION_AUTH_FAILED, null);
		}
		if (runOptions.isSingleton() && isRunning(configSequence)) {
			return new LibSequenceRunResult(null, configSequence.getSequenceName(), LSRERR_SINGLETON_RUNNING, null);
		}
		orchestrator.getChainManager().resolveChain(runOptions);
		LibSequenceRunningSequence runningSequence = new LibSequenceRunningSequence(callback, this, configSequence, runOptions);
		sequences.add(runningSequence);
		return new LibSequenceRunResult(runningSequence, configSequence.getSequenceName(), LSRERR_OK, null);
	}
	
	// Cancel a running sequence
	public LibSequenceRunResult cancel(LibSequenceRunningSequence runningSequence) {
		if ((runningSequence.isFinished()) || (!sequences.contains(runningSequence))) {
			return new LibSequenceRunResult(null, null, LSRERR_NOT_RUNNING, null);
		}
		runningSequence.cancel();
		return new LibSequenceRunResult(runningSequence, runningSequence.getName(), LSRERR_OK, null);
	}
	
	// Cancel all running sequences with a given name
	// This is an Admin-Function, so you will net to provide the callback-object for verification
	// A cancel can remove the sequence from the list, so we must use an iterator here
	public LibSequenceRunResult cancelByName (LibSequenceCallback callback, String sequenceName) {
		int nrOfSequencesCancelled = 0;
		Iterator<LibSequenceRunningSequence> iter = sequences.iterator();
		while (iter.hasNext()) {
			LibSequenceRunningSequence runningSequence = iter.next();
			if ((runningSequence.getName().equalsIgnoreCase(sequenceName)) && 
				(runningSequence.verifyAccess(callback)) &&
				(!runningSequence.isFinished())) {
				runningSequence.cancel();
				nrOfSequencesCancelled = nrOfSequencesCancelled +1;
			}
		}
		if (nrOfSequencesCancelled == 0) {
			return new LibSequenceRunResult(null, sequenceName, LSRERR_NOT_FOUND, null);			
		}
		return new LibSequenceRunResult(null, sequenceName, LSRERR_OK, null);
	}
	
	public Set<LibSequenceRunningSequence> queryRunningSequences(LibSequenceCallback callback) {
		HashSet<LibSequenceRunningSequence> result = new HashSet<>();
		for (LibSequenceRunningSequence sequence: sequences) {
			if ((sequence.verifyAccess(callback)) && (!sequence.isFinished())) {
				result.add(sequence);
			}
		}
		return result;
	}
	
	protected void onInit(LibSequenceRunningSequence runningSequence) {
		orchestrator.getActionManager().onInit(runningSequence);
	}
	
	protected void onCancel(LibSequenceRunningSequence runningSequence) {
		orchestrator.getActionManager().onCancel(runningSequence);
	}

	protected void onFinish(LibSequenceRunningSequence runningSequence) {
		orchestrator.getActionManager().onFinish(runningSequence);
	}

	protected LibSequenceActionResult doExecute(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) {
		return orchestrator.getActionManager().doExecute(runningSequence, configStep);
	}

	private void removeOldSequences() {
		int  c=0;
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

