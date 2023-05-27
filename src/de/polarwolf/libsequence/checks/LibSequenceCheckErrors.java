package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Enum of check-specific Exception error codes
 *
 */
public enum LibSequenceCheckErrors {

	LSKERR_JAVA_EXCEPTION(LibSequenceException.JAVA_EXCEPTION),
	LSKERR_USER_DEFINED_ERROR("User defined error"),
	LSKERR_CHECK_NOT_FOUND("Check not found"),
	LSKERR_CHECK_ALREADY_EXISTS("Check already exists"),
	LSKERR_SYNTAX_ERROR("Syntax error"),
	LSKERR_NO_INITIATOR("No initiator given"),
	LSKERR_NOT_A_PLAYER("Initiator must be a player"),
	LSKERR_VALUE_MISSING("Value is missing"),
	LSKERR_NOT_NUMERIC("Value is not numeric");

	private final String errorText;

	private LibSequenceCheckErrors(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}

}
