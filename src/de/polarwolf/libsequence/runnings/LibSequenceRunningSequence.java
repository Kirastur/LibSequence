package de.polarwolf.libsequence.runnings;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// The tree is: RunManager ==> RunningSequence
//
// A RunningSequence is a sequence which is currently executed

import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderException;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Represents a sequence which is currently executed
 *
 */
public class LibSequenceRunningSequence {

	public static final int TICKS_PER_SECOND = 20;

	protected final LibSequenceToken runnerToken;
	protected final LibSequenceConfigSequence configSequence;
	protected final LibSequenceRunOptions runOptions;

	protected final Plugin plugin;

	protected boolean bCancel = false;
	protected boolean bFinish = false;
	protected int step = 0;

	// Not static because we must cleanup reference after sequence is done
	// to let java destroy the manager-objects on dispose
	protected LibSequenceRunHelper runHelper;

	protected BukkitTask currentTask = null;

	public LibSequenceRunningSequence(Plugin plugin, LibSequenceRunManager runManager, LibSequenceToken runnerToken,
			LibSequenceConfigSequence configSequence, LibSequenceRunOptions runOptions) {
		this.plugin = plugin;
		this.runHelper = runManager.acquireRunHelper(this);
		this.runnerToken = runnerToken;
		this.configSequence = configSequence;
		this.runOptions = runOptions;
		onInit();
		currentTask = createScheduledTask(1);
	}

	/**
	 * Flag if the sequence was cancelled
	 */
	public boolean isCancelled() {
		return bCancel;
	}

	/**
	 * Flag if the sequence was finished (either successful or cancelled)
	 */
	public boolean isFinished() {
		return bFinish;
	}

	/**
	 * Get the step number the execution is currently in
	 */
	public int getStepNr() {
		return step;
	}

	/**
	 * Get the name of the sequence as defined in the ConfigSequence
	 */
	public String getName() {
		return configSequence.getSequenceName();
	}

	/**
	 * Gets the runOption the sequence is started with. The RunManager takes care
	 * that the runOptions object always exist.
	 */
	//
	public LibSequenceRunOptions getRunOptions() {
		return runOptions;
	}

	/**
	 * Get the plugin. Needed for scheduling the wait task.
	 */
	protected Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Test if the given Token is the ownerToken of the ConfigSession
	 */
	public final boolean isOwner(LibSequenceToken ownerTokenToCheck) {
		return configSequence.isOwner(ownerTokenToCheck);
	}

	/**
	 * Test if the given token is the runnerToken with was set at sequence start.
	 */
	public final boolean isRunner(LibSequenceToken runnerTokenToCheck) {
		return runnerToken.equals(runnerTokenToCheck);
	}

	// Gateway to placeholder
	/**
	 * forward this request to the runHelper
	 */
	public String resolvePlaceholder(String attributeName, String messageText) throws LibSequencePlaceholderException {
		String resolvedText = runHelper.resolvePlaceholder(messageText, runOptions);
		if (runHelper.containsPlaceholder(resolvedText)) {
			runOptions.getLogger().onPlaceholderWarn(this, attributeName, resolvedText);
		}
		return resolvedText;
	}

	/**
	 * forward this request to the runHelper
	 */
	public String resolvePlaceholderForOtherPlayer(String messageText, Player player)
			throws LibSequencePlaceholderException {
		return runHelper.resolvePlaceholderForOtherPlayer(messageText, runOptions, player);
	}

	/**
	 * Resolve placeholders and respects the initiator's locale
	 */
	public String findValueLocalizedAndResolvePlaceholder(LibSequenceConfigStep configStep, String attributeName,
			CommandSender target) throws LibSequencePlaceholderException {
		String messageText;
		if (target instanceof Player player) {
			messageText = configStep.findValueLocalized(attributeName, player.getLocale());
		} else {
			messageText = configStep.findValue(attributeName);
		}
		return resolvePlaceholder(attributeName, messageText);
	}

