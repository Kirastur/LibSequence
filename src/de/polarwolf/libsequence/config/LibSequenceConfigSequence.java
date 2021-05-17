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
	private String securityToken;
	private final LibSequenceCallback callback;

	protected final LibSequenceActionValidator actionValidator;

	protected final ArrayList<LibSequenceConfigStep> steps = new ArrayList<>();
		

	public LibSequenceConfigSequence(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, String sequenceName, ConfigurationSection config) throws LibSequenceConfigException {
		this.callback=callback;
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		
		recreateSecurityToken(callback);
		loadStepsFromConfig(config);
	}
	

	public LibSequenceConfigSequence(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, String sequenceName, List<Map<String,String>> config) throws LibSequenceConfigException {
		this.callback=callback;
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		
		recreateSecurityToken(callback);
		loadStepsFromList(config);	
	}
	
	protected String stepNrToString (int stepNr) {
		return "Step " + Integer.toString(stepNr);
	}


	// The bukkit ConfigurationSection is not able to detect duplicate keys
	// so we cannot check for duplicate steps
	protected void loadStepsFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (int i = 1; i <= config.getKeys(false).size(); i++) {

			if (!config.contains(Integer.toString(i), true)) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM, stepNrToString(i));
			}

			ConfigurationSection subConfig = config.getConfigurationSection(Integer.toString(i));
			if (subConfig==null) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM, stepNrToString(i));
			}

			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), i, subConfig));
		}
	}


	protected void  loadStepsFromList(List<Map<String,String>> config) throws LibSequenceConfigException {
		for (Map<String,String> stepTouples : config) {
			if (stepTouples==null) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM, stepNrToString(getSize()+1));
			}
			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), getSize()+1, stepTouples));			
		}
	}
	

	// This method is final, so no one can override this to steal a foreign callback-object
	public final boolean isOwner(LibSequenceCallback callbackToCheck) {
		return callbackToCheck==callback;
	}
	

	// The security token is needed to execute a sequence
	// You can get it only if you are the owner of the sequence
	// Only the owner knows the callback-object, so we use this as authentication method 
	public String getSecurityToken (LibSequenceCallback callbackAsAuthentication) throws LibSequenceConfigException {
		if (!isOwner(callbackAsAuthentication)) {
			throw new LibSequenceConfigException(getSequenceName(), LSCERR_NOT_AUTHORIZED, null);
		}
		return securityToken;
	}
	

	public final void recreateSecurityToken(LibSequenceCallback callbackAsAuthentication) throws LibSequenceConfigException {
		if (!isOwner(callbackAsAuthentication)) {
			throw new LibSequenceConfigException(getSequenceName(), LSCERR_NOT_AUTHORIZED, null);
		}		
		securityToken=UUID.randomUUID().toString();
	}
	

	// Even if the security token is not known by others, 
	// every plugin can verify if it has the correct one for this sequence.
	// This method is final, so no one can override this to steal a foreign token
	// This is needed because we have a loop which cycles through all sequences to find the sequence fitting to a given token
	public final boolean isValidSecurityToken(String tokenToCheck) {
		return securityToken.equals(tokenToCheck);
	}


	public String getSequenceName() {
		return sequenceName;
	}
	

	public int getSize() {
		return steps.size();
	}
	

	// The boundary check is done by Java itself
	public LibSequenceConfigStep getStep(int stepNr) {
		return steps.get(stepNr-1);
	}
	

	public void validateSyntax() throws LibSequenceConfigException {
		for (LibSequenceConfigStep step : steps) {
			step.validateSyntax();
		}
	}
	
}
