package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionTitle extends LibSequenceActionGeneric {

	public static final String KEYNAME_PERMISSION = "permission";

	public static final String KEYNAME_TITLE = "title";
	public static final String KEYNAME_SUBTITLE = "subtitle";

	public static final String KEYNAME_FADEIN = "fadein";
	public static final String KEYNAME_STAY = "stay";
	public static final String KEYNAME_FADEOUT = "fadeout";

	public LibSequenceActionTitle(Plugin plugin) {
		super(plugin);
	}
	
	protected boolean verifyNumeric(String keyValue) {
		if ((keyValue == null) || (keyValue.isEmpty())) {
			return true;
		}
		try {
			Integer.parseUnsignedInt(keyValue);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
    public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
		String sTitle = configStep.getValue(KEYNAME_TITLE);
		String sSubtitle = configStep.getValue(KEYNAME_SUBTITLE);
		String sFadein = configStep.getValue(KEYNAME_FADEIN);
		String sStay = configStep.getValue(KEYNAME_STAY);
		String sFadeout = configStep.getValue(KEYNAME_FADEOUT);
		
		if ((sTitle==null) && (sSubtitle==null)) {			
	    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_MISSING_ATTRIBUTE, "'title' or 'subitle' must be defined");
		}
		
		if ((!verifyNumeric(sFadein)) || (!verifyNumeric(sStay)) || (!verifyNumeric(sFadeout))) {
	    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_EXCEPTION, "Attribute must be numeric");
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
		String sTitle = configStep.getValue(KEYNAME_TITLE);
		String sSubtitle = configStep.getValue(KEYNAME_SUBTITLE);
		String sFadein = configStep.getValue(KEYNAME_FADEIN);
		String sStay = configStep.getValue(KEYNAME_STAY);
		String sFadeout = configStep.getValue(KEYNAME_FADEOUT);
		String permission = configStep.getValue(KEYNAME_PERMISSION);
		
		int iFadein = 10;
		int iStay = 70;
		int iFadeout = 20;
		
		if ((sFadein != null) && (!sFadein.isEmpty())) {
			iFadein = Integer.parseUnsignedInt(sFadein);
		}
		
		if ((sStay != null) && (!sStay.isEmpty())) {
			iStay = Integer.parseUnsignedInt(sStay);
		}

		if ((sFadeout != null) && (!sFadeout.isEmpty())) {
			iFadeout = Integer.parseUnsignedInt(sFadeout);
		}
		
		if (sTitle != null) {
			sTitle = sequence.resolvePlaceholder(sTitle);
		}
		
		if (sSubtitle != null) {
			sSubtitle = sequence.resolvePlaceholder(sSubtitle);
		}

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (checkPermission(player, permission)) {
				player.sendTitle(sTitle, sSubtitle, iFadein, iStay, iFadeout);
			}
		}
		
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null);
	}

}
