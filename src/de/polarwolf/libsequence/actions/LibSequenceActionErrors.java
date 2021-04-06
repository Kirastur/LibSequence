package de.polarwolf.libsequence.actions;

public enum LibSequenceActionErrors {

	LSAERR_OK ("OK"),
	LSAERR_USER_DEFINED_ERROR ("User defined error"),
	LSAERR_ACTION_NOT_FOUND ("Action not found"),
	LSAERR_ACTION_ALREADY_EXISTS ("Action already exists"),
	LSAERR_MISSING_ATTRIBUTE ("Required attribute is missing"),
	LSAERR_WRONG_INSTANCE ("Step belongs to another instance"),
	LSAERR_NOT_AUTHORIZED ("Action is not authorizied"),
	LSAERR_EXCEPTION ("Exception caught"),
	LSAERR_UNKNOWN_VALUE ("Unknown attribute value");

	private final String errorText;
	
	private LibSequenceActionErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}

}

