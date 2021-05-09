package de.polarwolf.libsequence.placeholders;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationPlaceholderAPI;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequencePlaceholderAPI implements LibSequencePlaceholder{
	
	public static final String PLACEHOLDERAPI_NAME = "PlaceholderAPI";
	protected final LibSequenceIntegrationPlaceholderAPI placeholderAPI;
	

	public LibSequencePlaceholderAPI (LibSequenceIntegrationPlaceholderAPI placeholderAPI) {
		this.placeholderAPI = placeholderAPI;
	}


	@Override
	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions) throws LibSequenceException {
		CommandSender initiator = runOptions.getInitiator();
		Player player = null;
		String playerName = null;
		if (initiator instanceof Player) {
			player = (Player)initiator;
			playerName = player.getName();
		}

		try {
			return placeholderAPI.setPlaceholders(player, messageText);
		} catch (LibSequenceException e) {
			throw new LibSequencePlaceholderException (PLACEHOLDERAPI_NAME, e.getTitle(), playerName, messageText, e);
		} catch (Exception e) {
			throw new LibSequencePlaceholderException (PLACEHOLDERAPI_NAME, LibSequenceException.JAVA_EXCEPTION, playerName, messageText, e);			
		}
	}

}
