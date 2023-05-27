package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * If the condition returns true, the initiator is added to the list. The
 * initiator is the player, item, block or console who started the sequence. If
 * the chain resolver is active, the initiator is the real player triggering the
 * sequence (e.g. Player => Button => CommandBlock => Sequence).
 *
 * You can just say "yes" here, or you can write a more sophisticated
 * condition including placeholders.
 */
public class LibSequenceIncludeInitiator implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);

		Set<CommandSender> senders = new HashSet<>();
		CommandSender initiator = runningSequence.getRunOptions().getInitiator();
		if (initiator == null) {
			return senders;
		}

		if (runningSequence.resolveCondition(valueText) ^ inverseSearch) {
			senders.add(initiator);
		}

		return senders;
	}

}
