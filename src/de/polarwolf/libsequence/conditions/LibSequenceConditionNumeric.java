package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceConditionNumeric implements LibSequenceCondition{


	@Override
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence) {
    	try {
    		double d = Double.parseDouble(conditionText);
    		return (d >= 1);
    	} catch (Exception e) {
    		return null;
    	}
	}

}
