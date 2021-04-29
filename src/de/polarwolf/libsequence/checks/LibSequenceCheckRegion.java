package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationWorldguard;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckRegion implements LibSequenceCheck {
	
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	
	public LibSequenceCheckRegion(LibSequenceIntegrationWorldguard integrationWorldguard) {
		this.integrationWorldguard = integrationWorldguard;
	}

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceCheckResult(checkName, LSCERR_VALUE_MISSING, null);
		}
		
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return new LibSequenceCheckResult(checkName, LSCERR_NOT_A_PLAYER, null);			
		}
		if (!(initiator instanceof Player)) {
			return new LibSequenceCheckResult(checkName, LSCERR_NOT_A_PLAYER, initiator.getName());			
		}
		Player player = (Player)initiator;

    	int resultWG = integrationWorldguard.testPlayer(player, valueText);
    	switch(resultWG) {
    		case LibSequenceIntegrationWorldguard.ERR_OK: return new LibSequenceCheckResult(checkName, LSCERR_OK, null);	
    		case LibSequenceIntegrationWorldguard.ERR_PLAYEROUTSIDE: return new LibSequenceCheckResult(checkName, LSCERR_FALSE, player.getName() + " is outside of " + valueText);
    		case LibSequenceIntegrationWorldguard.ERR_GENERIC: return new LibSequenceCheckResult(checkName, LSCERR_USER_DEFINED_ERROR, "generic region error");
    		case LibSequenceIntegrationWorldguard.ERR_NOWORLD: return new LibSequenceCheckResult(checkName, LSCERR_USER_DEFINED_ERROR, "world not found");
    		case LibSequenceIntegrationWorldguard.ERR_NOREGION: return new LibSequenceCheckResult(checkName, LSCERR_USER_DEFINED_ERROR, "region not found");
    		default: return new LibSequenceCheckResult(checkName, LSCERR_USER_DEFINED_ERROR, "unknown region error");
    	}
	}

}
