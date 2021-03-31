package de.polarwolf.libsequence.runnings;

import org.bukkit.command.CommandSender;

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
	protected final LibSequenceRunManager runManager;
	protected final LibSequenceConfigSequence configSequence;
	protected final CommandSender initiator;
	
	protected Boolean bCancel = false;
	protected Boolean bFinish = false;
	protected Integer step = 0;

	protected BukkitTask currentTask = null;

	public LibSequenceRunningSequence(LibSequenceCallback callback, LibSequenceRunManager runManager, LibSequenceConfigSequence configSequence, CommandSender initiator) {
		this.callback=callback;
		this.runManager=runManager;
		this.configSequence=configSequence;
		this.initiator=initiator;
		runManager.onInit(this);
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
	
	public CommandSender getInitiator() {
		return initiator;
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
				LibSequenceActionResult result = runManager.doExecute(this, configStep);
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
			runManager.onCancel(this);
			callback.debugSequenceCancelled(this);
		} else {
			runManager.onFinish(this);
			callback.debugSequenceFinished(this);
		}	
	}
	
}
