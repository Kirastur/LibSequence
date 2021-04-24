package de.polarwolf.libsequence.checks;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

public class LibSequenceCheckPermission implements LibSequenceCheck {

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, Plugin plugin, LibSequenceRunOptions runOptions) {
		if ((valueText == null) || (valueText.isEmpty())) {
			return new LibSequenceCheckResult(checkName, LSCERR_VALUE_MISSING, null);
		}

		CommandSender initiator = runOptions.getInitiator();
		if (initiator == null) {
			initiator = plugin.getServer().getConsoleSender();
		}

		if (initiator.hasPermission(valueText)) {
			return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		} else {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, initiator.getName() + " does not have " + valueText);
		}
	}

}
