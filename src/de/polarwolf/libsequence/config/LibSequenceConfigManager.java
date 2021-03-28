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
			if (section.verifyAccess(callback)) {
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
	
	public LibSequenceConfigSequence findOwnSequence(LibSequenceCallback callback, String sequenceName) {
		LibSequenceConfigSection section = findSection(callback);
		if(section==null) {
			return null;
		}
		LibSequenceConfigSequence sequence = section.getSequence(sequenceName);
		if (sequence==null) {
			return null;
		}
		return sequence;
	}
	
	public Set<String> getSequenceNames (LibSequenceCallback callback) {
		LibSequenceConfigSection section = findSection(callback);
		if (section==null) {
			return new HashSet<>();
		}
		return section.getSequenceKeys();
	}
			

	// Add a section to the config manager
	// A section can contains one or more sequences
	// Each section must have its unique callback
	public LibSequenceConfigResult addSection(LibSequenceCallback callback) {
		LibSequenceConfigSection oldSection = findSection(callback);
		if (oldSection!=null) {
			return new LibSequenceConfigResult(null, 0, LSCERR_SECTION_ALREADY_EXISTS, null, null);
		}
		LibSequenceConfigSection sectionNew=callback.createConfigSection(actionValidator);
		if (sectionNew==null) {
			return new LibSequenceConfigResult(null, 0, LSCERR_SECTION_GENERATION_ERROR, null, null);
		}
		LibSequenceConfigResult result = sectionNew.checkSyntax();
		if (result.hasError()) {
			return result;
		}
		sections.add(sectionNew);
		return new LibSequenceConfigResult(null, 0, LSCERR_OK, null, null);
	}
	
	// Reload a complete section including all of their sequences
	// This is a admin function so you must authenticate yourself by giving me the callback object 
	public LibSequenceConfigResult reloadSection(LibSequenceCallback callback) {
		LibSequenceConfigSection sectionOld = findSection(callback);
		if (sectionOld==null) {
			return new LibSequenceConfigResult(null, 0, LSCERR_SECTION_NOT_FOUND, null, null);
		}
		LibSequenceConfigSection sectionNew=callback.createConfigSection(actionValidator);
		if (sectionNew==null) {
			return new LibSequenceConfigResult(null, 0, LSCERR_SECTION_GENERATION_ERROR, null, null);
		}
		LibSequenceConfigResult result = sectionNew.checkSyntax();
		if (result.hasError()) {
			return result;
		}
		sections.remove(sectionOld);
		sections.add(sectionNew);
		return new LibSequenceConfigResult(null, 0, LSCERR_OK, null, null);
	}
		
	// Removes a complete section including all of their sequences
	// This is a admin function so you must authenticate yourself by giving me the callback object 
	public LibSequenceConfigResult removeSection(LibSequenceCallback callback) {
		LibSequenceConfigSection sectionOld = findSection(callback);
		if (sectionOld==null) {
			return  new LibSequenceConfigResult(null, 0, LSCERR_SECTION_NOT_FOUND, null, null);
		}
		sections.remove(sectionOld);
		return new LibSequenceConfigResult(null, 0, LSCERR_OK, null, null);
	}
	
}
