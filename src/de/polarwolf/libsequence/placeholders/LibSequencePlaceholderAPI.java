package de.polarwolf.libsequence.placeholders;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.integrations.LibSequenceIntegrationPlaceholderAPI;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequencePlaceholderAPI implements LibSequencePlaceholder{
	
	protected final LibSequenceIntegrationPlaceholderAPI placeholderAPI;
	
	public LibSequencePlaceholderAPI (LibSequenceIntegrationPlaceholderAPI placeholderAPI) {
		this.placeholderAPI = placeholderAPI;
	}

	@Override
	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions) {
		CommandSender initiator = runOptions.getInitiator();
		if (initiator instanceof Player) {
			Player player = (Player)initiator;
			return placeholderAPI.setPlaceholders(player, messageText);
		} else { 
			return placeholderAPI.setPlaceholders(null, messageText);
		}
	}
	
}
