package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionNotify extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_PERMISSION = "permission";

	public LibSequenceActionNotify(Plugin plugin) {
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
	
	protected boolean checkPermission(Player player, String permission) {
		if ((permission == null) || (permission.isEmpty())) {
			return true;
		}
		return player.hasPermission(permission);
	}

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String messageText = configStep.getValue(KEYNAME_MESSAGE);
		String permission = configStep.getValue(KEYNAME_PERMISSION);
		messageText = sequence.resolvePlaceholder(messageText);

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (checkPermission(player, permission)) {
				player.sendMessage(messageText);
			}
		}
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null);
	}

}
