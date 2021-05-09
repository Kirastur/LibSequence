package de.polarwolf.libsequence.checks;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

public class LibSequenceCheckPermission implements LibSequenceCheck {

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

		if (initiator.hasPermission(valueText)) {
			return "";
		} else {
			return initiator.getName() + " does not have permission: " + valueText;
		}
	}

}
