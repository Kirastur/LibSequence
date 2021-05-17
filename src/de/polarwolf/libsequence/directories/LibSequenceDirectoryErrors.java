package de.polarwolf.libsequence.directories;

import de.polarwolf.libsequence.exception.LibSequenceException;

public enum LibSequenceDirectoryErrors {

	LSDERR_JAVA_EXCEPTION (LibSequenceException.JAVA_EXCEPTION),
	LSDERR_USER_DEFINED_ERROR ("User defined error"),
	LSDERR_CALLBACK_IS_NULL("Callback cannot be null"),
	LSDERR_NAME_IS_EMPTY("Sequence Name cannot be empty"),
	LSDERR_CALLBACK_NOT_REGISTERED("Callback not registered as a provider"),
	LSDERR_SEQUENCE_ALREADY_REGISTERED("Sequence Name already registered for another plugin"),
	LSDERR_ONLY_OWNER_CAN_REMOVE("Only the owner can remove a sequence"),
	LSDERR_FAILED_GETTING_NAMES("Faild to get sequence names"),
	LSDERR_SEQUENCE_NOT_FOUND("Sequence not found"),
	LSDERR_SEQUENCE_NOT_RUNNABLE("Sequence not runnable"),
	LSDERR_ERROR_DURING_SEQUENCE_START("Error during sequence start"),
	LSDERR_ACCESS_DENIED("Access denied"),
	LSDERR_ERROR_DURING_SEQUENCE_LOAD("Error loading sequences");

	private final String errorText;
	
	private LibSequenceDirectoryErrors(String errorText) {
		this.errorText=errorText;
	}
	
	@Override
	public String toString() {
		return errorText;
	}
}
