package de.polarwolf.libsequence.logger;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Sequence are running asynchron, this means you only start the sequence and
 * the sequence is running in the background while you can perform other tasks
 * (The sequence is still running in the Java Main Thread, so the sequence can
 * use all Minecraft API functions). But this also means, the initiator of the
 * sequence can get a feedback only for the successful start of the sequence,
 * but not the successful execution. The LibSequence does not print
 * errormessages itself. Instead messages created during execution are sent via
 * this logger. You can write your own logger or use the Default Logger (see
 * below) which writes the logmessages to the console.
 *
 */
public interface LibSequenceLogger {

	/**
	 * This procedure is called when an error occurs while a sequence executes. The
	 * the 3rd-party-plugin is responsible for handling the error, e.g. printing a
	 * message to the console.
	 *
	 * @param sequence Sequence where the error occurred
	 * @param e        Exception which was thrown
	 */
	public void onExecutionError(LibSequenceRunningSequence sequence, LibSequenceRunException e);

	/**
	 * This method is called if a check fails. Every time an action performs a
	 * "check" which fails, this function is called. In a production environment you
	 * can discard the info, but for testing it is sometimes helpful to know why a
	 * check has failed.
	 *
	 * @param sequence    Sequence where the error occurred
	 * @param checkName   The check which failed
	 * @param failMessage The reason why the check has failed
	 */
	public void onCheckFailed(LibSequenceRunningSequence sequence, String checkName, String failMessage);

	/**
	 * This method is called if it seems that a placeholder was not resolved. If the
	 * PlaceholderManager has the suspicion that not all placeholders are replaced
	 * (e.g. too many remaining "%"), it will call this function. In a production
	 * environment you can discard the info, but for testing it is sometimes helpful
	 * to know if a placeholder replacement may have failed.
	 *
	 * @param sequence      Sequence where the warning occurred
	 * @param attributeName Attribute where the warning occurred
	 * @param valueText     Attribute-Value from the configuration where the warning
	 *                      occurred
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
	 *      (WIKI)
	 */
	public void onPlaceholderWarn(LibSequenceRunningSequence sequence, String attributeName, String valueText);

	/**
	 * This is a notification message which is called every time a new sequence was
	 * started. The 3rd-party-plugin can e.g. write a debug message to the console.
	 *
	 * @param sequence Sequence which was started
	 */
	public void debugSequenceStarted(LibSequenceRunningSequence sequence);

	/**
	 * This is a notification message which is called every time a new step in
	 * sequence execution was started.
	 *
	 * @param sequence Running Sequence
	 * @param step     Step which was reached
	 */
	public void debugSequenceStepReached(LibSequenceRunningSequence sequence, LibSequenceConfigStep step);

	/**
	 * This is a notification message which is called every time a running sequence
	 * was interrupted. It is guaranteed that you receive either a Cancelled or
	 * Finished callback at the end of every sequence.
	 *
	 * @param sequence Affected sequence
	 */
	public void debugSequenceCancelled(LibSequenceRunningSequence sequence);

	/**
	 * This is a notification message which is called every time a running sequence
	 * has finished normally. It is guaranteed that you receive either a Cancelled
	 * or Finished callback at the end of every sequence.
	 *
	 * @param sequence Affected sequence
	 */
	public void debugSequenceFinished(LibSequenceRunningSequence sequence);

}
