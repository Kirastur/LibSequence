package de.polarwolf.libsequence.exception;

/**
 * Report a general java error without belonging to one of the Managers
 *
 */
public class LibSequenceExceptionJava extends LibSequenceException {

	private static final long serialVersionUID = 1L;

	public LibSequenceExceptionJava(String contextName, Throwable cause) {
		super(contextName, LibSequenceException.JAVA_EXCEPTION, null, cause);
	}

	@Override
	public String getTitle() {
		return "Jaba exception";
	}

}
