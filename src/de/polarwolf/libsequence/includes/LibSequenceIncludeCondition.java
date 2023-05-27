package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Cycles through all players and evaluates the condition for EVERY
 * player. If the condition is true for this player, the player is added. This
 * include is designed for use with a dynamic condition, e.g. a condition
 * containing placeholders. The condition is true, if the value is "yes" or
 * "true", or if numeric >= 1.0 (after placeholder replacement).
 */
public class LibSequenceIncludeCondition implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		Set<CommandSender> senders = new HashSet<>();
		for (Player myPlayer : Bukkit.getOnlinePlayers()) {
			String conditionText = runningSequence.resolvePlaceholderForOtherPlayer(valueText, myPlayer);
			if (!conditionText.isEmpty()) {
				boolean hasCondition = runningSequence.resolveCondition(conditionText);
				// Now it gets tricky: ^ is the XOR operator, this is not math square
				if (hasCondition ^ inverseSearch) {
					senders.add(myPlayer);
				}
			}
		}

		return senders;
	}

}
