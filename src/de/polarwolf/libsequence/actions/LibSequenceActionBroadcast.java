package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionBroadcast extends LibSequenceActionGeneric {
	
	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_PERMISSION = "permission";

	
	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_MESSAGE);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_MESSAGE, null);
    	}
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null, null);
    }


	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String messageText = configStep.getValue(KEYNAME_MESSAGE);
		messageText = sequence.resolvePlaceholder(messageText);
		
		String permission = configStep.getValue(KEYNAME_PERMISSION);
		permission = sequence.resolvePlaceholder(permission);
		
		if ((permission != null) && (!permission.isEmpty())) {
			sequence.getPlugin().getServer().broadcast(messageText, permission);			
		} else {
			sequence.getPlugin().getServer().broadcastMessage(messageText);
		}

		return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null, null);
	}
	
}
