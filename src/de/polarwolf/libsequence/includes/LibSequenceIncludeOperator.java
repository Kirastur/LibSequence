package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * If the condition returns true, all players with OP status are added. OP is
 * tested on both: standard minecraft OP and LuckPerms auto-op
 * ("luckperms.autoop").
 *
 * For the condition you can just say "yes" here, or you can write a more
 * sophisticated condition including placeholders.
 *
 */
public class LibSequenceIncludeOperator implements LibSequenceInclude {

	public static final String AUTOOP = "luckperms.autoop";

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);
		Set<CommandSender> senders = new HashSet<>();

		boolean isCondition = runningSequence.resolveCondition(valueText);
		if (!(isCondition ^ inverseSearch)) {
			return senders;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.isOp() || player.hasPermission(AUTOOP)) {
				senders.add(player);
			}
		}

		return senders;
	}

}
