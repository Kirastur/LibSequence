package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.LSAERR_NOT_AUTHORIZED;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public abstract class LibSequenceActionGeneric implements LibSequenceAction {
	
	private final String authorizationKey;
	

	protected LibSequenceActionGeneric() {
		this.authorizationKey = null;
	}

	
	protected LibSequenceActionGeneric(String authorizationKey) {
		this.authorizationKey = authorizationKey;
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
	
	
	// Syntax Check is mostly done in SyntaxManager
	@Override
	public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException {
		// Valid per default
    }
	

    // Use runOption for authorization
	protected final void validateAuthorizationByKey(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep) throws LibSequenceActionException {
		if ((authorizationKey != null) && (!authorizationKey.isEmpty()) && (!runOptions.verifyAuthorizationKey(authorizationKey))) { 
			throw new LibSequenceActionException(configStep.findActionName(), LSAERR_NOT_AUTHORIZED, null);
		}
	}
    

	@Override
	public void validateAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep) throws LibSequenceException {
		validateAuthorizationByKey(runOptions, configStep);
	}
	
	
	// Settings for the Syntax Manager
	@Override
    public boolean skipAttributeVerification() {
		return false;
	}


	@Override
    public boolean hasInclude() {
		return false;
	}
	

	@Override
	public boolean hasCheck() {
    	return false;
    }
    

	@Override
    public Set<String> getRequiredAttributes() {
    	return new HashSet<>();
	}


	@Override
	public Set<String> getOptionalAttributes() {
		return new HashSet<>();
	}
    
}
