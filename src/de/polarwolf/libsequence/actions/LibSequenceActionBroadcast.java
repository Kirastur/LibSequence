package de.polarwolf.libsequence.actions;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionBroadcast extends LibSequenceActionGeneric {
	
	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_PERMISSION = "permission";
	
	
    @Override
    public Set<String> getRequiredAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_MESSAGE);
    	return myAttributes;
	}


    @Override
	public Set<String> getOptionalAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_PERMISSION);
    	return myAttributes;
	}
    

	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException {
		String messageText = configStep.findValue(KEYNAME_MESSAGE);
		messageText = sequence.resolvePlaceholder(messageText);
		
		String permission = configStep.findValue(KEYNAME_PERMISSION);
		permission = sequence.resolvePlaceholder(permission);
		
		if ((permission != null) && (!permission.isEmpty())) {
			sequence.getPlugin().getServer().broadcast(messageText, permission);			
		} else {
			sequence.getPlugin().getServer().broadcastMessage(messageText);
		}

	}
	
}
