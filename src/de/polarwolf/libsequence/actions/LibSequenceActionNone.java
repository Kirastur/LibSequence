package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Simply do nothing. This action can be used to comment out a specific step
 * (e.g. for testing), because for this action the syntax check is disabled.
 *
 */
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
