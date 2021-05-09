package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckSendertype implements LibSequenceCheck {
	
	public static final String TYPE_CONSOLE = "console";
	public static final String TYPE_PLAYER = "player";

	@Override
	public String performCheck (String checkName, String valueText, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		// We cannot resolve placeholders here, because this is designed as a boundary check
		
		// Syntax validation
		if ((!valueText.equalsIgnoreCase(TYPE_CONSOLE)) && (!valueText.equalsIgnoreCase(TYPE_PLAYER))) {
			throw new LibSequenceCheckException(checkName, LSKERR_SYNTAX_ERROR, "unknown sendertype: " + valueText);
		}

		// Perform check
		if (valueText.equalsIgnoreCase(TYPE_CONSOLE) && ((initiator instanceof ConsoleCommandSender) || (initiator instanceof RemoteConsoleCommandSender))) {
			return "";
		}

		if (valueText.equalsIgnoreCase(TYPE_PLAYER)  && (initiator instanceof Player)) {
			return "";
		}

		if (initiator == null) {
			return "no initiator given";
		} else {
			return initiator.getClass().getName() + " is not sendertype: " + valueText;
		}
	}

}
