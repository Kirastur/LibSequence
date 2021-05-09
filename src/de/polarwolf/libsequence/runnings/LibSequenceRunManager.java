package de.polarwolf.libsequence.runnings;

//The tree is: RunManager ==> RunningSequence
//
//The RunManager is the controlling instance on all currently running sequences
//There is only one Manager running except you are using private sequencers
//It acts as an interface between the scheduler and the API

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.chains.LibSequenceChainManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.conditions.LibSequenceConditionManager;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderException;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

public class LibSequenceRunManager {
	
	public static final int MAX_RUNNING_SEQUENCES = 20;

	// To make a clean object destroy, we must avoid circular references.
	// Since the Orchestrator has a link to the RunManager-object
	// the RunManager is not allowed to store the Orchestrator itself.
	// Instead The RunManaer only saves the references to the loweron Managers.
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceConditionManager conditionManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceChainManager chainManager;
	
	
	protected final Set<LibSequenceRunningSequence> sequences = new HashSet<>();
	

	public LibSequenceRunManager(LibSequenceOrchestrator orchestrator) {
		this.placeholderManager = orchestrator.getPlaceholderManager();
		this.conditionManager = orchestrator.getConditionManager();
		this.checkManager = orchestrator.getCheckManager();
		this.includeManager = orchestrator.getIncludeManager();
		this.actionManager = orchestrator.getActionManager();
		this.chainManager = orchestrator.getChainManager();
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
	

	// Gateway to PlaceholderManager
	// PlaceholderManager takes care if messageTest is null
	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions) throws LibSequencePlaceholderException {
		return placeholderManager.resolvePlaceholder(messageText, runOptions);
	}
		

	// Gateway to ConditionManager
	public boolean resolveCondition(String conditionText,LibSequenceRunningSequence runningSequence) {
		return conditionManager.performConditions(conditionText, runningSequence);
	}
	

	// Gateway to CheckManager
	public boolean performChecks(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) throws LibSequenceCheckException {
		return checkManager.performChecks(runningSequence, configStep);
	}
	

	// Gateway to IncludeManager
	public Set<CommandSender> performIncludes(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) throws LibSequenceIncludeException {
		return includeManager.performIncludes(runningSequence, configStep);
	}
	

	// Start a sequence
	// For authorization you must provide the authorization-token
	// The new RunningSequence-Object is included in the Result-Object
	// We take care that the runOption object always exists
	public LibSequenceRunningSequence execute(LibSequenceCallback callback, LibSequenceConfigSequence configSequence, String securityToken, LibSequenceRunOptions runOptions) throws LibSequenceRunException {

		String sequenceName = configSequence.getSequenceName();
		
		// Create dummy runOptions if empty
		if (runOptions == null) {
			runOptions = new LibSequenceRunOptions();
		}

		// Is the number of maximal currently running sequences reached?
		if (getNumberOfRunningSequences() > getMaxRunningSequences()) {
			throw new LibSequenceRunException(sequenceName, 0, LSRERR_TOO_MANY, null);
		}

		// Is sequence a singleton and already another sequence running?
		if (runOptions.isSingleton() && isRunning(configSequence)) {
			throw new LibSequenceRunException(sequenceName, 0, LSRERR_SINGLETON_RUNNING, null);
		}

		// Do we have the correct security token to start the sequence?
		if (!configSequence.isValidSecurityToken(securityToken)) {
			throw new LibSequenceRunException(sequenceName, 0, LSRERR_NOT_AUTHORIZED, null);
		}

		try  {

			// Is the syntax of the sequence correct?
			configSequence.validateSyntax();

			// Are all actions authorized to run?
			actionManager.validateAuthorization(runOptions, configSequence);

			// OK, all checks are done, now lt's resolve the chains
			chainManager.resolveChain(runOptions);

			// Finally create the runningSequence-object
			return new LibSequenceRunningSequence(callback, this, configSequence, runOptions);

		} catch (LibSequenceException e) {
			throw new LibSequenceRunException(sequenceName, 0, e);
		} catch (Exception e) {
			throw new LibSequenceRunException(sequenceName, 0, LSRERR_JAVA_EXCEPTION, null, e);
		}

	}

	
	// Cancel a running sequence
	public void cancel(LibSequenceRunningSequence runningSequence) throws LibSequenceRunException {
		if ((runningSequence.isFinished()) || (!sequences.contains(runningSequence))) {
			throw new LibSequenceRunException(null, 0, LSRERR_NOT_RUNNING, runningSequence.getName());
		}
		runningSequence.cancel();
	}
	
	
	// Cancel all running sequences with a given name
	// This is an Admin-Function, so you will net to provide the callback-object for verification
	// A cancel can remove the sequence from the list, so we must use an iterator here
	public int cancelByName (LibSequenceCallback callback, String sequenceName) {
		int nrOfSequencesCancelled = 0;

		Iterator<LibSequenceRunningSequence> iter = sequences.iterator();
		while (iter.hasNext()) {
			LibSequenceRunningSequence runningSequence = iter.next();
			if ((runningSequence.getName().equals(sequenceName)) && 
				(runningSequence.hasAccess(callback)) &&
				(!runningSequence.isFinished())) {
				runningSequence.cancel();
				nrOfSequencesCancelled = nrOfSequencesCancelled +1;
			}
		}

		return nrOfSequencesCancelled;
	}
	

	public Set<LibSequenceRunningSequence> findRunningSequences(LibSequenceCallback callback) {
		HashSet<LibSequenceRunningSequence> result = new HashSet<>();
		for (LibSequenceRunningSequence sequence: sequences) {
			if ((sequence.hasAccess(callback)) && (!sequence.isFinished())) {
				result.add(sequence);
			}
		}
		return result;
	}
	

	protected void onInit(LibSequenceRunningSequence runningSequence) {
		sequences.add(runningSequence);
		actionManager.onInit(runningSequence);
	}
	

	protected void onCancel(LibSequenceRunningSequence runningSequence) {
		actionManager.onCancel(runningSequence);
		sequences.remove(runningSequence);
	}


	protected void onFinish(LibSequenceRunningSequence runningSequence) {
		actionManager.onFinish(runningSequence);
		sequences.remove(runningSequence);
	}


	protected void executeStep(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) throws LibSequenceRunException {
		String sequenceName = runningSequence.getName();
		int stepNr = runningSequence.getStepNr();
		
		try {
			actionManager.execute(runningSequence, configStep);
		} catch (LibSequenceActionException e) {
			throw new LibSequenceRunException(sequenceName, stepNr, e);
		} catch (Exception e) {
			throw new LibSequenceRunException(sequenceName, stepNr, LSRERR_JAVA_EXCEPTION, null, e);
		}
	}
	
}

