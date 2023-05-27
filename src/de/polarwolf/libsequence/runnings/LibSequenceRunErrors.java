package de.polarwolf.libsequence.runnings;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Enum of Exception error codes dedicated to sequence execution
 *
 */
public enum LibSequenceRunErrors {

	LSRERR_JAVA_EXCEPTION(LibSequenceException.JAVA_EXCEPTION),
	LSRERR_USER_DEFINED_ERROR("User defined error"),
	LSRERR_NOT_FOUND("Sequence not found"),
	LSRERR_NOT_AUTHORIZED("Not authorized to run sequence"),
	LSRERR_TOO_MANY("There are too many sequences running at the same time"),
	LSRERR_CONFIG("There are errors in the sequence config"),
	LSRERR_NOT_RUNNING("The requested sequence is not running"),
	LSRERR_SINGLETON_RUNNING("The singeton sequence is already running"),
	LSRERR_ACTION_AUTH_FAILED("At least one action is not authorizing us"),
	LSRERR_CHAIN_RESOLVE_FAILED("Failed resolving chains"),
	LSRERR_DISABLED("LibSequence instance is shut down");

	private final String errorText;

	private LibSequenceRunErrors(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}

}
