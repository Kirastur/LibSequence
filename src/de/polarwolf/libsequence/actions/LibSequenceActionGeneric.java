package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public abstract class LibSequenceActionGeneric implements LibSequenceAction {

	@Override
	public void onInit(LibSequenceRunningSequence sequence) {		
	}

	@Override
    public void onCancel(LibSequenceRunningSequence sequence) {
    }
    
	@Override
    public void onFinish(LibSequenceRunningSequence sequence) {
    }
	
	
	// All out-of-the-box actions are public
	@Override
	public boolean isAuthorized(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep) {
		return true;
	}
	
}
