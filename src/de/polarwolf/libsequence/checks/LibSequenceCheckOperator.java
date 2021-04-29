package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckOperator implements LibSequenceCheck {
	
	public static final String AUTOOP = "luckperms.autoop";

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceCheckResult(checkName, LSCERR_VALUE_MISSING, null);
		}
		
		if (!runningSequence.resolveCondition(valueText)) {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, " condition is not true");
		}

		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, "no initiator given");			
		}
		
		// Option 1: Classical OP
		boolean isOP = initiator.isOp();
		
		// Option 2: LuckPerms autoop
		boolean isAutoOP = initiator.hasPermission(AUTOOP);
		
		if (isOP || isAutoOP) {
			return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		} else {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, initiator.getName() + " is not a server operator");
		}
	}

}
