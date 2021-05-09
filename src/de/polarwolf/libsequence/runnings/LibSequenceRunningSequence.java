package de.polarwolf.libsequence.runnings;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

// The tree is: RunManager ==> RunningSequence
//
// A RunningSequence is a sequence which is currently executed

import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderException;

public class LibSequenceRunningSequence {
	
	public static final int TICKS_PER_SECOND = 20;
	
	protected final LibSequenceCallback callback;
	protected final LibSequenceConfigSequence configSequence;
	protected final LibSequenceRunOptions runOptions;
	
	protected final Plugin plugin;
	
	protected boolean bCancel = false;
	protected boolean bFinish = false;
	protected int step = 0;

	protected BukkitTask currentTask = null;
	
	// Be careful with RunManager here
	// Don't use section-based calls
	// You can't rely that the sections does still exist during execution

	// Not static because we must cleanup reference after sequence is done
	// to let java destroy the sequencer-object on dispose  
	private LibSequenceRunManager runManager;

	public LibSequenceRunningSequence(LibSequenceCallback callback, LibSequenceRunManager runManager, LibSequenceConfigSequence configSequence, LibSequenceRunOptions runOptions) {
		this.callback=callback;
		this.runManager=runManager;
		this.configSequence=configSequence;
		this.runOptions=runOptions;
		onInit();
		currentTask = createScheduledTask(1);
		// Bukkit runTaskLater return value is Nonnull
		plugin = currentTask.getOwner();			
	}
	

	public boolean isCancelled() {
		return bCancel;
	}
	

	public boolean isFinished() {
		return bFinish;
	}
	

	public int getStepNr() {
		return step;
	}
	

	public String getName() {
		return configSequence.getSequenceName();
	}
	

	// RunManager takes care that the RunManager object always exist
	public LibSequenceRunOptions getRunOptions() {
		return runOptions;
	}
	

	public Plugin getPlugin() {
		return plugin;
	}
	

	public final boolean hasAccess(LibSequenceCallback callbackToCheck) {
		return configSequence.hasAccess(callbackToCheck);
	}

	
	// Gateway to PlaceholderManager
	// RunManager takes care if the messageText is null
	public String resolvePlaceholder(String messageText) throws LibSequencePlaceholderException {
		return runManager.resolvePlaceholder(messageText, runOptions);
	}

	
	// GatewayManager to ConditionManager
	public boolean resolveCondition(String conditionText) {
		return runManager.resolveCondition(conditionText, this);
	}

	
	// Gateway to CheckManager
	public boolean performChecks(LibSequenceConfigStep configStep) throws LibSequenceCheckException {
		return runManager.performChecks(this, configStep);
	}

	
	// Gateway to IncludeManager
	public Set<CommandSender> performIncludes(LibSequenceConfigStep configStep) throws LibSequenceIncludeException {
		return runManager.performIncludes(this, configStep);
	}

	
	// Report failed check to Callback
	public void onCheckFailed(String checkName, String failMessage) {
		callback.onCheckFailed(this, checkName, failMessage);
	}


	// Report exception to Callback
	protected void onExecutionError(LibSequenceRunException e) {
		callback.onExecutionError(this, e);
	}

	
	protected void executeStep(LibSequenceConfigStep configStep) throws LibSequenceRunException {
		runManager.executeStep(this, configStep);
	}

	
	protected void onInit() {
		runManager.onInit(this);	
		callback.debugSequenceStarted(this);
	}
	

	protected void onCancel() {
		callback.debugSequenceCancelled(this);
		runManager.onCancel(this);	
	}


	protected void onFinish() {
		callback.debugSequenceFinished(this);
		runManager.onFinish(this);	
	}
	

	protected BukkitTask createScheduledTask(int wait) {
		SingleStepTask task = new SingleStepTask(this);
		return callback.scheduleTask(task, wait);
	}
	

	// A cancel must always be possible, so no return value here
	public void cancel() {
		if (bFinish) {
			return;
		}
		bCancel= true;
		
		// Yes, this is useless because if the task does not exists
		// the cancel() creates an exception which is handled by the "finally",
		// but I don't like that way
		if (currentTask==null) {
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


	protected void handleNextStep() {

		// Cleanup the reference to the Bukkit runTasLater object
		currentTask=null;

		// check if sequence is cancelled during sleep
		if (isCancelled() || isFinished()) {
			handleEndOfSequence();
			return;
		}

		// Check if end of sequence reached
		step = step +1;
		if (step > configSequence.getSize()) {
			handleEndOfSequence();
			return;
		}
			
		// Execute action
		LibSequenceConfigStep configStep = configSequence.getStep(step);
		callback.debugSequenceStepReached(this, configStep);
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
		if (wait==0) {
			wait=1;
		} else {
			wait=wait * TICKS_PER_SECOND;
		}
		currentTask = createScheduledTask(wait);
	}
	

	protected void handleEndOfSequence() {
		if (bFinish) {
			return;
		}
		bFinish=true;
		if (bCancel) {
			onCancel();
		} else {
			onFinish();
		}
	}
	
}
