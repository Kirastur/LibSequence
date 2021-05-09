package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionInfo extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_LEVEL = "level";

	public static final String LEVEL_INFO = "info";
	public static final String LEVEL_WARNING = "warning";


    @Override
    public Set<String> getRequiredAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_MESSAGE);
    	return myAttributes;
	}


    @Override
	public Set<String> getOptionalAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_LEVEL);
    	return myAttributes;
	}
    

	@Override
    public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException {
    	String levelType=configStep.findValue(KEYNAME_LEVEL);
   		if (!((levelType == null) || levelType.isEmpty() || levelType.equalsIgnoreCase(LEVEL_INFO) || levelType.equalsIgnoreCase(LEVEL_WARNING))) {  
       		throw new LibSequenceActionException(configStep.findActionName(), LSAERR_UNKNOWN_VALUE, KEYNAME_LEVEL + ": " + levelType);
    	}
    }
	
	
	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException {
		String messageText = configStep.findValue(KEYNAME_MESSAGE);
		messageText = sequence.resolvePlaceholder(messageText);

		String levelType = configStep.findValue(KEYNAME_LEVEL);
		
		if ((levelType != null) && (levelType.equalsIgnoreCase(LEVEL_WARNING))) {
			sequence.getPlugin().getLogger().warning(messageText);
		} else {
			sequence.getPlugin().getLogger().info(messageText);
		}
	}

}
