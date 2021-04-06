package de.polarwolf.libsequence.placeholders;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import me.clip.placeholderapi.PlaceholderAPI;

public class LibSequencePlaceholderAPI implements LibSequencePlaceholder{

	@Override
	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions) {
		CommandSender initiator = runOptions.getInitiator();
		if (initiator instanceof Player) {
			Player player = (Player)initiator;
			messageText =  PlaceholderAPI.setPlaceholders(player, messageText);
		}
		return messageText;
	}
	
}
