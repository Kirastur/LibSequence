package de.polarwolf.libsequence.runnings;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_DISABLED;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_JAVA_EXCEPTION;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_NOT_AUTHORIZED;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_NOT_RUNNING;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_SINGLETON_RUNNING;
import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_TOO_MANY;

import java.util.ArrayList;

//The tree is: RunManager ==> RunningSequence
//
//The RunManager is the controlling instance on all currently running sequences
//There is only one Manager running except you are using private sequencers
//It acts as an interface between the scheduler and the API

import java.util.List;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionManager;
import de.polarwolf.libsequence.chains.LibSequenceChainManager;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.conditions.LibSequenceConditionManager;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Manages sequence execution
 *
 * @see <A href= "https://github.com/Kirastur/LibSequence/wiki/RunManager">Run
 *      Manager</A> (WIKI)
 */
public class LibSequenceRunManager {

	// To make a clean object destroy, we must avoid circular references.
	// Since the Orchestrator has a link to the RunManager-object
	// the RunManager is not allowed to store the Orchestrator itself.
	// Instead The RunManaer only saves the references to the loweron Managers.
	protected final Plugin plugin;
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceConditionManager conditionManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	protected final LibSequenceActionManager actionManager;
	protected final LibSequenceChainManager chainManager;
	protected final int maxCurrentSequences;

	private boolean bDisabled = false;

	protected final List<LibSequenceRunningSequence> sequences = new ArrayList<>();

	public LibSequenceRunManager(LibSequenceOrchestrator orchestrator) {
		this.plugin = orchestrator.getPlugin();
		this.placeholderManager = orchestrator.getPlaceholderManager();
		this.conditionManager = orchestrator.getConditionManager();
		this.checkManager = orchestrator.getCheckManager();
		this.includeManager = orchestrator.getIncludeManager();
		this.actionManager = orchestrator.getActionManager();
		this.chainManager = orchestrator.getChainManager();
		this.maxCurrentSequences = orchestrator.getMaxCurrentSequences();
	}

	public final boolean isDisabled() {
		return bDisabled;
	}

	protected final void setDisabled() {
		bDisabled = true;
	}

	public int getMaxCurrentSequences() {
		return maxCurrentSequences;
	}

	public int getNumberOfRunningSequences() {
		int i = 0;
		for (LibSequenceRunningSequence sequence : sequences) {
			if (!sequence.isFinished()) {
				i = i + 1;
			}
		}
		return i;
	}

	public boolean isRunning(LibSequenceConfigSequence configSequence) {
		for (LibSequenceRunningSequence sequence : sequences) {
			if ((sequence.configSequence == configSequence) && (!sequence.isFinished())) {
				return true;
			}
		}
		return false;
	}

	// Create the RunningSequence
	// You can overwrite this if you have a derived object
	protected LibSequenceRunningSequence createRunningSequence(LibSequenceToken runnerToken,
			LibSequenceConfigSequence configSequence, LibSequenceRunOptions runOptions) {
		return new LibSequenceRunningSequence(plugin, this, runnerToken, configSequence, runOptions);
	}

	protected LibSequenceRunHelper acquireRunHelper(LibSequenceRunningSequence runningSequence) {
		return new LibSequenceRunHelper(runningSequence, placeholderManager, conditionManager, checkManager,
				includeManager, this);
	}

	// Start a sequence
	// For authorization you must provide the authorization-token
	// The new RunningSequence-Object is included in the Result-Object
	// We take care that the runOption object always exists
	public LibSequenceRunningSequence execute(LibSequenceToken runnerToken, LibSequenceConfigSequence configSequence,
			LibSequenceToken securityToken, LibSequenceRunOptions runOptions) throws LibSequenceRunException {

		String sequenceName = configSequence.getSequenceName();
		if (isDisabled()) {
			throw new LibSequenceRunException(sequenceName, 0, LSRERR_DISABLED, null);
		}

		// Create dummy runOptions if empty
		if (runOptions == null) {
			runOptions = new LibSequenceRunOptions();
		}

		// Is the number of maximal currently running sequences reached?
		if (getNumberOfRunningSequences() >= getMaxCurrentSequences()) {
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

		try {

			// Is the syntax of the sequence correct?
			configSequence.validateSyntax();

			// Are all actions authorized to run?
			actionManager.validateAuthorization(runOptions, configSequence);

			// OK, all checks are done, now let's resolve the chains
			chainManager.resolveChain(runOptions);

			// Finally create the runningSequence-object
			return createRunningSequence(runnerToken, configSequence, runOptions);

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
	// This is an Admin-Function, so you will net to provide the runnerToken for
	// verification.
	// A cancel can remove the sequence from the list, so we must be carefull here
	public int cancelByName(LibSequenceToken runnerToken, String sequenceName) {
		int nrOfSequencesCancelled = 0;
		for (LibSequenceRunningSequence myRunningSequence : new ArrayList<>(sequences)) {
			if ((myRunningSequence.getName().equals(sequenceName)) && (myRunningSequence.isRunner(runnerToken))
					&& (!myRunningSequence.isFinished())) {
				myRunningSequence.cancel();
				nrOfSequencesCancelled = nrOfSequencesCancelled + 1;
			}
		}
		return nrOfSequencesCancelled;
	}

	public List<LibSequenceRunningSequence> findRunningSequences(LibSequenceToken runnerToken) {
		List<LibSequenceRunningSequence> result = new ArrayList<>();
		for (LibSequenceRunningSequence mySequence : sequences) {
			if ((mySequence.isRunner(runnerToken)) && (!mySequence.isFinished())) {
				result.add(mySequence);
			}
		}
		return result;
	}

	public List<LibSequenceRunningSequence> sneakRunningSequencesOwnedByMe(LibSequenceToken ownerToken) {
		List<LibSequenceRunningSequence> result = new ArrayList<>();
		for (LibSequenceRunningSequence mySequence : sequences) {
			if ((mySequence.isOwner(ownerToken)) && (!mySequence.isFinished())) {
				result.add(mySequence);
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

	protected void executeStep(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep)
			throws LibSequenceRunException {
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

	public void disable() {
		setDisabled();
	}

}
