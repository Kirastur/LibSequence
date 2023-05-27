package de.polarwolf.libsequence.conditions;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Manages conditions. Conditions are used if a boolean decision is needed
 * ("yes" or "no"), e.g. as part of an <I>include_*</I> or <I>check_* rule</I>.
 * You need conditions if you want do develop an action which has a special
 * attribute, and this attribute is working as a switch to enable or disable a
 * specific feature of the action.
 *
 * @see de.polarwolf.libsequence.conditions.LibSequenceCondition
 *      LibSequenceCondition
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Conditions">Conditions</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/ConditionManager">Condition
 *      Manager</A> (WIKI)
 */
public class LibSequenceConditionManager {

	protected List<LibSequenceCondition> conditionList = new ArrayList<>();

	public LibSequenceConditionManager(LibSequenceOrchestrator orchestrator) {
		// Prevent from starting the Manager without having an orchestrator
	}

	public void registerCondition(LibSequenceCondition condition) {
		conditionList.add(condition);
	}

	public boolean performConditions(String conditionText, LibSequenceRunningSequence runningSequence) {
		for (LibSequenceCondition myCondition : conditionList) {
			Boolean conditionResult = myCondition.performCondition(conditionText, runningSequence);
			if (conditionResult != null) {
				return conditionResult.booleanValue();
			}
		}
		return false;
	}

}
