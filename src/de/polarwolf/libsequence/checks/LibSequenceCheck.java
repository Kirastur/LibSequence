package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Checks are done by the predefined "check" action. The "check" action tests,
 * if the CommandSender (player) fulfills all of the given checks. If yes, the
 * sequence will continue as normal. If not, the sequence prints out a
 * deny-message to the CommandSender and terminates the sequence here.
 *
 * @see de.polarwolf.libsequence.checks.LibSequenceCheckManager CheckManager
 * @see <A href="https://github.com/Kirastur/LibSequence/wiki/Checks">Checks</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/CheckManager">Check
 *      Manager</A> (WIKI)
 *
 */
public interface LibSequenceCheck {

	/**
	 * Performs a check. The checkName is given because the object itself is not
	 * aware of the name he is registered with. The "valueText" is the string to
	 * test.<BR>
	 * If the check fails, the function should return a String describing the fail.
	 * If the check is OK, a NULL or an empty string must returned.<BR>
	 * The inverse operator "!" is fully managed by the checkManager so your custom
	 * check-rule does not need to care about it.
	 *
	 * @param checkName       Name of the check
	 * @param valueText       Text that should be checked
	 * @param runningSequence Affected sequence
	 * @return NULL or emptyString if check is OK otherwise an error text.
	 * @throws LibSequenceException
	 */
	public String performCheck(String checkName, String valueText, LibSequenceRunningSequence runningSequence)
			throws LibSequenceException;

}
