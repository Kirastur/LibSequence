package de.polarwolf.libsequence.reload;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Container to store file-related reload settings
 *
 */
public record LibSequenceReloaderConfigFileSetting(Plugin plugin, LibSequenceToken ownerToken, String fileName,
		String fileSection) {

}
