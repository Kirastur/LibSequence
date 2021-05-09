package de.polarwolf.libsequence.syntax;

import de.polarwolf.libsequence.exception.LibSequenceException;

public class LibSequenceSyntaxException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	protected final LibSequenceSyntaxErrors errorCode;


	public LibSequenceSyntaxException(String contextName, LibSequenceSyntaxErrors errorCode, String errorDetailText) {
		super(contextName, errorCode.toString(), errorDetailText);
		this.errorCode = errorCode;
	}
	

	public LibSequenceSyntaxException(String contextName, LibSequenceSyntaxErrors errorCode, String errorDetailText, Throwable cause) {
		super(contextName, errorCode.toString(), errorDetailText, cause);
		this.errorCode = errorCode;
	}
	

	@Override
	public String getTitle() {
		return "Syntax error";
	}


	public LibSequenceSyntaxErrors getErrorCode() {
		return errorCode;
	}

}
