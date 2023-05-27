package de.polarwolf.libsequence.actions;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Exception thrown during syntax verify or execution
 *
 */
public class LibSequenceActionException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	private final LibSequenceActionErrors errorCode;

	public LibSequenceActionException(String contextName, LibSequenceActionErrors errorCode, String errorDetailText) {
		super(contextName, errorCode.toString(), errorDetailText);
		this.errorCode = errorCode;
	}

	public LibSequenceActionException(String contextName, LibSequenceException cause) {
		super(contextName, cause.getTitle(), null, cause);
		this.errorCode = null;
	}

	public LibSequenceActionException(String contextName, LibSequenceActionErrors errorCode, String errorDetailText,
			Throwable cause) {
		super(contextName, errorCode.toString(), errorDetailText, cause);
		this.errorCode = errorCode;
	}

	@Override
	public String getTitle() {
		return "Action error";
	}

	public LibSequenceActionErrors getErrorCode() {
		return errorCode;
	}

}
