package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeAll  implements LibSequenceInclude {
	

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);

		Set<CommandSender> senders = new HashSet<>();
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) {
			senders.add(runningSequence.getPlugin().getServer().getConsoleSender());
			for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
				senders.add(player);
			}
		}
		
		return senders;
	}
	
}
