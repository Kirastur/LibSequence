package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeCondition  implements LibSequenceInclude {
	
	protected final LibSequencePlaceholderManager placeholderManager;
	

	public LibSequenceIncludeCondition(LibSequencePlaceholderManager placeholderManager) {
		this.placeholderManager=placeholderManager;
	}

	
	@Override
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence) {
		Set<CommandSender> senders = new HashSet<>();
		for (Player player : runningSequence.getPlugin().getServer().getOnlinePlayers()) {
			LibSequenceRunOptions playerRunOptions = runningSequence.getRunOptions().getCopy();
			playerRunOptions.setInitiator(player);
			// Don't use runningSequence here because we have our own runOptions
			String conditionText = placeholderManager.resolvePlaceholder(valueText, playerRunOptions);
			if (!conditionText.isEmpty()) {
				boolean hasCondition = runningSequence.resolveCondition(conditionText); 
				// Now it gets tricky: ^ is the XOR operator, this is not math square
				if (hasCondition ^ inverseSearch) {
					senders.add(player);
				}
			}
		}
		
		return new LibSequenceIncludeResult(senders,  includeName, LSIERR_OK, null);
	}

}
