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
	protected final Set<LibSequenceConfigSequence> sequences = new HashSet<>();
	

	public LibSequenceConfigSection(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, ConfigurationSection config) throws LibSequenceConfigException {
		this.callback=callback;
		this.actionValidator=actionValidator;		
		loadSequencesFromConfig(config);
	}
	

	public LibSequenceConfigSection(LibSequenceCallback callback, LibSequenceActionValidator actionValidator, Map<String,List<Map<String,String>>> config) throws LibSequenceConfigException {
		this.callback=callback;
		this.actionValidator=actionValidator;
		loadSequencesFromMap(config);
	}


	protected void loadSequencesFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (String sequenceName : config.getKeys(false)) {
			ConfigurationSection configurationSection = config.getConfigurationSection(sequenceName);
			if (configurationSection==null) {
				throw new LibSequenceConfigException(LSCERR_NO_CONFIGFILE, sequenceName);
			}
			LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(callback, actionValidator, sequenceName, configurationSection);
			sequences.add(sequenceConfig);
		}
	}


	protected void loadSequencesFromMap (Map<String,List<Map<String,String>>> config) throws LibSequenceConfigException {
		for (Entry<String,List<Map<String,String>>> sequence: config.entrySet()) {
				String sequenceName = sequence.getKey();
			List<Map<String,String>> sequenceValue=sequence.getValue();
			if (sequenceValue==null) {
				throw new LibSequenceConfigException(LSCERR_SECTION_GENERATION_ERROR, sequenceName);
			}
			LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(callback, actionValidator, sequenceName, sequenceValue);
			sequences.add(sequenceConfig);
		}
	}


	public int getSize() {
		return sequences.size();
	}
	

	// You can only perform admin-operations on the section if you are the owner of the section
	// Only the owner knows the callback-object, so we use this as authentication method
	// So we have implemented a method to check this
	// This method is final, so no one can override this to steal the callback
	// This is needed because we have a loop which cycles through all sections to find the section fitting to the given callback
	public final boolean hasAccess(LibSequenceCallback callbackToCheck) {
		return callbackToCheck==callback;
	}	


	public boolean hasSequence(String sequenceName) {
		for (LibSequenceConfigSequence sequence : sequences) {
			if (sequence.getSequenceName().equals(sequenceName)) {
				return true;
			}
		}
		return false;
	}
	

	public LibSequenceConfigSequence getSequence(String sequenceName) throws LibSequenceConfigException {
		for (LibSequenceConfigSequence sequence : sequences) {
			if (sequence.getSequenceName().equals(sequenceName)) {
				return sequence;
			}
		}
		throw new LibSequenceConfigException(LSCERR_SEQUENCE, sequenceName);
	}


	public Set<String> getSequenceNames() {
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
			if (sequence.isValidSecurityToken(securityToken)) {
				return sequence;
			}
		}
		return null;
	}
	

	public void validateSyntax() throws LibSequenceConfigException {
		for (LibSequenceConfigSequence sequence : sequences) {
			sequence.validateSyntax();
		}
	}

}
