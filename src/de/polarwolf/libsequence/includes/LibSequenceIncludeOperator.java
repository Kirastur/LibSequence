package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeOperator  implements LibSequenceInclude {
	
	public static final String AUTOOP = "luckperms.autoop"; 

	
	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		valueText = runningSequence.resolvePlaceholder(valueText);
		if (valueText.isEmpty()) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_VALUE_MISSING, null);
		}

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
			boolean isOP = player.isOp() || player.hasPermission(AUTOOP);
			if (isOP ^ inverseSearch) {
				senders.add(player);
			}
		}
		
		return new LibSequenceIncludeResult(senders, includeName, LSIERR_OK, null);
	}

}
