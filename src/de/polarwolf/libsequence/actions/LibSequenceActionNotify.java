package de.polarwolf.libsequence.actions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionNotify extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";


	@Override
    public boolean hasInclude() {
		return true;
	}
	

    @Override
    public Set<String> getRequiredAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_MESSAGE);
    	return myAttributes;
	}


	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException {

		Set<CommandSender> targets  = sequence.performIncludes(configStep);
		
		for (CommandSender target : targets) {
			String messageText = sequence.findValueLocalizedAndResolvePlaceholder(configStep, KEYNAME_MESSAGE, target);
			target.sendMessage(messageText);
		}
	}

}
