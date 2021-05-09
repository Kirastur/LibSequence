package de.polarwolf.libsequence.checks;

import de.polarwolf.libsequence.exception.LibSequenceException;

public class LibSequenceCheckException extends LibSequenceException {

	private static final long serialVersionUID = 1L;

	protected final LibSequenceCheckErrors errorCode;

	
	public LibSequenceCheckException(String contextName, LibSequenceCheckErrors errorCode, String errorDetailText) {
		super(contextName, errorCode.toString(), errorDetailText);
		this.errorCode = errorCode;
	}
	

	public LibSequenceCheckException(String contextName, LibSequenceException cause) {
		super(contextName, cause.getTitle(), null, cause);
		this.errorCode = null;
	}

	
	public LibSequenceCheckException(String contextName, LibSequenceCheckErrors errorCode, String errorDetailText, Throwable cause) {
		super(contextName, errorCode.toString(), errorDetailText, cause);
		this.errorCode = errorCode;
	}
	
	
	@Override
	public String getTitle() {
		return "Check error";
	}


	public LibSequenceCheckErrors getErrorCode() {
		return errorCode;
	}

}
