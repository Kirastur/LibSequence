package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// A section is a set of sequences provided by a specific source
// The source can be the local config.yml of LibSequence itself
// or it was read from a 3rd-party plugin
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_NO_CONFIGSECTION;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_SECTION_GENERATION_ERROR;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_SEQUENCE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Defines a set of sequences (all of them have the same owner).
 *
 * @see de.polarwolf.libsequence.config.LibSequenceConfigSequence
 *      LibSequenceConfigSequence
 * @see de.polarwolf.libsequence.config.LibSequenceConfigManager
 *      LibSequenceConfigManager
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Configuration">Configuration</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
 *      Definition</A> (WIKI)
 *
 */
public class LibSequenceConfigSection {

	/**
	 * The section name is used in error messages.
	 */
	private final String sectionName;

	/**
	 * Owner of the section
	 */
	protected final LibSequenceToken ownerToken;

	/**
	 * The actionValidator identifies the LibSequence-Instance this section belongs
	 * to. It is called during syntax check.
	 */
	protected final LibSequenceActionValidator actionValidator;

	/**
	 * Unordered list of all sequences contained in this section
	 */
	protected final List<LibSequenceConfigSequence> sequences = new ArrayList<>();

	/**
	 * Build this section (including all sequences) by loading it from file (for
	 * example config.yml).
	 *
	 * @param ownerToken      The owner if this sequence.
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param config          Configuration Section to load the sequences from
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigSection(LibSequenceToken ownerToken, LibSequenceActionValidator actionValidator,
			String sectionName, ConfigurationSection config) throws LibSequenceConfigException {
		this.ownerToken = ownerToken;
		this.actionValidator = actionValidator;
		this.sectionName = sectionName;
		loadSequencesFromConfig(config);
	}

	/**
	 * Build this section (including all sequences) by loading it from an in-memory
	 * list of sequences
	 *
	 * @param ownerToken      The owner if this sequence.
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param config          List of sequences we should build the section from
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigSection(LibSequenceToken ownerToken, LibSequenceActionValidator actionValidator,
			String sectionName, Map<String, List<Map<String, String>>> config) throws LibSequenceConfigException {
		this.ownerToken = ownerToken;
		this.actionValidator = actionValidator;
		this.sectionName = sectionName;
		loadSequencesFromMap(config);
	}

	/**
	 * Create a dummy section if we want to fill the section manually
	 *
	 * @param ownerToken      The owner if this sequence.
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 */
	public LibSequenceConfigSection(LibSequenceToken ownerToken, LibSequenceActionValidator actionValidator,
			String sectionName) {
		this.ownerToken = ownerToken;
		this.actionValidator = actionValidator;
		this.sectionName = sectionName;
	}

	/**
	 * Helper function to load the section from file
	 */
	protected void loadSequencesFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (String sequenceName : config.getKeys(false)) {
			ConfigurationSection configurationSection = config.getConfigurationSection(sequenceName);
			if (configurationSection == null) {
				throw new LibSequenceConfigException(sectionName, LSCERR_NO_CONFIGSECTION, sequenceName);
			}
			LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(ownerToken, actionValidator,
					sequenceName, configurationSection);
			sequences.add(sequenceConfig);
		}
	}

	/**
	 * Helper function to load the sequence from memory
	 */
	protected void loadSequencesFromMap(Map<String, List<Map<String, String>>> config)
			throws LibSequenceConfigException {
		for (Entry<String, List<Map<String, String>>> sequence : config.entrySet()) {
			String sequenceName = sequence.getKey();
			List<Map<String, String>> sequenceValue = sequence.getValue();
			if (sequenceValue == null) {
				throw new LibSequenceConfigException(sectionName, LSCERR_SECTION_GENERATION_ERROR, sequenceName);
			}
			LibSequenceConfigSequence sequenceConfig = new LibSequenceConfigSequence(ownerToken, actionValidator,
					sequenceName, sequenceValue);
			sequences.add(sequenceConfig);
		}
	}

	/**
	 * Get the section name for printing error messages
	 *
	 * @return Name of the section as set during creation
	 */
	public String getSectionName() {
		return sectionName;
	}

	/**
	 * Get the number of sequences this section contains
	 */
	public int getSize() {
		return sequences.size();
	}

	/**
	 * Check if the given ownerToken is the owner of the section. Some section
	 * functions are limit and can only be used if the caller identifies himself as
	 * the owner of the sequence. The owner was set during object creation. This
	 * method is final, so no one can override this to steal a foreign
	 * callback-object.
	 *
	 * @param ownerTokenToCheck The ownerToken we should check
	 * @return TRUE if the given ownerToken is the sequence-owner, otherwise FALSE
	 */
	public final boolean isOwner(LibSequenceToken ownerTokenToCheck) {
		return ownerToken.equals(ownerTokenToCheck);
	}

	/**
	 * Check if the section contains a sequence with the given name
	 *
	 * @param sequenceName Sequence to look for
	 * @return TRUE if a section with this name was found, otherwise FALSE
	 */
	public boolean hasSequence(String sequenceName) {
		for (LibSequenceConfigSequence mySequence : sequences) {
			if (mySequence.getSequenceName().equals(sequenceName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the sequence with the given name.
	 *
	 * @param sequenceName Sequence to look for
	 * @return The sequence-object associated with the given name
	 * @throws LibSequenceConfigException An exception is thrown if no sequence with
	 *                                    the given name could be found in this
	 *                                    section.
	 */
	public LibSequenceConfigSequence getSequence(String sequenceName) throws LibSequenceConfigException {
		for (LibSequenceConfigSequence mySequence : sequences) {
			if (mySequence.getSequenceName().equals(sequenceName)) {
				return mySequence;
			}
		}
		throw new LibSequenceConfigException(sectionName, LSCERR_SEQUENCE, sequenceName);
	}

	/**
	 * Get a list of sequence names this section contains.
	 */
	public Set<String> getSequenceNames() {
		Set<String> keySet = new HashSet<>();
		for (LibSequenceConfigSequence mySequence : sequences) {
			keySet.add(mySequence.getSequenceName());
		}
		return keySet;
	}

	/**
	 * Get the sequence with the given security token. This method is final, so no
	 * one can override this to steal the securityToken. This is needed because we
	 * have a loop which cycles through all sections to find the sequence fitting to
	 * the given securityToken. Every sequence has its own securityToken so we can
	 * use this token to identify the sequence we should run.
	 *
	 * @param securityToken Security Token we should search the sequences for.
	 * @return The sequence if a fitting sequence was found, otherwise NULL
	 */
	public final LibSequenceConfigSequence findSequence(LibSequenceToken securityToken) {
		for (LibSequenceConfigSequence mySequence : sequences) {
			if (mySequence.isValidSecurityToken(securityToken)) {
				return mySequence;
			}
		}
		return null;
	}

	/**
	 * Validates the syntax of the complete section including all sequences
	 *
	 * @throws LibSequenceConfigException A exception is thrown if the verify fails,
	 *                                    e.g. Syntax Error
	 */
	public void validateSyntax() throws LibSequenceConfigException {
		for (LibSequenceConfigSequence mySequence : sequences) {
			mySequence.validateSyntax();
		}
	}

}
