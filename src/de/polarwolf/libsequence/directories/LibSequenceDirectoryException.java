package de.polarwolf.libsequence.directories;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Exception thrown during directory operation
 *
 */
public class LibSequenceDirectoryException extends LibSequenceException {

	private static final long serialVersionUID = 1L;

	protected final LibSequenceDirectoryErrors errorCode;

	public LibSequenceDirectoryException(String contextName, LibSequenceDirectoryErrors errorCode,
			String errorDetailText) {
		super(contextName, errorCode.toString(), errorDetailText);
		this.errorCode = errorCode;
	}

	public LibSequenceDirectoryException(String contextName, LibSequenceDirectoryErrors errorCode,
			String errorDetailText, Throwable cause) {
		super(contextName, errorCode.toString(), errorDetailText, cause);
		this.errorCode = errorCode;
	}

	@Override
	public String getTitle() {
		return "Directory error";
	}

}
