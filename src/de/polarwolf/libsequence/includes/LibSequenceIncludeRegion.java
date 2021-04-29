package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationWorldguard;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeRegion  implements LibSequenceInclude {
	
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	

	public LibSequenceIncludeRegion(LibSequenceIntegrationWorldguard integrationWorldguard) {
		this.integrationWorldguard = integrationWorldguard;
	}


	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
	
	    	int resultWG = integrationWorldguard.testPlayer(player, valueText);
	    	switch(resultWG) {
	    		case LibSequenceIntegrationWorldguard.ERR_OK: break;	
	    		case LibSequenceIntegrationWorldguard.ERR_PLAYEROUTSIDE: break;
	    		case LibSequenceIntegrationWorldguard.ERR_GENERIC: return new LibSequenceIncludeResult(null, includeName, LSIERR_USER_DEFINED_ERROR, "generic region error");
	    		case LibSequenceIntegrationWorldguard.ERR_NOWORLD: return new LibSequenceIncludeResult(null, includeName, LSIERR_USER_DEFINED_ERROR, "world not found");
	    		case LibSequenceIntegrationWorldguard.ERR_NOREGION: return new LibSequenceIncludeResult(null, includeName, LSIERR_USER_DEFINED_ERROR, "region not found: " + valueText);
	    		default: return new LibSequenceIncludeResult(null, includeName, LSIERR_USER_DEFINED_ERROR, "unknown region error");
	    	}
	    	
			// Now it gets tricky: ^ is the XOR operator, this is not math square
			if ((resultWG == LibSequenceIntegrationWorldguard.ERR_OK) ^ inverseSearch) {
				senders.add(player);
			}
		}
		
		return new LibSequenceIncludeResult(senders,  includeName, LSIERR_OK, null);
	}

}
