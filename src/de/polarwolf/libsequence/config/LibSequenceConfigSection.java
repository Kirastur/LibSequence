package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// A section is a set of sequences provided by a specific source
// The source can be the local config.yml of LibSequence itself
// or it was read from a 3rd-party plugin

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;

public class LibSequenceConfigSection {

	protected final LibSequenceCallback callback;
	protected final LibSequenceActionValidator actionValidator;

	protected final String sequenceWithSyntaxError;
	
	protected final Set<LibSequenceConfigSequence> sequences = new HashSet<>();
	
	public LibSequenceConfigSection(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, ConfigurationSection config) {
		this.callback=callback;
		this.actionValidator=actionValidator;
		
		sequenceWithSyntaxError=loadSequencesFromConfig(config);
	}
	
	public LibSequenceConfigSection(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, Map<String,List<Map<String,String>>> config) {
		this.callback=callback;
		this.actionValidator=actionValidator;

		sequenceWithSyntaxError=loadSequencesFromMap(config);
	}

	protected String loadSequencesFromConfig(ConfigurationSection config) {
		for (String sequenceName : config.getKeys(false)) {
			ConfigurationSection configurationSection = config.getConfigurationSection(sequenceName);
			if (configurationSection==null) {
				return sequenceName;
			} else {
				LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(callback, actionValidator, sequenceName, configurationSection);
				sequences.add(sequenceConfig);
			}
		}
		return null;
	}

	protected String loadSequencesFromMap (Map<String,List<Map<String,String>>> config) {
		for (Entry<String,List<Map<String,String>>> sequence: config.entrySet()) {
				String sequenceName = sequence.getKey();
			List<Map<String,String>> sequenceValue=sequence.getValue();
			if (sequenceValue==null) {
				return sequenceName;
			} else {
				LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(callback, actionValidator, sequenceName, sequenceValue);
				sequences.add(sequenceConfig);
			}
		}
		return null;
	}

	// You can only perform admin-operations on the section if you are the owner of the section
	// Only the owner knows the callback-object, so we use this as authentication method
	// So we have implemented a method to check this
	// This method is final, so no one can override this to steal the callback
	// This is needed because we have a loop which cycles through all sections to find the section fitting to the given callback
	public final boolean verifyAccess(LibSequenceCallback callbackToCheck) {
		return callbackToCheck==callback;
	}	

	public int getSize() {
		return sequences.size();
	}
	
	public LibSequenceConfigSequence getSequence(String sequenceName) {
		for (LibSequenceConfigSequence sequence : sequences) {
			if (sequence.getSequenceName().equals(sequenceName)) {
				return sequence;
			}
		}
		return null;
	}

	public Set<String> getSequenceKeys() {
		Set<String> keySet = new HashSet<>();
		for (LibSequenceConfigSequence sequence : sequences) {
			keySet.add(sequence.getSequenceName());
		}
		return keySet;
	}
	
	// This method is final, so no one can override this to steal the securityToken
	// This is needed because we have a loop which cycles through all sections to find the sequence fitting to the given securityToken
	public final LibSequenceConfigSequence findSequence(String securityToken) {
		for (LibSequenceConfigSequence sequence : sequences) {
			if (sequence.verifySecurityToken(securityToken)) {
				return sequence;
			}
		}
		return null;
	}
	
	public LibSequenceConfigResult checkSyntax() {
		if (sequenceWithSyntaxError!=null) {
			return new LibSequenceConfigResult(sequenceWithSyntaxError, 0, LSCERR_SEQUENCE, null, null);
		}
		for (LibSequenceConfigSequence sequence : sequences) {
			LibSequenceConfigResult result = sequence.checkSyntax();
			if (result.hasError()) {
				return result;
			}
		}
		return new LibSequenceConfigResult(null, 0, LSCERR_OK, null, null);		
	}

}
