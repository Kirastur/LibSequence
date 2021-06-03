package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeInitiator  implements LibSequenceInclude {

	
	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);

		Set<CommandSender> senders = new HashSet<>();
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return senders;
		}
		
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) { 
			senders.add(initiator);
		}
		
		return senders;
	}

}
