package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.entity.Player;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionNotify extends LibSequenceActionGeneric {

	public static final String KEYNAME_MESSAGE = "message";
	public static final String KEYNAME_PERMISSION = "include_permission";

	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String message=configStep.getValue(KEYNAME_MESSAGE);
    	if (message==null) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, KEYNAME_MESSAGE, null);
    	}
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null, null);
    }
	
	protected boolean checkPermission(Player player, String permission) {
		if ((permission == null) || (permission.isEmpty())) {
			return true;
		}
		return player.hasPermission(permission);
	}

	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {
		String permission = configStep.getValue(KEYNAME_PERMISSION);
		permission = sequence.resolvePlaceholder(permission);

		for (Player player : sequence.getPlugin().getServer().getOnlinePlayers()) {
			if (checkPermission(player, permission)) {
				String messageText = configStep.getValueLocalized(KEYNAME_MESSAGE, player.getLocale());
				messageText = sequence.resolvePlaceholder(messageText);
				player.sendMessage(messageText);
			}
		}
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null, null);
	}

}
