package de.polarwolf.libsequence.checks;

import org.bukkit.command.CommandSender;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

public class LibSequenceCheckPermission implements LibSequenceCheck {

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceCheckResult(checkName, LSCERR_VALUE_MISSING, null);
		}

		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, "no initiator given");
		}

		if (initiator.hasPermission(valueText)) {
			return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		} else {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, initiator.getName() + " does not have " + valueText);
		}
	}

}
