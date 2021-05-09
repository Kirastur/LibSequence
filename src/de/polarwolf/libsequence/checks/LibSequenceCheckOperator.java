package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckOperator implements LibSequenceCheck {
	
	public static final String AUTOOP = "luckperms.autoop";

	@Override
	public String performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);

		if (!runningSequence.resolveCondition(valueText)) {
			return "Condition is not TRUE: " + valueText;
		}

		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			throw  new LibSequenceCheckException(checkName, LSKERR_NO_INITIATOR, null);			
		}
		
		// Option 1: Classical OP
		boolean isOP = initiator.isOp();
		
		// Option 2: LuckPerms autoop
		boolean isAutoOP = initiator.hasPermission(AUTOOP);
		
		if (isOP || isAutoOP) {
			return "";
		} else {
			return "Is not a server operator: " + initiator.getName();
		}
	}

}
