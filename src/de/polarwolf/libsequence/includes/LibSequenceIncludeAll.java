package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeAll  implements LibSequenceInclude {
	

	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) {
			senders.add(runningSequence.getPlugin().getServer().getConsoleSender());
			for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
				senders.add(player);
			}
		}
		
		return new LibSequenceIncludeResult(senders, includeName, LSIERR_OK, null);
	}
	
}
