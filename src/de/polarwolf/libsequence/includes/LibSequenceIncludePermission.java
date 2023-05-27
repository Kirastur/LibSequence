package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.LSIERR_VALUE_MISSING;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Add all players with the given permission. The check is done using the normal
 * minecraft permission system, so all features of the permission system like OP
 * or permission groups are respected. Together with the inverse operator "!" it
 * adds all players not having the permission.
 *
 */
public class LibSequenceIncludePermission implements LibSequenceInclude {

	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(includeName, valueText);
		if (valueText.isEmpty()) {
			throw new LibSequenceIncludeException(includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			boolean hasPermission = player.hasPermission(valueText);
			// Now it gets tricky: ^ is the XOR operator, this is not math square
			if (hasPermission ^ inverseSearch) {
				senders.add(player);
			}
		}

		return senders;
	}

}
