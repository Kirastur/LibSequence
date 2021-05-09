package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationWorldguard;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckRegion implements LibSequenceCheck {
	
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	
	public LibSequenceCheckRegion(LibSequenceIntegrationWorldguard integrationWorldguard) {
		this.integrationWorldguard = integrationWorldguard;
	}

	@Override
	public String performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			throw new LibSequenceCheckException(checkName, LSKERR_VALUE_MISSING, null);
		}
		
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			throw new LibSequenceCheckException(checkName, LSKERR_NO_INITIATOR, null);			
		}
		if (!(initiator instanceof Player)) {
			// ToDo: Initiator Class Name
			throw new LibSequenceCheckException(checkName, LSKERR_NOT_A_PLAYER, initiator.getName());			
		}
		Player player = (Player)initiator;

    	if  (integrationWorldguard.testPlayer(player, valueText)) {
    		return "";
    	} else {
    		return initiator.getName() + " is outside of: " + valueText;
    	}
	}

}
