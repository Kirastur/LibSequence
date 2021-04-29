package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckCondition implements LibSequenceCheck {

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (runningSequence.resolveCondition(valueText)) {
    		return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		} else {
       		return new LibSequenceCheckResult(checkName, LSCERR_FALSE, valueText);   			
   		}
    }
}
