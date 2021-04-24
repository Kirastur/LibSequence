package de.polarwolf.libsequence.checks;

public enum LibSequenceCheckErrors {
	
	LSCERR_OK ("OK"),
	LSCERR_FALSE ("check returns FALSE"),
	LSCERR_CHECK_NOT_FOUND ("Check not found"),
	LSCERR_CHECK_ALREADY_EXISTS ("Check already exists"),
	LSCERR_USER_DEFINED_ERROR ("User defined error"),
	LSCERR_SYNTAX_ERROR ("Syntax error"),
	LSCERR_NOT_NUMERIC ("Value is not numeric"),
	LSCERR_VALUE_MISSING ("Value is missing"),
	LSCERR_NOT_A_PLAYER ("Initiator must be a player");

	private final String errorText;
	
	private LibSequenceCheckErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}


}
