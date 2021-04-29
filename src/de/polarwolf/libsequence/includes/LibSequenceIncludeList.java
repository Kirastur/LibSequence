package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeList  implements LibSequenceInclude {

	
	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);

		// an empty list is not an error, it just means nothing
		Set<CommandSender> senders = new HashSet<>();
		if (valueText.isEmpty()) {
			return new LibSequenceIncludeResult(senders, includeName, LSIERR_OK, null);
		}

		String[] playerNames = valueText.split(", ");
		for (String playerName : playerNames ) {
			if (!playerName.isEmpty()) {
				Player player = runningSequence.getPlugin().getServer().getPlayer(playerName);
				if (player != null) {
					senders.add(player);
				}
			}
		}
		
		return new LibSequenceIncludeResult(senders, includeName, LSIERR_OK, null);
	}

}
