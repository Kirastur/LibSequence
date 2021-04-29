package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceConditionBoolean implements LibSequenceCondition {


	@Override
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence) {
    	if (conditionText.equalsIgnoreCase("yes") || conditionText.equalsIgnoreCase("true")) {
    		return true;
    	}

    	if (conditionText.equalsIgnoreCase("no") || conditionText.equalsIgnoreCase("false")) {
       		return false;
       	}

    	return null;
	}

}
