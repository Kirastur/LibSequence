package de.polarwolf.libsequence.runnings;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_JAVA_EXCEPTION;

import org.bukkit.scheduler.BukkitRunnable;

// This class is supposed to be final
// Enter you custom task handling in RunningSequence.handleNextstep

/**
 * Bukkit wait
 *
 */
public final class SingleStepTask extends BukkitRunnable {

	private LibSequenceRunningSequence sequence;

	SingleStepTask(LibSequenceRunningSequence sequence) {
		this.sequence = sequence;
	}

	@Override
	public void run() {
		try {
			sequence.handleNextStep();
		} catch (Exception e) {
			LibSequenceRunException lse = new LibSequenceRunException(sequence.getName(), sequence.getStepNr(),
					LSRERR_JAVA_EXCEPTION, null, e);
			sequence.onExecutionError(lse);
			sequence.cancel();
		}
	}

}
