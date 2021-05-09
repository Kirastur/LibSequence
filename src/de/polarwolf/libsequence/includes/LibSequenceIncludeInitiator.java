package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeInitiator  implements LibSequenceInclude {

	
	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);

		Set<CommandSender> senders = new HashSet<>();
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			throw new LibSequenceIncludeException(includeName, LSIERR_NO_INITIATOR, null);			
		}
		
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) { 
			senders.add(initiator);
		}
		
		return senders;
	}

}
