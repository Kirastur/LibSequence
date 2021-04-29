package de.polarwolf.libsequence.conditions;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceConditionManager {
	
	protected List<LibSequenceCondition> conditionList = new ArrayList<>();
	

	public void registerCondition(LibSequenceCondition condition) {
		conditionList.add(condition);
	}
	

	public boolean performConditions (String conditionText, LibSequenceRunningSequence runningSequence) {
		for (LibSequenceCondition condition : conditionList) {
			Boolean conditionResult = condition.performCondition(conditionText, runningSequence);
			if (conditionResult != null) {
				return conditionResult.booleanValue();
			}
		}
		return false;
	}

}
