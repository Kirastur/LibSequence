package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionInfo extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_LEVEL = "level";

	public static final String LEVEL_INFO = "info";
	public static final String LEVEL_WARNING = "warning";


	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_MESSAGE);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_MESSAGE, null);
    	}

    	String levelType=configStep.getValue(KEYNAME_LEVEL);
   		if (!((levelType == null) || levelType.isEmpty() || levelType.equalsIgnoreCase(LEVEL_INFO) || levelType.equalsIgnoreCase(LEVEL_WARNING))) {  
       		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_UNKNOWN_VALUE, KEYNAME_LEVEL + ": " + levelType, null);
    	}

   		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null, null);
    }
	
	
	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String messageText = configStep.getValue(KEYNAME_MESSAGE);
		messageText = sequence.resolvePlaceholder(messageText);

		String levelType = configStep.getValue(KEYNAME_LEVEL);
		
		if ((levelType != null) && (levelType.equalsIgnoreCase(LEVEL_WARNING))) {
			sequence.getPlugin().getLogger().warning(messageText);
		} else {
			sequence.getPlugin().getLogger().info(messageText);
		}
		
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null, null);
	}

}
