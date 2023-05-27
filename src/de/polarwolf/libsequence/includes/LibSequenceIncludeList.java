package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Include all players from the given list. The player-names must be separated
 * by ","or ";" or " ". If the list is empty after placeholder-replacement, no
 * player is added by this rule. The inverse operator "!" inverts the complete
 * list (all online players not on the list are added).
 *
 */
public class LibSequenceIncludeList implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);

		// an empty list is not an error, it just means nothing
		Set<CommandSender> senders = new HashSet<>();
		if (valueText.isEmpty()) {
			return senders;
		}

		valueText = valueText.replace(';', ' ');
		valueText = valueText.replace(',', ' ');
		valueText = valueText.replace("  ", " ");
		String[] playerNames = valueText.split(" ");
		for (String playerName : playerNames) {
			if (!playerName.isEmpty()) {
				Player player = Bukkit.getPlayer(playerName);
				if (player != null) {
					senders.add(player);
				}
			}
		}

		if (inverseSearch) {
			Set<CommandSender> inverseSenders = new HashSet<>();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!senders.contains(player)) {
					inverseSenders.add(player);
				}
			}
			return inverseSenders;
		} else {
			return senders;
		}
	}

}
