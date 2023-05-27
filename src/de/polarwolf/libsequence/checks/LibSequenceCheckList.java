package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.LSKERR_NOT_A_PLAYER;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Tests if the initiator is included in the given list of player-names. The
 * player-names must be separated by space, colon or semicolon.
 *
 */
public class LibSequenceCheckList implements LibSequenceCheck {

	@Override
	public String performCheck(String checkName, String valueText, LibSequenceRunningSequence runningSequence)
			throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(checkName, valueText);
		// Do not check for an empty valueText. It's allowed here

		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (!(initiator instanceof Player)) {
			throw new LibSequenceCheckException(checkName, LSKERR_NOT_A_PLAYER, null);
		}

		valueText = valueText.replace(';', ' ');
		valueText = valueText.replace(',', ' ');
		valueText = valueText.replace("  ", " ");
		String[] playerNames = valueText.split(" ");
		for (String playerName : playerNames) {
			Player player = Bukkit.getPlayer(playerName);
			if ((player != null) && (player == initiator)) {
				return "";
			}
		}

		return initiator.getName() + " not included in list: " + valueText;
	}

}
