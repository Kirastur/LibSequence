package de.polarwolf.libsequence.config;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_SECTION_GENERATION_ERROR;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_SECTION_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Manages the configuration. The Manager is the controlling instance for
 * hosting and refreshing the configuration. It also acts as an interface
 * between the configuration and the API. There is only one Configuration
 * Manager running except you are using private sequencers.
 *
 * The configuration tree tree is: Manager ==> Section ==> Sequence ==> Step
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/ConfigManager">Config
 *      Manager</A> (WIKI)
 */
public class LibSequenceConfigManager {

	protected final LibSequenceActionValidator actionValidator;

	protected final List<LibSequenceConfigSection> sections = new ArrayList<>();

	public LibSequenceConfigManager(LibSequenceOrchestrator orchestrator) {
		this.actionValidator = orchestrator.getActionManager().getActionValidator();
	}

	/**
	 * Check if a section exists for the given ownerToken
	 *
	 */
	public boolean hasSection(LibSequenceToken ownerToken) {
		for (LibSequenceConfigSection mySection : sections) {
			if (mySection.isOwner(ownerToken)) { // Is secure because isOwner is final
				return true;
			}
		}
		return false;
	}

	/**
	 * Find the section for a given ownerToken. Each section must have its own
	 * ownerToken, so the response is unique.
	 */
	public LibSequenceConfigSection findOwnSection(LibSequenceToken ownerToken) {
		for (LibSequenceConfigSection mySection : sections) {
			if (mySection.isOwner(ownerToken)) { // Is secure because isOwner is final
				return mySection;
			}
		}
		return null;
	}

	/**
	 * Check if the session identified by the ownerToken contains a sequence with
	 * the given name.
	 **/
	public boolean hasOwnSequence(LibSequenceToken ownerToken, String sequenceName) {
		LibSequenceConfigSection section = findOwnSection(ownerToken);
		if (section == null) {
			return false;
		}
		return section.hasSequence(sequenceName);
	}

	/**
	 * Get a sequence from the section identified by the ownerToken with the given
	 * name.
	 **/
	public LibSequenceConfigSequence getOwnSequence(LibSequenceToken ownerToken, String sequenceName)
			throws LibSequenceConfigException {
		LibSequenceConfigSection section = findOwnSection(ownerToken);
		if (section == null) {
			throw new LibSequenceConfigException(null, LSCERR_SECTION_NOT_FOUND, sequenceName);
		}
		return section.getSequence(sequenceName);
	}

	/**
	 * Find the sequence with the given securityToken. You need the securityToken if
	 * you want to access a sequence delivered by a foreign plugin.
	 */
	public LibSequenceConfigSequence findForeignSequence(LibSequenceToken securityToken) {
		for (LibSequenceConfigSection mySection : sections) {
			LibSequenceConfigSequence sequence = mySection.findSequence(securityToken);
			if (sequence != null) {
				return sequence;
			}
		}
		return null;
	}

	/**
	 * Get a list of all sequence-names contained in the section identified by the
	 * ownerToken.
	 **/
	public Set<String> getSequenceNames(LibSequenceToken ownerToken) throws LibSequenceConfigException {
		LibSequenceConfigSection section = findOwnSection(ownerToken);
		if (section == null) {
			throw new LibSequenceConfigException(null, LSCERR_SECTION_NOT_FOUND, null);
		}
		return section.getSequenceNames();
	}

	/**
	 * Add section to the config manager or reload it if it already exists. A
	 * section can contains one or more sequences Each section must have its unique
	 * ownerToken. This is an admin function so you must authenticate yourself by
	 * giving me the ownerToken object.
	 *
	 * @throws LibSequenceConfigException
	 **/
	public void setSection(LibSequenceToken ownerToken, LibSequenceConfigSection newSection)
			throws LibSequenceConfigException {
		if (newSection == null) {
			throw new LibSequenceConfigException(null, LSCERR_SECTION_GENERATION_ERROR, null);
		}
		newSection.validateSyntax();

		LibSequenceConfigSection oldSection = findOwnSection(ownerToken);
		if (oldSection != null) {
			sections.remove(oldSection);
		}
		sections.add(newSection);
	}

	/**
	 * Register a section without adding sequences to it. Normally a section is
	 * automatically created when you first call loadSection. But in some
	 * circumstances you need a section even if load fails or before load. So you
	 * have the option to make a "dummy" section.
	 */
	public void preregisterSection(LibSequenceToken ownerToken, String sectionName) {
		if (findOwnSection(ownerToken) != null) {
			return;
		}
		sections.add(new LibSequenceConfigSection(ownerToken, actionValidator, sectionName));
	}

	/**
	 * Removes a complete section including all of its sequences. This is an admin
	 * function so you must authenticate yourself by giving me the ownerToken
	 **/
	public void unregisterSection(LibSequenceToken ownerToken) throws LibSequenceConfigException {
		LibSequenceConfigSection sectionOld = findOwnSection(ownerToken);
		if (sectionOld == null) {
			throw new LibSequenceConfigException(null, LSCERR_SECTION_NOT_FOUND, null);
		}

		sections.remove(sectionOld);
	}

	/**
	 * Remove all sections on disable.
	 */
	public void disable() {
		sections.clear();
	}

}
