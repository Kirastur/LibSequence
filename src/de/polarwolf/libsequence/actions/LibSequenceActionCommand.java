package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.main.Main;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCommand extends LibSequenceActionGeneric {

	public static final String KEYNAME_COMMAND = "command";
	public static final String KEYNAME_SENDER = "sender";
	public static final String SENDER_CONSOLE = "console";
	public static final String SENDER_INITIATOR = "initiator";
	public static final String USERERROR_NO_TARGET_FOUND = "no target found";

	public LibSequenceActionCommand(Main main) {
		super(main);
	}

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		CommandSender sender;
		String command = configStep.getValue(KEYNAME_COMMAND);
    	String senderType =configStep.getValue(KEYNAME_SENDER);
    	
    	if ((senderType != null) && senderType.equalsIgnoreCase(SENDER_INITIATOR) && (sequence.getInitiator() != null)) {
    		sender = sequence.getInitiator();
    	} else {
    		sender = main.getServer().getConsoleSender();
    	}
		try {
			Boolean result = main.getServer().dispatchCommand(sender, command);
			if (!result) {
		    	return new LibSequenceActionResult(null, LSAERR_USER_DEFINED_ERROR, USERERROR_NO_TARGET_FOUND);
			}
		} catch (CommandException e) {
	    	return new LibSequenceActionResult(null, LSAERR_EXCEPTION, e.getMessage());
		}
    	return new LibSequenceActionResult(null, LSAERR_OK, null);
	}
	
	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_COMMAND);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_COMMAND);
    	}
    	String senderType=configStep.getValue(KEYNAME_SENDER);
   		if (!((senderType == null) || senderType.isEmpty() || senderType.equalsIgnoreCase(SENDER_CONSOLE)|| senderType.equalsIgnoreCase(SENDER_INITIATOR))) {  
       		return new LibSequenceActionResult(configStep.getActionName(), LASERR_UNKNOWN_VALUE, KEYNAME_SENDER + ": " + senderType);
    	}
   		return new LibSequenceActionResult(null, LSAERR_OK, null);
    }

}
