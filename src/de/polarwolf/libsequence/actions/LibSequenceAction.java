package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceAction {

	public void onInit(LibSequenceRunningSequence sequence);
    public void onCancel(LibSequenceRunningSequence sequence);
    public void onFinish(LibSequenceRunningSequence sequence);
    
    public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep);
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep);
}
