package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionTitle extends LibSequenceActionGeneric {

	public static final String KEYNAME_TITLE = "title";
	public static final String KEYNAME_SUBTITLE = "subtitle";

	public static final String KEYNAME_FADEIN = "fadein";
	public static final String KEYNAME_STAY = "stay";
	public static final String KEYNAME_FADEOUT = "fadeout";
	
	public static final String USERERROR_TITLE = "'title' or 'subitle' must be defined";
	public static final String USERERROR_NOT_NUMERIC = "Value must be numeric";


	@Override
    public boolean hasInclude() {
		return true;
	}
	

    @Override
    public Set<String> getOptionalAttributes() {
    	Set<String> myAttributes = new HashSet<>();
    	myAttributes.add(KEYNAME_TITLE);
    	myAttributes.add(KEYNAME_SUBTITLE);
    	myAttributes.add(KEYNAME_FADEIN);
    	myAttributes.add(KEYNAME_STAY);
    	myAttributes.add(KEYNAME_FADEOUT);
    	return myAttributes;
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
    public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException {
		String sTitle = configStep.findValue(KEYNAME_TITLE);
		String sSubtitle = configStep.findValue(KEYNAME_SUBTITLE);
		String sFadein = configStep.findValue(KEYNAME_FADEIN);
		String sStay = configStep.findValue(KEYNAME_STAY);
		String sFadeout = configStep.findValue(KEYNAME_FADEOUT);
		
		if ((sTitle==null) && (sSubtitle==null)) {			
	    	throw new LibSequenceActionException(configStep.findActionName(), LSAERR_USER_DEFINED_ERROR, USERERROR_TITLE);
		}
		
		if ((!verifyNumeric(sFadein)) || (!verifyNumeric(sStay)) || (!verifyNumeric(sFadeout))) {
	    	throw new LibSequenceActionException(configStep.findActionName(), LSAERR_USER_DEFINED_ERROR, USERERROR_NOT_NUMERIC);
		}
	}


	@Override
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) throws LibSequenceException {
		String sFadein = configStep.findValue(KEYNAME_FADEIN);
		String sStay = configStep.findValue(KEYNAME_STAY);
		String sFadeout = configStep.findValue(KEYNAME_FADEOUT);
		
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
		
		Set<CommandSender> senders = sequence.performIncludes(configStep);
		
		for (CommandSender sender : senders) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				String sTitle = configStep.findValueLocalized(KEYNAME_TITLE, player.getLocale());
				sTitle = sequence.resolvePlaceholder(sTitle);
				
				String sSubtitle = configStep.findValueLocalized(KEYNAME_SUBTITLE, player.getLocale());
				sSubtitle = sequence.resolvePlaceholder(sSubtitle);
				player.sendTitle(sTitle, sSubtitle, iFadein, iStay, iFadeout);
				}
			}
		}
		

}
