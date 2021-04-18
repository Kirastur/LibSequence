package de.polarwolf.libsequence.integrations;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class LibSequenceIntegrationPlaceholderAPI {
	
	public String setPlaceholders(Player player, String text) {
		return PlaceholderAPI.setPlaceholders(player, text);
	}

}
