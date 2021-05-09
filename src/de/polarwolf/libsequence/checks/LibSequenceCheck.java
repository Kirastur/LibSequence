package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceCheck {
	
	public String performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) throws LibSequenceException;

}
