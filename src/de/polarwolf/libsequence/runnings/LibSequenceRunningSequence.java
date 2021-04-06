package de.polarwolf.libsequence.runnings;

import javax.annotation.Nonnull;

// The tree is: RunManager ==> RunningSequence
//
// A RunningSequence is a sequence which is currently executed

import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;

public class LibSequenceRunningSequence {

	private final LibSequenceRunManager runManager;
	
	protected final LibSequenceCallback callback;
	protected final LibSequenceConfigSequence configSequence;
	protected final LibSequenceRunOptions runOptions;
	
	protected Boolean bCancel = false;
	protected Boolean bFinish = false;
	protected Integer step = 0;

	protected BukkitTask currentTask = null;
	
	// Be careful with RunManager here
	// Don't use section-based calls
	// You can't rely that the sections does still exists during execution

	public LibSequenceRunningSequence(LibSequenceCallback callback, LibSequenceRunManager runManager, LibSequenceConfigSequence configSequence, @Nonnull LibSequenceRunOptions runOptions) {
		this.callback=callback;
		this.runManager=runManager;
		this.configSequence=configSequence;
		this.runOptions=runOptions;
		runManagerOnInit();
		currentTask = createScheduledTask(1);
		callback.debugSequenceStarted(this);
	}
	
	public Boolean isCancelled() {
		return bCancel;
	}
	
	public Boolean isFinished() {
		return bFinish;
	}
	
	public Integer getStepNr() {
		return step;
	}
	
	public String getName() {
		return configSequence.getSequenceName();
	}
	
	public LibSequenceRunOptions getRunOptions() {
		return runOptions;
	}
	
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
 	protected BukkitTask createScheduledTask(Integer wait) {
		SingleStepTask task = new SingleStepTask(this);
		return callback.scheduleTask(task, wait);
	}
	
	// A cancel must always return true
	public void cancel() {
		bCancel= true;
		if (currentTask!=null) {
			try {
			 currentTask.cancel();
			} finally {
				handleEndOfSequence();
			}
		} else {
		handleEndOfSequence();
		}
	}

	protected void handleNextStep() {
		currentTask=null;
		if (!isCancelled()) {
			step = step +1;
			if (step <= configSequence.getSize()) {
				LibSequenceConfigStep configStep = configSequence.getStep(step);
				callback.debugSequenceStepReached(this, configStep);
				LibSequenceActionResult result = executeStep(configStep);
				if (result.hasError()) {
					callback.onExecutionError(this, result);
				}
				Integer wait = configStep.getWait();
				if (wait==0) {
					wait=1;
				} else {
					wait=wait*20;
				}
				currentTask = createScheduledTask(wait);
				return;
			}
		}
		handleEndOfSequence();
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
	}
	
}
