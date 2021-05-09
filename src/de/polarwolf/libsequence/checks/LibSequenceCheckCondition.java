package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckCondition implements LibSequenceCheck {

	@Override
	public String performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (runningSequence.resolveCondition(valueText)) {
			return "";
		} else {
			return "Does not resolve to TRUE: "+ valueText;
		}
    }

}
