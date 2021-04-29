package de.polarwolf.libsequence.conditions;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceCondition {
	
	// We use an object as result here
	// True: Condition is fulfilled
	// False: Condition has finally failed
	// null: indifferent, string does not belong to me
	public Boolean performCondition(String conditionText, LibSequenceRunningSequence runningSequence);

}
