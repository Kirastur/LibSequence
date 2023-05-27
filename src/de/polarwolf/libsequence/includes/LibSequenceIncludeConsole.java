package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * If the condition returns true, the server console ("CONSOLE") is added to
 * the list.
 */
public class LibSequenceIncludeConsole implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);

		Set<CommandSender> senders = new HashSet<>();
		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) {
			senders.add(Bukkit.getConsoleSender());
		}

		return senders;
	}

}
