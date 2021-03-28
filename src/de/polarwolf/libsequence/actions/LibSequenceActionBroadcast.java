package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.main.Main;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionBroadcast extends LibSequenceActionGeneric {
	
	public LibSequenceActionBroadcast(Main main) {
		super(main);
	}

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		main.getServer().broadcastMessage(configStep.getMessage());
    	return new LibSequenceActionResult(null, LSAERR_OK, null);
	}
	
	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getMessage();
    	if (message==null) {
    		return new LibSequenceActionResult(null, LSAERR_MISSING_MESSAGE, null);
    	}
    	return new LibSequenceActionResult(null, LSAERR_OK, null);
    }

}
