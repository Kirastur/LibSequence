package de.polarwolf.libsequence.actions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCheck  extends LibSequenceActionGeneric {
	
	public static final String KEYNAME_DENYMESSAGE = "denymessage";

	
    @Override
	public boolean hasCheck() {
    	return true;
    }

    
    @Override
	public Set<String> getOptionalAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_DENYMESSAGE);
    	return myAttributes;
	}


	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException {

		// Important: Under no circumstances a sequence can continue if the check result is undefined
		// Therefore on an Exception during check we must cancel the sequence
		try {
			if (!sequence.performChecks(configStep)) {

				CommandSender initiator = sequence.getRunOptions().getInitiator();
				if (initiator != null) {
					String sDenyMessage = sequence.findValueLocalizedAndResolvePlaceholder(configStep, KEYNAME_DENYMESSAGE, initiator);
					initiator.sendMessage(sDenyMessage);
				}
			
				sequence.cancel();
			}
		} catch (Exception e) {
			sequence.cancel();
			throw e;
		}
	}

}
