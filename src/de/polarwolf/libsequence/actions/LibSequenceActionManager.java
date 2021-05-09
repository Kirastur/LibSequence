package de.polarwolf.libsequence.actions;

import java.util.HashMap;
import java.util.Map;

import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;
import de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager;


import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

public class LibSequenceActionManager {

	protected final LibSequenceSyntaxManager syntaxManager;
	
	protected Map<String, LibSequenceAction> actionMap = new HashMap <>();
	
	
	public LibSequenceActionManager(LibSequenceSyntaxManager syntaxManager) {
		this.syntaxManager = syntaxManager;
	}
	

	public boolean hasAction(String actionName) {
		return actionMap.containsKey(actionName);
	}
	

	public void registerAction(String actionName, LibSequenceAction action) throws LibSequenceActionException {
		if (hasAction(actionName)) {
			throw new LibSequenceActionException(actionName, LSAERR_ACTION_ALREADY_EXISTS, null);
		}
		actionMap.put(actionName, action);
	}
	

	// We need to create a new ActionValidator every time
	// to avoid java circular references.
	// Compare them with actionValidator.isSameInstance
	public LibSequenceActionValidator getActionValidator() {
		return new LibSequenceActionValidator(this);
	}


	public LibSequenceAction getActionByName (String actionName) throws LibSequenceActionException {
		LibSequenceAction action = actionMap.get(actionName);
		if (action == null) {
			throw new LibSequenceActionException(null, LSAERR_ACTION_NOT_FOUND, actionName);						
		}
		return action;
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
    

    public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceActionException {
    	String actionName = configStep.findActionName();
    	LibSequenceAction action = getActionByName(actionName);
    	
    	try {
    		if (!action.skipAttributeVerification()) {
    			syntaxManager.performAttributeVerification(action, configStep);
    		}
    		action.validateSyntax(configStep);
    	} catch (LibSequenceActionException e) {
    		throw e;
    	} catch (LibSequenceException e) {
    		throw new LibSequenceActionException(actionName, e);
    	} catch (Exception e) {
    		throw new LibSequenceActionException(actionName, LSAERR_JAVA_EXCEPTION, null, e);
    	}
	}

 
    public void validateAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigSequence configSequence) throws LibSequenceActionException {
    	for (int i=1; i<= configSequence.getSize(); i++) {
    		String actionName = "";
        	try {
        		LibSequenceConfigStep configStep = configSequence.getStep(i);
        		actionName = configStep.findActionName();
        		LibSequenceAction action = getActionByName(actionName);
        		action.validateAuthorization(runOptions, configStep);
        	} catch (LibSequenceActionException e) {
        		throw e;
        	} catch (LibSequenceException e) {
        		throw new LibSequenceActionException(actionName, e);
        	} catch (Exception e) {
        		throw new LibSequenceActionException(actionName, LSAERR_JAVA_EXCEPTION, null, e);
        	}
    	}
    }
    

    // First we must check if the sequence belongs to my instance
    // We expect a syntaxCheck() before, so we know the action is valid here
    // We expect authorization is done before, so we don't need to check here
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceActionException {
		String actionName = configStep.findActionName();
		LibSequenceAction action = getActionByName(actionName);

		if (!configStep.isSameInstance(getActionValidator())) {
			throw new LibSequenceActionException(actionName, LSAERR_WRONG_INSTANCE, null);
		}
		
		try {
			action.execute(sequence, configStep);
    	} catch (LibSequenceActionException e) {
    		throw e;
    	} catch (LibSequenceException e) {
    		throw new LibSequenceActionException(actionName, e);
    	} catch (Exception e) {
    		throw new LibSequenceActionException(actionName, LSAERR_JAVA_EXCEPTION, null, e);
    	}
	}
	
}
