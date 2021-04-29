package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.checks.LibSequenceCheckResult;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCheck  extends LibSequenceActionGeneric {
	
	public static final String KEYNAME_DENYMESSAGE = "denymessage";

	
    @Override
	public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null, null);
    }
    
	
	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {

		LibSequenceCheckResult checkResult = sequence.performChecks(configStep);
		if (checkResult.hasError()) {

			String sDenyMessage;
			CommandSender initiator = sequence.getRunOptions().getInitiator();
			if (initiator instanceof Player) {
				Player player = (Player)initiator;
				sDenyMessage = configStep.getValueLocalized(KEYNAME_DENYMESSAGE, player.getLocale());
			} else { 
				sDenyMessage = configStep.getValue(KEYNAME_DENYMESSAGE);
			}
			sDenyMessage = sequence.resolvePlaceholder(sDenyMessage);
			
			if (initiator != null) {
				initiator.sendMessage(sDenyMessage);
			}
			
			sequence.cancel();
			return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_CHECK_FAILED, null, checkResult);			
		}
		
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null, null);
	}

}
