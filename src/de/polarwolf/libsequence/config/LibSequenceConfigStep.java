package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// A Step is an unordered list of name/value pairs


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;

public class LibSequenceConfigStep {

	// The two important keys in a step are hard-coded here
	public static final String KEYNAME_WAIT = "wait-after-action";
	public static final String KEYNAME_ACTION = "action";
	
	// Please use the public getter for this
	// Perhaps someone wants to override it
	protected final String sequenceName;
	protected final int stepNr;
	
	// The actionValidator is called during syntax check
	protected final LibSequenceActionValidator actionValidator;
	
	// Flag if we have a problem reading the config from source
	protected final String keyWithSyntaxError;

	// Container for the Name/Value pairs of the step
	protected final Map<String,String> stepData = new  HashMap<>();
	

	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr, ConfigurationSection config) {
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;

		keyWithSyntaxError=loadStepFromConfig(config);
	}
	

	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr, Map<String,String> config) {
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;

		keyWithSyntaxError=loadStepFromMap(config);
	}
	

	protected String loadStepFromConfig(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {
			String value = config.getString(key);
			if (value==null) {
				return key;
			} else {
				stepData.put(key, config.getString(key));
			}
		}
		return null;
	}
	

	protected String loadStepFromMap(Map<String,String> config) {
		for (Entry<String,String> entry : config.entrySet()) { 
			String name = entry.getKey();
			String value = entry.getValue();
			if (value==null) {
				return name;
			} else {
				stepData.put(name, value);
			}
		}
		return null;		
	}
	

	// We use this check for the ActionManager to verify that the step belongs to the correct sequencer
	// This is not a security feature because the ActionValidator is public
	public final boolean isSameInstance(LibSequenceActionValidator actionValidatorToTest) {
		return actionValidator.isSameInstance(actionValidatorToTest);
	}
		

	public String getSequenceName() {
		return sequenceName;
	}
	

	public int getStepNr() {
		return stepNr;
	}
	

	// Please remember: return is null if no entry is found
	public String getValue(String keyName) {
		return stepData.get(keyName);
	}
	

	// Please remember: return is null if no entry is found
	public String getValueLocalized(String keyName, String locale) {
		if (locale != null) {

			// 1st try: take the full language (e.g. "de_de")
			if (locale.length() >= 5) {
				String s = getValue(keyName + "_" + locale.substring(0, 5));
				if (s != null) {
					return s;
				}
			}
		
			// 2nd try: take the group language (e.g. "de")
			if (locale.length() >= 2) {
				String s = getValue (keyName + "_" + locale.substring(0,  2));
				if (s != null) {
					return s;
				}
			}
		}
		
		// No localized string found, return default
		return getValue(keyName);
	}
	

	// Get the number of seconds to wait after the action is executed
	public int getWait() {
		String waitTime = getValue(KEYNAME_WAIT);
		if (waitTime==null) {
			return 0; //it could be that the step contains no wait, then set the wait to zero
		}
		// No validation here because we expect a checkSyntax() before
		return Integer.parseUnsignedInt(waitTime);
	}
	

	// The Action is the key-feature of each step
	public String getActionName() {
		return getValue(KEYNAME_ACTION);
	}
	

	public Set<String> getKeys() {
		return stepData.keySet();
	}


	// The syntax check is called during initial section load and before every run
	// An invalid sequence is not runnable
	// A Sequence is immutable (it cannot be changed after load, except explicit section unload/reload)
	public LibSequenceConfigResult checkSyntax() {
		if ((keyWithSyntaxError!=null) && (!keyWithSyntaxError.isEmpty())) {		
			return new LibSequenceConfigResult(getSequenceName(), getStepNr(), LSCERR_KEY_SYNTAX_ERROR, keyWithSyntaxError, null);
		}
		String actionName = getActionName();
		if ((actionName==null) || actionName.isEmpty()) {
			return new LibSequenceConfigResult(getSequenceName(), getStepNr(), LSCERR_MISSING_ACTION, null, null);
		}
		String wait = getValue(KEYNAME_WAIT);
		if (wait!=null) {
			try {
				Integer.parseUnsignedInt(wait);
			} catch (Exception e) {
				return new LibSequenceConfigResult(getSequenceName(), getStepNr(), LSCERR_WAIT_NOT_NUMERIC, wait, null);
			}
		}
		LibSequenceActionResult actionResult = actionValidator.validateAction(this);
		if (actionResult.hasError()) {
			return new LibSequenceConfigResult(getSequenceName(), getStepNr(), LSCERR_ACTION, null, actionResult);
		}
		return new LibSequenceConfigResult(getSequenceName(), getStepNr(), LSCERR_OK, null, null);
	}
		
}
