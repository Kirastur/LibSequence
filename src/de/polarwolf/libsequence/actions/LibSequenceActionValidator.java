package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;

// This class is supposed to be final
// enter you custom syntaxCheck into runManager
public final class LibSequenceActionValidator {

	private final LibSequenceActionManager actionManager;
	

	public LibSequenceActionValidator(LibSequenceActionManager actionManager) {
		this.actionManager=actionManager;
	}
	

	public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceActionException {
		actionManager.validateSyntax(configStep);
	}
	

	public boolean isSameInstance(LibSequenceActionValidator actionValidatorToCompare) {
		return (actionValidatorToCompare.actionManager == this.actionManager);
	}

}
