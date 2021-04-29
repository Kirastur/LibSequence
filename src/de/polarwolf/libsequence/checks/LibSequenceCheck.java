package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceCheck {
	
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence);

}
