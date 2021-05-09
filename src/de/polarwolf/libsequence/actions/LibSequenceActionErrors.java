package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.exception.LibSequenceException;

public enum LibSequenceActionErrors {

	LSAERR_JAVA_EXCEPTION (LibSequenceException.JAVA_EXCEPTION),
	LSAERR_USER_DEFINED_ERROR ("User defined error"),
	LSAERR_ACTION_NOT_FOUND ("Action not found"),
	LSAERR_ACTION_ALREADY_EXISTS ("Action already exists"),
	LSAERR_SYNTAX_ERROR ("Syntax error"),
	LSAERR_WRONG_INSTANCE ("Step belongs to another instance"),
	LSAERR_NOT_AUTHORIZED ("Action is not authorizied"),
	LSAERR_UNKNOWN_VALUE ("Unknown value");

	private final String errorText;
	
	private LibSequenceActionErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}

}

