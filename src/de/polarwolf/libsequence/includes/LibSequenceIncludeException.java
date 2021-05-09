package de.polarwolf.libsequence.includes;

import de.polarwolf.libsequence.exception.LibSequenceException;

public class LibSequenceIncludeException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	protected final LibSequenceIncludeErrors errorCode;

	public LibSequenceIncludeException(String contextName, LibSequenceIncludeErrors errorCode, String errorDetailText) {
		super(contextName, errorCode.toString(), errorDetailText);
		this.errorCode = errorCode;
	}
	

	public LibSequenceIncludeException(String contextName, LibSequenceException cause) {
		super(contextName, cause.getTitle(), null, cause);
		this.errorCode = null;
	}

	
	public LibSequenceIncludeException(String contextName, LibSequenceIncludeErrors errorCode, String errorDetailText, Throwable cause) {
		super(contextName, errorCode.toString(), errorDetailText, cause);
		this.errorCode = errorCode;
	}


	@Override
	public String getTitle() {
		return "Include CommandSender error";
	}


	public LibSequenceIncludeErrors getErrorCode() {
		return errorCode;
	}

}
