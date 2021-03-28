package de.polarwolf.libsequence.runnings;

import org.bukkit.scheduler.BukkitRunnable;

// This class is supposed to be final
// Enter you custom task handling in RunningSequence.handleNextstep

public final class SingleStepTask extends BukkitRunnable {
	
	private LibSequenceRunningSequence sequence;

	SingleStepTask(LibSequenceRunningSequence sequence) {
		this.sequence=sequence;
	}
	
	@Override
	public void run() {
		sequence.handleNextStep();
	}

}
