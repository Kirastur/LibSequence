package de.polarwolf.libsequence.actions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Send a <I>broadcast</I> to all players
 *
 */
public class LibSequenceActionBroadcast extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_PERMISSION = "permission";

	@Override
	public Set<String> getRequiredAttributes() {
		Set<String> myAttributes = new HashSet<>();
		myAttributes.add(KEYNAME_MESSAGE);
		return myAttributes;
	}

	@Override
	public Set<String> getOptionalAttributes() {
		Set<String> myAttributes = new HashSet<>();
		myAttributes.add(KEYNAME_PERMISSION);
		return myAttributes;
	}

	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep)
			throws LibSequenceException {
		String messageText = sequence.findValueLocalizedAndResolvePlaceholder(configStep, KEYNAME_MESSAGE, null);
		String permission = sequence.findValueLocalizedAndResolvePlaceholder(configStep, KEYNAME_PERMISSION, null);

		if ((permission != null) && (!permission.isEmpty())) {
			Bukkit.broadcast(messageText, permission);
		} else {
			Bukkit.broadcastMessage(messageText);
		}

	}

}
