package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.LSAERR_UNKNOWN_VALUE;
import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.LSAERR_USER_DEFINED_ERROR;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Execute a given minecraft command.
 *
 */
public class LibSequenceActionCommand extends LibSequenceActionGeneric {

	public static final String KEYNAME_COMMAND = "command";
	public static final String KEYNAME_SENDER = "sender";
	public static final String SENDER_CONSOLE = "console";
	public static final String SENDER_INITIATOR = "initiator";
	public static final String USERERROR_NO_TARGET_FOUND = "no target found";
	public static final String USERERROR_NO_COMMAND_GIVEN = "no command given";

	@Override
	public Set<String> getRequiredAttributes() {
		Set<String> myAttributes = new HashSet<>();
		myAttributes.add(KEYNAME_COMMAND);
		return myAttributes;
	}

	@Override
	public Set<String> getOptionalAttributes() {
		Set<String> myAttributes = new HashSet<>();
		myAttributes.add(KEYNAME_SENDER);
		return myAttributes;
	}

	@Override
	public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException {
		String senderType = configStep.findValue(KEYNAME_SENDER);
		if (!((senderType == null) || senderType.isEmpty() || senderType.equalsIgnoreCase(SENDER_CONSOLE)
				|| senderType.equalsIgnoreCase(SENDER_INITIATOR))) {
			throw new LibSequenceActionException(configStep.getActionName(), LSAERR_UNKNOWN_VALUE,
					KEYNAME_SENDER + ": " + senderType);
		}
	}

	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep)
			throws LibSequenceException {
		String command = sequence.findValueLocalizedAndResolvePlaceholder(configStep, KEYNAME_COMMAND, null);
		String senderType = configStep.findValue(KEYNAME_SENDER);

		if (command.isEmpty()) {
			throw new LibSequenceActionException(configStep.getActionName(), LSAERR_USER_DEFINED_ERROR,
					USERERROR_NO_COMMAND_GIVEN);
		}

		CommandSender sender = Bukkit.getConsoleSender();
		if ((senderType != null) && senderType.equalsIgnoreCase(SENDER_INITIATOR)
				&& (sequence.getRunOptions().getInitiator() != null)) {
			sender = sequence.getRunOptions().getInitiator();
		}

		boolean result = Bukkit.dispatchCommand(sender, command);
		if (!result) {
			throw new LibSequenceActionException(configStep.getActionName(), LSAERR_USER_DEFINED_ERROR,
					USERERROR_NO_TARGET_FOUND);
		}
	}

}
