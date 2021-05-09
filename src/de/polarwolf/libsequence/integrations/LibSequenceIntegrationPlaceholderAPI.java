package de.polarwolf.libsequence.integrations;

import org.bukkit.entity.Player;

import de.polarwolf.libsequence.exception.LibSequenceException;
import me.clip.placeholderapi.PlaceholderAPI;

public class LibSequenceIntegrationPlaceholderAPI {
	
	public static final String PLACEHOLDER_API_NAME = "PlaceholderAPI";
	
	public String setPlaceholders(Player player, String text) throws LibSequenceIntegrationException {
		try {
			return PlaceholderAPI.setPlaceholders(player, text);
		} catch (Exception e) {
			throw new LibSequenceIntegrationException(PLACEHOLDER_API_NAME, LibSequenceException.JAVA_EXCEPTION, "\"" + text + "\"", e);
		}
	}

}
