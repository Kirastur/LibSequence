package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationWorldguard;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeRegion  implements LibSequenceInclude {
	
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	

	public LibSequenceIncludeRegion(LibSequenceIntegrationWorldguard integrationWorldguard) {
		this.integrationWorldguard = integrationWorldguard;
	}


	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			throw new LibSequenceIncludeException(includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
	
	    	boolean isInside = integrationWorldguard.testPlayer(player, valueText);
	    	
			// Now it gets tricky: ^ is the XOR operator, this is not math square
			if (isInside ^ inverseSearch) {
				senders.add(player);
			}
		}
		
		return senders;
	}

}
