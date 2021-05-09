package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludePermission implements LibSequenceInclude {


	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			throw new LibSequenceIncludeException(includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
			Boolean hasPermission = player.hasPermission(valueText);
			// 	Now it gets tricky: ^ is the XOR operator, this is not math square
			if (hasPermission ^ inverseSearch) {
				senders.add(player);
			}
		}
		
		return senders;
	}

}
