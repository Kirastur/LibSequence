package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeList  implements LibSequenceInclude {

	
	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);

		// an empty list is not an error, it just means nothing
		Set<CommandSender> senders = new HashSet<>();
		if (valueText.isEmpty()) {
			return senders;
		}

		String[] playerNames = valueText.split(", ");
		for (String playerName : playerNames) {
			if (!playerName.isEmpty()) {
				Player player = runningSequence.getPlugin().getServer().getPlayer(playerName);
				if (player != null) {
					senders.add(player);
				}
			}
		}
		
		if (inverseSearch) {
			Set<CommandSender> inverseSenders = new HashSet<>();
			for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
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
