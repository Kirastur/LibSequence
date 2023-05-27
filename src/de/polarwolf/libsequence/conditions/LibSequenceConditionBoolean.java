package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * A condition is true if the condition-text is one of the words "yes" or
 * "true", and false if the condition-text is one of the words "no" or "false".
 *
 */
public class LibSequenceConditionBoolean implements LibSequenceCondition {

	@Override
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence) {
		if (conditionText.equalsIgnoreCase("yes") || conditionText.equalsIgnoreCase("true")) {
			return true;
		}

		if (conditionText.equalsIgnoreCase("no") || conditionText.equalsIgnoreCase("false")) {
			return false;
		}

		return null; // NOSONAR
	}

}
