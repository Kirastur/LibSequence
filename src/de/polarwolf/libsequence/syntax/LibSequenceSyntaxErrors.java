package de.polarwolf.libsequence.syntax;

import de.polarwolf.libsequence.exception.LibSequenceException;

public enum LibSequenceSyntaxErrors {
	
	LSYERR_JAVA_EXCEPTION (LibSequenceException.JAVA_EXCEPTION),
	LSYERR_USER_DEFINED_ERROR ("User defined error"),
	LSYERR_UNKONWN_ATTRIBUTE ("Unknown attribute"),
	LSYERR_REQUIRED_ATTRIBUTE_MISSING ("Required attribute is missing"),
	LSYERR_VALUE_MISSING ("Value is missing");

	private final String errorText;
	
	private LibSequenceSyntaxErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}

}
