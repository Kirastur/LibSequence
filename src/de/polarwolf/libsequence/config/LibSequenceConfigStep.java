package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;

public class LibSequenceConfigStep {

	// The two important keys in a step are hard-coded here
	public static final String KEYNAME_WAIT = "wait-after-action";
	public static final String KEYNAME_ACTION = "action";
	
	// Please use the public getter for this
	// Perhaps someone wants to override it
	private final String sequenceName;
	private final int stepNr;
	
	// The actionValidator is called during syntax check
	protected final LibSequenceActionValidator actionValidator;
	
	// Container for the Name/Value pairs of the step
	protected final Map<String,String> attributes = new HashMap<>();
	

	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr, ConfigurationSection config) throws LibSequenceConfigException {
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;
		loadStepFromConfig(config);
	}
	

	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr, Map<String,String> config) throws LibSequenceConfigException {
		this.actionValidator=actionValidator;
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;
		loadStepFromMap(config);
	}
	

	// The bukkit ConfigurationSection is not able to detect duplicate keys
	// so we cannot check for it
	protected void loadStepFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (String keyName : config.getKeys(false)) {
			String valueText = config.getString(keyName);
			if (valueText==null) {
				throw new LibSequenceConfigException(getSequenceName(), getStepNr(), LSCERR_KEY_SYNTAX_ERROR, keyName);
			}
			attributes.put(keyName, valueText);
		}
	}
	

	protected void loadStepFromMap(Map<String,String> config) throws LibSequenceConfigException {
		for (Entry<String,String> entry : config.entrySet()) { 
			String keyName = entry.getKey();
			String valueText = entry.getValue();
			if (valueText==null) {
				throw new LibSequenceConfigException(getSequenceName(), getStepNr(), LSCERR_KEY_SYNTAX_ERROR, keyName);
			}
			attributes.put(keyName, valueText);
		}
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
	public String findValue(String keyName) {
		return attributes.get(keyName);
	}
	

	// Please remember: return is null if no entry is found
	public String findValueLocalized(String keyName, String locale) {
		if (locale != null) {

			// 1st try: take the full language (e.g. "de_de")
			if (locale.length() >= 5) {
				String s = findValue(keyName + "_" + locale.substring(0, 5));
				if (s != null) {
					return s;
				}
			}
		
			// 2nd try: take the group language (e.g. "de")
			if (locale.length() >= 2) {
				String s = findValue (keyName + "_" + locale.substring(0, 2));
				if (s != null) {
					return s;
				}
			}
		}
		
		// No localized string found, return default
		return findValue(keyName);
	}
	

	// Get the number of seconds to wait after the action is executed
	public int getWait() {
		String waitTime = findValue(KEYNAME_WAIT);
		if (waitTime==null) {
			return 0; //it could be that the step contains no wait, then set the wait to zero
		}
		// No validation here because we expect a checkSyntax() before
		return Integer.parseUnsignedInt(waitTime);
	}
	

	// The Action is the key-feature of each step
	public String findActionName() {
		return findValue(KEYNAME_ACTION);
	}
	

	public Set<String> getAttributeKeys() {
		return new HashSet<>(attributes.keySet());
	}


	// The syntax check is called during initial section load and before every run
	// An invalid sequence is not runnable
	// A Sequence is immutable (it cannot be changed after load, except explicit section unload/reload)
	public void validateSyntax() throws LibSequenceConfigException {
		
		// verify if actionName does exists		
		String actionName = findActionName();
		if ((actionName==null) || actionName.isEmpty()) {
			throw new LibSequenceConfigException(getSequenceName(), getStepNr(), LSCERR_MISSING_ACTION, null);
		}
		
		// test if wait is numeric
		String wait = findValue(KEYNAME_WAIT);
		if (wait!=null) {
			try {
				Integer.parseUnsignedInt(wait);
			} catch (Exception e) {
				throw new LibSequenceConfigException(getSequenceName(), getStepNr(), LSCERR_WAIT_NOT_NUMERIC, wait);
			}
		}
		
		// check if the action itself does not have an error
		try {
			actionValidator.validateSyntax(this);
		} catch (LibSequenceActionException e) {
			throw new LibSequenceConfigException(getSequenceName(), getStepNr(), LSCERR_ACTION, null, e); 
		}
	}
		
}
