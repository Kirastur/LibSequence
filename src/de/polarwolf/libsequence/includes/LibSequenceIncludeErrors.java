package de.polarwolf.libsequence.includes;

public enum LibSequenceIncludeErrors {
	
	LSIERR_OK ("OK"),
	LSIERR_USER_DEFINED_ERROR ("User defined error"),
	LSIERR_INCLUDE_NOT_FOUND ("Include not found"),
	LSIERR_INCLUDE_ALREADY_EXISTS ("Include already exists"),
	LSIERR_SYNTAX_ERROR ("Syntax error"),
	LSIERR_VALUE_MISSING ("Value is missing");

	private final String errorText;
	
	private LibSequenceIncludeErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}

}
