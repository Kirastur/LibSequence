package de.polarwolf.libsequence.config;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Enum of config-specific Exception error codes
 *
 */
public enum LibSequenceConfigErrors {

	LSCERR_JAVA_EXCEPTION(LibSequenceException.JAVA_EXCEPTION),
	LSCERR_USER_DEFINED_ERROR("User defined error"),
	LSCERR_ACTION("Error within action"),
	LSCERR_KEY_SYNTAX_ERROR("Syntax error in key"),
	LSCERR_MISSING_ACTION("Action is missing or empty"),
	LSCERR_WAIT_NOT_NUMERIC("Wait is not numeric or not in range"),
	LSCERR_STEP_ENUM("Error in step enumeration"),
	LSCERR_SEQUENCE("Cannot identify sequence"),
	LSCERR_SECTION_NOT_FOUND("Section not found"),
	LSCERR_SECTION_ALREADY_EXISTS("Section already exists"),
	LSCERR_NO_CONFIGSECTION("Section not found in configfile or YAML syntax error"),
	LSCERR_NO_CONFIGFILE("File not found"),
	LSCERR_SECTION_GENERATION_ERROR("Cannot get section generation data"),
	LSCERR_NOT_AUTHORIZED("Unauthorized access");

	private final String errorText;

	private LibSequenceConfigErrors(String errorText) {
		this.errorText = errorText;
	}

	@Override
	public String toString() {
		return errorText;
	}

}
