package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.includes.LibSequenceIncludeResult;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionNotify extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";


	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_MESSAGE);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_MESSAGE, null);
    	}
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null, null);
    }
	

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		LibSequenceIncludeResult includeResult = sequence.performIncludes(configStep);
		
		if (includeResult.getSenders() != null) {
			for (CommandSender sender : includeResult.getSenders()) {
				String messageText = "";
				if (sender instanceof Player) {
					Player player = (Player)sender;
					messageText = configStep.getValueLocalized(KEYNAME_MESSAGE, player.getLocale());
				} else { 
					messageText = configStep.getValue(KEYNAME_MESSAGE);
				}
				messageText = sequence.resolvePlaceholder(messageText);
				sender.sendMessage(messageText);
			}
		}
		
		if (includeResult.hasError()) {
			includeResult.overwriteSenders(null);
	    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_USER_DEFINED_ERROR, "include failed", includeResult);
		}

    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null, null);
	}

}
