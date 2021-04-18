package de.polarwolf.libsequence.actions;

import java.util.HashMap;
import java.util.Map;

import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

public class LibSequenceActionManager {

	protected final Map<String, LibSequenceAction> actionMap = new HashMap <>();
	protected final LibSequenceActionValidator actionValidator;
	
	public LibSequenceActionManager() {
		actionValidator = new LibSequenceActionValidator(this);
	}
	
	public boolean hasAction(String actionName) {
		for (String actionKey: actionMap.keySet()) {
			if (actionName.equals(actionKey)) {
				return true;
			}
		}
		return false;
	}
	
	public LibSequenceActionResult registerAction(String actionName, LibSequenceAction action) {
		if (hasAction(actionName)) {
			return new LibSequenceActionResult(null, actionName, LSAERR_ACTION_ALREADY_EXISTS, null);
		}
		actionMap.put(actionName, action);
		return new LibSequenceActionResult(null, actionName, LSAERR_OK, null);
	}
	
	public LibSequenceActionValidator getActionValidator() {
		return actionValidator;
	}

	public LibSequenceAction getActionByName (String actionName) {
		return actionMap.get(actionName);
	}
	
	public void onInit(LibSequenceRunningSequence sequence) {
		for (LibSequenceAction action : actionMap.values()) {
			action.onInit(sequence);
		}
	}
	
	public void onCancel(LibSequenceRunningSequence sequence) {
		for (LibSequenceAction action : actionMap.values()) {
			action.onCancel(sequence);
		}
	}

    public void onFinish(LibSequenceRunningSequence sequence) {
		for (LibSequenceAction action : actionMap.values()) {
			action.onFinish(sequence);
		}
	}
    
    // We expect to have a valid actionName here
    // This must be done in the configStep syntaxCheck before calling this
	// Check is done on Load and before sequence start 
    public LibSequenceActionResult validateAction(LibSequenceConfigStep configStep) {
    	LibSequenceAction action = getActionByName(configStep.getActionName());
    	if (action==null) {
			return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_ACTION_NOT_FOUND, null);
    	}
    	return action.checkSyntax(configStep);
	}
    
    public LibSequenceActionResult checkAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigSequence configSequence) {
    	for (int i=1; i<= configSequence.getSize(); i++) {
    		LibSequenceConfigStep configStep = configSequence.getStep(i);
        	LibSequenceAction action = getActionByName(configStep.getActionName());
        	if (action==null) {
    			return new LibSequenceActionResult(configSequence.getSequenceName(), configStep.getActionName(), LSAERR_ACTION_NOT_FOUND, null);
        	}
        	if (!action.isAuthorized(runOptions, configStep)) {
    			return new LibSequenceActionResult(configSequence.getSequenceName(), configStep.getActionName(), LSAERR_NOT_AUTHORIZED, null);
        	}
    		
    	}
    	return new LibSequenceActionResult(configSequence.getSequenceName(), null, LSAERR_OK, null);
   	
    }
    
	// First we must check if the sequence belongs to my instance
    // We expect a syntaxCheck() before, so we know the action is valid here
    // We expect authorization is done before, so we don't need to check here
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		LibSequenceAction action = getActionByName(configStep.getActionName());
		if (!configStep.verifyActionValidator(actionValidator)) {
			return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_WRONG_INSTANCE, null);
		}
		return action.doExecute(sequence, configStep);
	}
	
}

