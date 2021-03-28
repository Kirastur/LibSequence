package de.polarwolf.libsequence.config;

public enum LibSequenceConfigErrors {
	
	LSCERR_OK ("OK"),
	LSCERR_USER_DEFINED_ERROR ("User defined error"),
	LSCERR_ACTION ("Unspecified error within action"),
	LSCERR_KEY_SYNTAX_ERROR ("Syntax error in key"),
	LSCERR_MISSING_ACTION ("Action is missing or empty"),
	LSCERR_WAIT_NOT_NUMERIC ("Wait is not numeric or not in range"),
	LSCERR_STEP_ENUM ("Error in step enumeration"),
	LSCERR_SEQUENCE ("Cannot identify sequence"),
	LSCERR_SECTION_NOT_FOUND ("Section not found"),
	LSCERR_SECTION_ALREADY_EXISTS ("Section already exists"),
	LSCERR_SECTION_GENERATION_ERROR ("Cannot get section generation data");
	
	private final String errorText;
	
	private LibSequenceConfigErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}

}
