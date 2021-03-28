package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandException;
import org.bukkit.command.ConsoleCommandSender;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.main.Main;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCommand extends LibSequenceActionGeneric {

	public static final String KEYNAME_COMMAND = "command";
	public static final String USERERROR_NO_TARGET_FOUND = "no target found";

	public LibSequenceActionCommand(Main main) {
		super(main);
	}

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String command = configStep.getValue(KEYNAME_COMMAND);
		ConsoleCommandSender sender = main.getServer().getConsoleSender();
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
    		return new LibSequenceActionResult(null, LSAERR_MISSING_ATTRIBUTE, KEYNAME_COMMAND);
    	}
    	return new LibSequenceActionResult(null, LSAERR_OK, null);
    }

}
