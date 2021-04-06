package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionInfo extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";

	public LibSequenceActionInfo(Plugin plugin) {
		super(plugin);
	}

	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_MESSAGE);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_MESSAGE);
    	}
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null);
    }

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String messageText = configStep.getValue(KEYNAME_MESSAGE);
		messageText = sequence.resolvePlaceholder(messageText);
		plugin.getLogger().info(messageText);
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null);
	}

}
