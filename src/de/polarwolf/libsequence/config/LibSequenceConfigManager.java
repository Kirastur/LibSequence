package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// The Manager is the controlling instance
// There is only one Manager running except you are using private sequencers
// It acts as an interface between the sequences and the API

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.callback.LibSequenceCallback;

public class LibSequenceConfigManager {
	
	protected final LibSequenceActionValidator actionValidator;
	
	protected final Set<LibSequenceConfigSection> sections = new HashSet<>();
	

	public LibSequenceConfigManager(LibSequenceActionValidator actionValidator) {
		this.actionValidator=actionValidator;
	}
	

	// Find the section for a given callback
	// Each section must have its own callback, so the response is unique
	// Calling section.verifyAccess(config) is secure because this method is final 
	protected final LibSequenceConfigSection findSection(LibSequenceCallback callback) {
		for (LibSequenceConfigSection section : sections) {
			if (section.hasAccess(callback)) {
				return section;
			}
		}
		return null;		
	}
	

	public LibSequenceConfigSequence findForeignSequence(String securityToken) {
		for (LibSequenceConfigSection section : sections) {
			LibSequenceConfigSequence sequence=section.findSequence(securityToken);
			if (sequence!=null) {
				return sequence;
			}
		}
		return null;		
	}
	
	
	public boolean hasOwnSequence(LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSection section = findSection(callback);
		if (section == null) {
			return false;
		}
		return section.hasSequence(sequenceName);
	}

	
	public LibSequenceConfigSequence getOwnSequence(LibSequenceCallback callback, String sequenceName) throws LibSequenceConfigException {
		LibSequenceConfigSection section = findSection(callback);
		if (section == null) {
			throw new LibSequenceConfigException(LSCERR_SECTION_NOT_FOUND, null);
		}
		return section.getSequence(sequenceName);
	}
	

	public Set<String> getSequenceNames (LibSequenceCallback callback) throws LibSequenceConfigException {
		LibSequenceConfigSection section = findSection(callback);
		if (section==null) {
			throw new LibSequenceConfigException(LSCERR_SECTION_NOT_FOUND, null);
		}
		return section.getSequenceNames();
	}
			

	// Add or reload a section to the config manager
	// A section can contains one or more sequences
	// Each section must have its unique callback
	// This is a admin function so you must authenticate yourself by giving me the callback object 
	public void loadSection(LibSequenceCallback callback) throws LibSequenceConfigException {
		LibSequenceConfigSection oldSection = findSection(callback);
		
		LibSequenceConfigSection newSection=callback.createConfigSection(actionValidator);
		if (newSection==null) {
			throw new LibSequenceConfigException(LSCERR_SECTION_GENERATION_ERROR, null);
		}

		newSection.validateSyntax();

		if (oldSection != null) {
			sections.remove(oldSection);
		}

		sections.add(newSection);
	}
	

	// Removes a complete section including all of their sequences
	// This is a admin function so you must authenticate yourself by giving me the callback object 
	public void removeSection(LibSequenceCallback callback) throws LibSequenceConfigException {
		LibSequenceConfigSection sectionOld = findSection(callback);
		if (sectionOld==null) {
			throw new LibSequenceConfigException(LSCERR_SECTION_NOT_FOUND, null);
		}

		sections.remove(sectionOld);
	}
	
}
