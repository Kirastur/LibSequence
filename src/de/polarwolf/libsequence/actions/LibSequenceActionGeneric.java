package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.main.Main;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public abstract class LibSequenceActionGeneric implements LibSequenceAction {

	protected final Main main;
	
	protected LibSequenceActionGeneric(Main main) {
		this.main=main;
	}

	@Override
	public void onInit(LibSequenceRunningSequence sequence) {		
	}

	@Override
    public void onCancel(LibSequenceRunningSequence sequence) {
    }
    
	@Override
    public void onFinish(LibSequenceRunningSequence sequence) {
    }
    
}
