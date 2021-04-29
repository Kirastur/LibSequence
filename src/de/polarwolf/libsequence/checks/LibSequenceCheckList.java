package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckList  implements LibSequenceCheck {

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

		String[] playerNames = valueText.split(", ");
		for (String playerName : playerNames ) {
			Player player = runningSequence.getPlugin().getServer().getPlayer(playerName);
			if ((player != null) && (player == initiator)) {
				return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
			}
		}
		
		return new LibSequenceCheckResult(checkName, LSCERR_FALSE, initiator.getName() + " not included in list: " + valueText);
	}

}
