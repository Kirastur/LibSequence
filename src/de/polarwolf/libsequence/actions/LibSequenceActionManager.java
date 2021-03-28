package de.polarwolf.libsequence.actions;

import java.util.HashMap;
import java.util.Map;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

public class LibSequenceActionManager {

	protected final Map<String, LibSequenceAction> actionMap = new HashMap <>();
	public final LibSequenceActionValidator actionValidator;
	
	public LibSequenceActionManager() {
		actionValidator = new LibSequenceActionValidator(this);
	}
	
	public Boolean hasAction(String actionName) {
		for (String actionKey: actionMap.keySet()) {
			if (actionName.equals(actionKey)) {
				return true;
			}
		}
		return false;
	}
	
	public LibSequenceActionResult registerAction(String actionName, LibSequenceAction action) {
		if (hasAction(actionName)) {
			return new LibSequenceActionResult(actionName, LSAERR_ACTION_ALREADY_EXISTS, null);
		}
		actionMap.put(actionName, action);
		return new LibSequenceActionResult(actionName, LSAERR_OK, null);
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
    
	// First we must check if the  belongs to my instance
    // Then we  expect a syntaxCheck() before, so we know the action is valid    
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		if (!(configStep.verifyActionValidator(actionValidator))) {
			return 	new LibSequenceActionResult(configStep.getActionName(), LSAERR_WRONG_INSTANCE, sequence.getName());
		}
		return getActionByName(configStep.getActionName()).doExecute(sequence, configStep);
	}
	
    // We expect to have a valid actionName here
    // This must be done in the configStep syntaxCheck before calling this
    protected LibSequenceActionResult validateAction(LibSequenceConfigStep configStep) {
    	LibSequenceAction action = getActionByName(configStep.getActionName());
    	if (action==null) {
			return new LibSequenceActionResult(configStep.getActionName(), LSAERR_ACTION_NOT_FOUND, null);
    	}
    	LibSequenceActionResult result = action.checkSyntax(configStep);
		return new LibSequenceActionResult (configStep.getActionName(), result.errorCode, result.errorSubText);
	}
	
}

