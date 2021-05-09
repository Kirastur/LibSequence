package de.polarwolf.libsequence.syntax;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.actions.LibSequenceAction;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;

import static de.polarwolf.libsequence.syntax.LibSequenceSyntaxErrors.*;

public class LibSequenceSyntaxManager {
	
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	
	
	public LibSequenceSyntaxManager(LibSequenceCheckManager checkManager, LibSequenceIncludeManager includeManager) {
		this.checkManager = checkManager;
		this.includeManager = includeManager;
	}
	
	
	protected Set<String> getRequiredAttributes(LibSequenceAction action) {
		Set<String> actionAttributes = action.getRequiredAttributes();
		if (actionAttributes == null)  {
			actionAttributes = new HashSet<>();
		}
		return actionAttributes;		
		
	}
	
	protected Set<String> getOptionalAttributes(LibSequenceAction action) {
		Set<String> optionalAttributes = new HashSet<>();
		optionalAttributes.add(LibSequenceConfigStep.KEYNAME_ACTION);
		optionalAttributes.add(LibSequenceConfigStep.KEYNAME_WAIT);

		if (action.hasCheck()) {
			optionalAttributes.addAll(checkManager.getCheckNames());
		}

		if (action.hasInclude()) {
			optionalAttributes.addAll(includeManager.getIncludeNames());
		}

		Set<String> actionAttributes = action.getOptionalAttributes();
		if (actionAttributes != null)  {
			optionalAttributes.addAll(actionAttributes);
		}

		return optionalAttributes;
	}
		
	public void performAttributeVerification(LibSequenceAction action, LibSequenceConfigStep configStep) throws LibSequenceSyntaxException {
		Set<String> requiredAttributes = getRequiredAttributes(action);
		Set<String> optionalAttributes = getOptionalAttributes(action);
		
		// First Step: Check if all required attributes are present
		// You can ignore Multilanguage, because the base attribute must be present
		for (String keyName : requiredAttributes) {
			if (configStep.findValue(keyName) == null) {
				throw new LibSequenceSyntaxException(keyName, LSYERR_REQUIRED_ATTRIBUTE_MISSING, null);
			}
		}
		
		// Second step: Check if a step attribute is not contained in optional
		optionalAttributes.addAll(requiredAttributes);
		for (String keyName : configStep.getAttributeKeys()) {
			boolean isFound = false;
			for (String optName : optionalAttributes) {
				if (configStep.findValue(keyName).isEmpty()) {
					throw new LibSequenceSyntaxException(keyName, LSYERR_VALUE_MISSING, null);
				}
				if (keyName.startsWith(optName)) {
					isFound = true;
				}
			}
			if (!isFound) {
				throw new LibSequenceSyntaxException(keyName, LSYERR_UNKONWN_ATTRIBUTE, null);
			}
		}

	}

}
