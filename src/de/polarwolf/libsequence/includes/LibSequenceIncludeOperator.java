package de.polarwolf.libsequence.includes;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeOperator  implements LibSequenceInclude {
	
	public static final String AUTOOP = "luckperms.autoop"; 

	
	@Override
	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) throws LibSequenceException {
		valueText = runningSequence.resolvePlaceholder(valueText);

		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
			boolean isOP = player.isOp() || player.hasPermission(AUTOOP);
			if (isOP ^ inverseSearch) {
				senders.add(player);
			}
		}
		
		return senders;
	}

}
