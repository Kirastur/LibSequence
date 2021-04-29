package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckSendertype implements LibSequenceCheck {
	
	public static final String TYPE_CONSOLE = "console";
	public static final String TYPE_PLAYER = "player";

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) {
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		// We cannot resolve placeholders here, because this is designed as a boundary check
		// ToDo Syntax Check

		if (valueText.equalsIgnoreCase(TYPE_CONSOLE) && ((initiator instanceof ConsoleCommandSender) || (initiator instanceof RemoteConsoleCommandSender))) {
			return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		}

		if (valueText.equalsIgnoreCase(TYPE_PLAYER)  && (initiator instanceof Player)) {
			return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
		}

		if (initiator == null) {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, "no initiator given");
		} else {
			return new LibSequenceCheckResult(checkName, LSCERR_FALSE, initiator.getClass().getName());
		}
	}

}
