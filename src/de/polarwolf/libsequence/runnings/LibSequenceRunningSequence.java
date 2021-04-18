package de.polarwolf.libsequence.runnings;

import org.bukkit.plugin.Plugin;

// The tree is: RunManager ==> RunningSequence
//
// A RunningSequence is a sequence which is currently executed

import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;

public class LibSequenceRunningSequence {

	
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
		runManagerOnInit();
		currentTask = createScheduledTask(1);
		// Bukkit runTaskLater return value is Nonnull
		plugin = currentTask.getOwner();			
		callback.debugSequenceStarted(this);
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
	
	public final boolean verifyAccess(LibSequenceCallback callbackToCheck) {
		return configSequence.verifyAccess(callbackToCheck);
	}

	// RunManager takes care if the messageText is null
	public String resolvePlaceholder(String messageText) {
		return runManager.resolvePlaceholder(messageText, runOptions);
	}
	
	protected LibSequenceActionResult executeStep(LibSequenceConfigStep configStep) {
		return runManager.doExecute(this, configStep);
	}
	
	protected void runManagerOnInit() {
		runManager.onInit(this);	
	}
	
	protected void runManagerOnCancel() {
		runManager.onCancel(this);	
	}

	protected void runManagerOnFinish() {
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
		
		if (currentTask==null) {
			handleEndOfSequence();
			return;
		}

		try {
			currentTask.cancel();
		} finally {
			handleEndOfSequence();
		}
	}

	protected void handleNextStep() {

		// check if sequence is cancelled during sleep
		currentTask=null;
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
		LibSequenceActionResult result = executeStep(configStep);
		if (result.hasError()) {
			callback.onExecutionError(this, result);
			// The show must go on, no cancel here
		}

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
			wait=wait*20;
		}
		currentTask = createScheduledTask(wait);
	}
	
	private void handleEndOfSequence() {
		if (bFinish) {
			return;
		}
		bFinish=true;
		if (bCancel) {
			runManagerOnCancel();
			callback.debugSequenceCancelled(this);
		} else {
			runManagerOnFinish();
			callback.debugSequenceFinished(this);
		}
		runManager = null;
	}
	
}
