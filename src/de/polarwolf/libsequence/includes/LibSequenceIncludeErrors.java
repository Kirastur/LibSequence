package de.polarwolf.libsequence.includes;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Enum of include-specific Exception error codes
 *
 */
public enum LibSequenceIncludeErrors {

	LSIERR_JAVA_EXCEPTION(LibSequenceException.JAVA_EXCEPTION),
	LSIERR_USER_DEFINED_ERROR("User defined error"),
	LSIERR_INCLUDE_NOT_FOUND("Include not found"),
	LSIERR_INCLUDE_ALREADY_EXISTS("Include already exists"),
	LSIERR_SYNTAX_ERROR("Syntax error"),
	LSIERR_NO_INITIATOR("No initiator given"),
	LSIERR_VALUE_MISSING("Value is missing");

	private final String errorText;

	private LibSequenceIncludeErrors(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}

}
