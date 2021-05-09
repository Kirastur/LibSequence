package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionNone extends LibSequenceActionGeneric {
	
	@Override
    public boolean skipAttributeVerification() {
		return true;
	}


	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		// Simply do nothing
	}

}
