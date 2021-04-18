package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// A sequence is an ordered list of steps

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;


public class LibSequenceConfigSequence {

	// Please use the public getter for this
	// Perhaps someone want to override
	private final String sequenceName;

	// The security token is a sensitive element and therefore only private
	private final String securityToken;
	
	protected final LibSequenceCallback callback;
	protected final LibSequenceActionValidator actionValidator;

	protected final boolean hasEnumError;

	protected final ArrayList<LibSequenceConfigStep> steps = new ArrayList<>();
		
	public LibSequenceConfigSequence(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, String sequenceName, ConfigurationSection config) {
		this.callback=callback;
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		
		hasEnumError=!loadStepsFromConfig(config);
		securityToken=UUID.randomUUID().toString();
	}
	
	public LibSequenceConfigSequence(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, String sequenceName, List<Map<String,String>> config) {
		this.callback=callback;
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		
		hasEnumError=!loadStepsFromList(config);	
		securityToken=UUID.randomUUID().toString();
	}

	protected boolean loadStepsFromConfig(ConfigurationSection config) {
		int i = 1;
		while(config.contains(Integer.toString(i), true)) {
			ConfigurationSection subConfig = config.getConfigurationSection(Integer.toString(i));
			if (subConfig==null) {
				return false;
			}
			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), getSize()+1, subConfig));
			i = i +1;
		}
		return (getSize()==(i-1));
	}

	protected boolean loadStepsFromList(List<Map<String,String>> config) {
		for (Map<String,String> stepTouples : config) {
			if (stepTouples==null) {
				return false;
			}
			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), getSize()+1, stepTouples));			
		}
		return true;
	}
	
	// This method is final, so no one can override this to steal a foreign callback-object
	public final boolean verifyAccess(LibSequenceCallback callbackToCheck) {
		return callbackToCheck==callback;
	}
	
	// The security token is needed to execute a sequence
	// You can get it only if you are the owner of the sequence
	// Only the owner knows the callback-object, so we use this as authentication method 
	public String getSecurityToken (LibSequenceCallback callbackAsAuthentication) {
		if (verifyAccess(callbackAsAuthentication)) { 
			return securityToken;
		} else {
			return null;
		}
	}
	
	// Even it the security token is not known by others, 
	// every plugin can verify if it has the correct one for this sequence
	// This method is final, so no one can override this to steal a foreign token
	// This is needed because we have a loop which cycles through all sequences to find the sequence fitting to a given token
	public final boolean verifySecurityToken(String tokenToCheck) {
		return securityToken.equals(tokenToCheck);
	}

	public String getSequenceName() {
		return sequenceName;
	}
	
	public int getSize() {
		return steps.size();
	}
	
	public LibSequenceConfigStep getStep(int stepNr) {
		if ((stepNr > 0) && (stepNr <= getSize())) {
			return steps.get(stepNr-1);
		} else  {
			return null;
		}
	}
	
	public LibSequenceConfigResult checkSyntax() {
		if (hasEnumError) {
			return new LibSequenceConfigResult(getSequenceName(), 0, LSCERR_STEP_ENUM, null, null);
		}
		for (LibSequenceConfigStep step : steps) {
			LibSequenceConfigResult result = step.checkSyntax();
			if (result.hasError()) {
				return result;
			}
		}
		return new LibSequenceConfigResult(getSequenceName(), 0, LSCERR_OK, null, null);		
	}
	
}
