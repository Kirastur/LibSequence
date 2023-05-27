package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Defines a condition. Conditions are used if a boolean decision is needed
 * ("yes" or "no"), e.g. as part of an <I>include_*</I> or <I>check_* rule</I>.
 * You need conditions if you want do develop an action which has a special
 * attribute, and this attribute is working as a switch to enable or disable a
 * specific feature of the action.
 *
 * @see de.polarwolf.libsequence.conditions.LibSequenceConditionManager
 *      ConditionManager
 * @see <A
 *      href="https://github.com/Kirastur/LibSequence/wiki/Conditions">Conditions</A>
 *      (WIKI)
 * @see <A
 *      href="https://github.com/Kirastur/LibSequence/wiki/ConditionManager">Condition
 *      Manager</A> (WIKI)
 */
public interface LibSequenceCondition {

	/**
	 * Parse the given String in the context of the given sequence, and evaluate it
	 * to a TRUE/FALSE result.
	 *
	 * @param conditionText   Text which should be checked
	 * @param runningSequence Affected sequence
	 * @return TRUE if the evaluation of the String matches the condition, FALSE if
	 *         not, or NULL if the result is indifferent.
	 */
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence);

}
