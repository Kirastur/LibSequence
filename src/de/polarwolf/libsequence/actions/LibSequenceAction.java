package de.polarwolf.libsequence.actions;

import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceAction {
	
	public void onInit(LibSequenceRunningSequence sequence);
    public void onCancel(LibSequenceRunningSequence sequence);
    public void onFinish(LibSequenceRunningSequence sequence);
    
    public boolean skipAttributeVerification();
    public boolean hasInclude();
    public boolean hasCheck();
    public Set<String> getRequiredAttributes();
    public Set<String> getOptionalAttributes();
    
    public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException;
    public void validateAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep) throws LibSequenceException;
    
    public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException;

}
