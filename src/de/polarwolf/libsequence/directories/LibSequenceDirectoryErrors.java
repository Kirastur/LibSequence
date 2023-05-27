package de.polarwolf.libsequence.directories;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Enum of directory-specific Exception error codes
 *
 */
public enum LibSequenceDirectoryErrors {

	LSDERR_JAVA_EXCEPTION(LibSequenceException.JAVA_EXCEPTION),
	LSDERR_USER_DEFINED_ERROR("User defined error"),
	LSDERR_TOKEN_IS_NULL("Token cannot be null"),
	LSDERR_NAME_IS_EMPTY("Sequence Name cannot be empty"),
	LSDERR_OWNERTOKEN_NOT_REGISTERED("OwnerToken not registered as a provider"),
	LSDERR_CONFIG_HAS_GONE("The config section for the provider has gone"),
	LSDERR_SEQUENCE_ALREADY_REGISTERED("Sequence Name already registered for another plugin"),
	LSDERR_OWNERTOKEN_IN_USE(
			"The ownerToken is already in use. You can register only one ConfigSection per ownerToken"),
	LSDERR_ONLY_OWNER_CAN_REMOVE("Only the owner can remove a sequence"),
	LSDERR_SEQUENCE_NOT_FOUND("Sequence not found"),
	LSDERR_SEQUENCE_NOT_RUNNABLE("Sequence not runnable"),
	LSDERR_ERROR_DURING_SEQUENCE_START("Error during sequence start"),
	LSDERR_ACCESS_DENIED("Access denied"),
	LSDERR_ERROR_DURING_SEQUENCE_LOAD("Error loading sequences"),
	LSDERR_DISABLED("LibSequence Directory instance is shut down")
;

	private final String errorText;

	private LibSequenceDirectoryErrors(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}
}
