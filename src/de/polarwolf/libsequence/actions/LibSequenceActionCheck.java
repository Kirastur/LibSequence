package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.integrations.LibSequenceIntegrationWorldguard;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceActionCheck  extends LibSequenceActionGeneric {
	
	protected final LibSequenceIntegrationWorldguard integrationWorldguard;
	
	public static final String KEYNAME_CONDITION = "check_condition";
	public static final String KEYNAME_REGION = "check_region";
	public static final String KEYNAME_PERMISSION = "check_permission";
	public static final String KEYNAME_DENYMESSAGE = "denymessage";
	
	public LibSequenceActionCheck(LibSequenceIntegrationWorldguard integrationWorldguard) {
		this.integrationWorldguard = integrationWorldguard;
	}

    @Override
	public LibSequenceActionResult checkSyntax(LibSequenceConfigStep configStep) {
    	String region=configStep.getValue(KEYNAME_REGION);
    	if ((region != null) && (integrationWorldguard == null)) {
    		return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_USER_DEFINED_ERROR, "WorldGuard needed for region check");
    		
    	}
    	return new LibSequenceActionResult(configStep.getSequenceName(), configStep.getActionName(), LSAERR_OK, null);
    }
    
    protected boolean checkCondition(String text) {
    	if (text.isEmpty()) {
    		return false; 
    	}
    	if (text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("true")) {
    		return true;
    	}
    	try {
    		double d = Double.parseDouble(text);
        	return (d >= 1);
    	} catch (Exception e) {
			return false;
    	}
    }
    
    protected String checkRegion(Player player, String regionName) {
    	if (regionName.isEmpty()) {
    		return "no region given";    	
    	}
    	int result = integrationWorldguard.testPlayer(player, regionName);
    	switch(result) {
    		case LibSequenceIntegrationWorldguard.ERR_OK: return "";
    		case LibSequenceIntegrationWorldguard.ERR_PLAYEROUTSIDE: return "player outside region";
    		case LibSequenceIntegrationWorldguard.ERR_GENERIC: return "generic region error";
    		case LibSequenceIntegrationWorldguard.ERR_NOWORLD: return "world not found";
    		case LibSequenceIntegrationWorldguard.ERR_NOREGION: return "region not found";
    		default: return "unknown region error";
    	}
    }
    
	protected boolean checkPermission(Player player, String permission) {
		if (permission.isEmpty()) {
			return false;
		}
		return player.hasPermission(permission);
	}
	
	@Override
	public LibSequenceActionResult doExecute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep) {

		boolean isValid = true;
		String sReason = "";
		Player player = null;
		// If initiator is not a Player, RegionCheck and PermissionCheck will always fail
		CommandSender initiator = sequence.getRunOptions().getInitiator();
		if (initiator instanceof Player) {
			player = (Player)initiator;
		}

		
		// Get Attributes
		String sCondition = configStep.getValue(KEYNAME_CONDITION);
		sCondition = sequence.resolvePlaceholder(sCondition);

		String sRegion = configStep.getValue(KEYNAME_REGION);
		sRegion = sequence.resolvePlaceholder(sRegion);

		String sPermission = configStep.getValue(KEYNAME_PERMISSION);
		sPermission = sequence.resolvePlaceholder(sPermission);
		
		String sDenyMessage;
		if (player != null) {
			sDenyMessage = configStep.getValueLocalized(KEYNAME_DENYMESSAGE, player.getLocale());
		} else { 
			sDenyMessage = configStep.getValue(KEYNAME_DENYMESSAGE);
		}
		sDenyMessage = sequence.resolvePlaceholder(sDenyMessage);

		// First Check: Condition
		if ((sCondition != null) && (!checkCondition(sCondition))) {
			isValid = false;			
			sReason = KEYNAME_CONDITION;
		}
		
		// Second check: Region
		if (sRegion != null) {
			if (player == null) {
				isValid = false;
				sReason = KEYNAME_REGION + ": not a player";				
			} else {
				String sRegionResult = checkRegion(player, sRegion);
				if (!sRegionResult.isEmpty()) {
					isValid = false;
					sReason = KEYNAME_REGION + ": " + sRegionResult;									
				}
			}
		}
		
		// Third Check: Permission
		if ((sPermission != null) &&
			((player == null) || (!checkPermission(player, sPermission)))) {
			isValid = false;
			sReason = KEYNAME_PERMISSION;
		}
		
		// Finally if one of the check fails, cancel the sequence
		if (!isValid) {
			// A failed action does not terminate the sequence. So a dedicated cancel is needed 
			sequence.cancel();
			if ((player != null) && (sDenyMessage != null) && (!sDenyMessage.isEmpty())) {
				player.sendMessage(sDenyMessage);
			}
			return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_CHECK_FAILED, sReason);
		}
    	return new LibSequenceActionResult(sequence.getName(), configStep.getActionName(), LSAERR_OK, null);
	}

}
