package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * This check tests if the value is TRUE (by testing for the strings "yes" or
 * "true") or is a numeric value greater or equal 1. This check is intended to
 * use in conjunction with placeholders.
 *
 */
public class LibSequenceCheckCondition implements LibSequenceCheck {

	@Override
	public String performCheck(String checkName, String valueText, LibSequenceRunningSequence runningSequence)
			throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(checkName, valueText);
		if (runningSequence.resolveCondition(valueText)) {
			return "";
		} else {
			return "Does not resolve to TRUE: " + valueText;
		}
	}

}
