package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * if the Condition is true (e.g. set to "yes"), all players are added. This is
 * useful, if you want to subtract specific players using the "-" operator.
 */
public class LibSequenceIncludeAll implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);

		Set<CommandSender> senders = new HashSet<>();
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) {
			senders.add(Bukkit.getConsoleSender());
			for (Player player : Bukkit.getOnlinePlayers()) {
				senders.add(player);
			}
		}

		return senders;
	}

}
