package de.polarwolf.libsequence.actions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

		Set<CommandSender> senders  = sequence.performIncludes(configStep);
		
		for (CommandSender sender : senders) {
			String messageText = "";
			if (sender instanceof Player) {
				Player player = (Player)sender;
				messageText = configStep.findValueLocalized(KEYNAME_MESSAGE, player.getLocale());
			} else { 
				messageText = configStep.findValue(KEYNAME_MESSAGE);
			}
			messageText = sequence.resolvePlaceholder(messageText);
			sender.sendMessage(messageText);
		}
	}

}