	// GatewayManager to ConditionManager
	/**
	 * forward this request to the runHelper
	 */
	public boolean resolveCondition(String conditionText) {
		return runHelper.resolveCondition(conditionText, this);
	}

	// Gateway to CheckManager
	/**
	 * forward this request to the runHelper
	 */
	public boolean performChecks(LibSequenceConfigStep configStep) throws LibSequenceCheckException {
		return runHelper.performChecks(configStep);
	}

	// Gateway to IncludeManager
	/**
	 * forward this request to the runHelper
	 */
	public Set<CommandSender> performIncludes(LibSequenceConfigStep configStep) throws LibSequenceIncludeException {
		return runHelper.performIncludes(configStep);
	}

	/**
	 * Report a failed check to the logger
	 */
	public void onCheckFailed(String checkName, String failMessage) {
		runOptions.getLogger().onCheckFailed(this, checkName, failMessage);
	}

	/**
	 * Report an exception to the logger
	 */
	protected void onExecutionError(LibSequenceRunException e) {
		runOptions.getLogger().onExecutionError(this, e);
	}

	/**
	 * Executes the given sequence step
	 */
	protected void executeStep(LibSequenceConfigStep configStep) throws LibSequenceRunException {
		runHelper.executeStep(configStep);
	}

	/**
	 * Distribute the sequence-start notification
	 */
	protected void onInit() {
		runHelper.onInit();
		runOptions.getLogger().debugSequenceStarted(this);
	}

	/**
	 * Distribute the sequence-cancel notification
	 */
	protected void onCancel() {
		runOptions.getLogger().debugSequenceCancelled(this);
		runHelper.onCancel();
	}

	/**
	 * Distribute the sequence-fihish notification
	 */
	protected void onFinish() {
		runOptions.getLogger().debugSequenceFinished(this);
		runHelper.onFinish();
	}

	/**
	 * Start the Bukkit scheduler for the wait-step
	 */
	protected BukkitTask createScheduledTask(int wait) {
		SingleStepTask task = new SingleStepTask(this);
		return task.runTaskLater(plugin, wait);
	}

	/**
	 * Cancel the sequence execution. A cancel must always be possible, so no return
	 * value here.
	 */
	public void cancel() {
		if (bFinish) {
			return;
		}
		bCancel = true;

		// Yes, this is useless because if the task does not exists
		// the cancel() creates an exception which is handled by the "finally",
		// but I don't like that way
		if (currentTask == null) {
			handleEndOfSequence();
			return;
		}

		try {
			currentTask.cancel();
		} finally {
			currentTask = null;
			handleEndOfSequence();
		}
	}

	/**
	 * Handle the next sequence-step in sequence execution
	 */
	protected void handleNextStep() {

		// Cleanup the reference to the Bukkit runTasLater object
		currentTask = null;

		// check if sequence is cancelled during sleep
		if (isCancelled() || isFinished()) {
			handleEndOfSequence();
			return;
		}

		// Check if end of sequence reached
		step = step + 1;
		if (step > configSequence.getSize()) {
			handleEndOfSequence();
			return;
		}

		// Execute action
		LibSequenceConfigStep configStep = configSequence.getStep(step);
		runOptions.getLogger().debugSequenceStepReached(this, configStep);
		try {
			executeStep(configStep);
		} catch (LibSequenceRunException e) {
			onExecutionError(e);
		}
		// The show must go on, even on an error

		// Check if action has cancelled the sequence
		if (isCancelled()) {
			handleEndOfSequence();
			return;
		}

		// Prepare for next step
		int wait = configStep.getWait();
		if (wait == 0) {
			wait = 1;
		} else {
			wait = wait * TICKS_PER_SECOND;
		}
		currentTask = createScheduledTask(wait);
	}

	/**
	 * Cleanup the sequence after finish or cancel
	 */
	protected void handleEndOfSequence() {
		if (bFinish) {
			return;
		}
		bFinish = true;
		if (bCancel) {
			onCancel();
		} else {
			onFinish();
		}
		runHelper = null;
	}

}
