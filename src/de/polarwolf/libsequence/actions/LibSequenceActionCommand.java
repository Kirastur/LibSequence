package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCommand extends LibSequenceActionGeneric {

	public static final String KEYNAME_COMMAND = "command";
	public static final String KEYNAME_SENDER = "sender";
	public static final String SENDER_CONSOLE = "console";
	public static final String SENDER_INITIATOR = "initiator";
	public static final String USERERROR_NO_TARGET_FOUND = "no target found";

	public LibSequenceActionCommand(Plugin plugin) {
		super(plugin);
	}

	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String command=configStep.getValue(KEYNAME_COMMAND);
    	if (command==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_COMMAND);
    	}
    	String senderType=configStep.getValue(KEYNAME_SENDER);
   		if (!((senderType == null) || senderType.isEmpty() || senderType.equalsIgnoreCase(SENDER_CONSOLE) || senderType.equalsIgnoreCase(SENDER_INITIATOR))) {  
       		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_UNKNOWN_VALUE, KEYNAME_SENDER + ": " + senderType);
    	}
   		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null);
    }

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		CommandSender sender;
		String command = configStep.getValue(KEYNAME_COMMAND);
    	String senderType = configStep.getValue(KEYNAME_SENDER);
    	
    	command = sequence.resolvePlaceholder(command);
    	
		sender = plugin.getServer().getConsoleSender();
    	if ((senderType != null) && senderType.equalsIgnoreCase(SENDER_INITIATOR) && (sequence.getRunOptions().getInitiator() != null)) {
    		sender = sequence.getRunOptions().getInitiator();
    	}

    	try {
			boolean result = plugin.getServer().dispatchCommand(sender, command);
			if (!result) {
		    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_USER_DEFINED_ERROR, USERERROR_NO_TARGET_FOUND);
			}
		} catch (CommandException e) {
	    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_EXCEPTION, e.getMessage());
		}
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null);
	}
	
}
