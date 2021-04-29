package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeInitiator  implements LibSequenceInclude {

	
	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_USER_DEFINED_ERROR, "no initiator given");			
		}
		
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) { 
			senders.add(initiator);
		}
		
		return new LibSequenceIncludeResult(senders, includeName, LSIERR_OK, null);
	}

}
