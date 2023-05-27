package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * The condition is true, if the condition-text can be converted to a numeric
 * value (of type "double"), and the resulting value is equal or greater than 1.
 * The condition is false if the resulting value is lower than 1.
 *
 */
public class LibSequenceConditionNumeric implements LibSequenceCondition {

	@Override
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence) {
		try {
			double d = Double.parseDouble(conditionText);
			return (d >= 1);
		} catch (Exception e) {
			return null; // NOSONAR
		}
	}

}
