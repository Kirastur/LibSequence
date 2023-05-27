package de.polarwolf.libsequence.exception;

/**
 * Generic definition of exceptions thrown by this library
 *
 */
public abstract class LibSequenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String JAVA_EXCEPTION = "Java Exception";

	private final String contextName;
	private final String errorName;
	private final String errorDetailText;

	protected LibSequenceException(String contextName, String errorName, String errorDetailText) {
		super(buildMessage(contextName, errorName, errorDetailText), null, false, false);
		this.contextName = contextName;
		this.errorName = errorName;
		this.errorDetailText = errorDetailText;
	}

	protected LibSequenceException(String contextName, String errorName, String errorDetailText, Throwable cause) {
		super(buildMessage(contextName, errorName, errorDetailText), cause, false, false);
		this.contextName = contextName;
		this.errorName = errorName;
		this.errorDetailText = errorDetailText;
	}

	public abstract String getTitle();

	public String getContextName() {
		return contextName;
	}

	public String getErrorName() {
		return errorName;
	}

	public String getErrorDetailText() {
		return errorDetailText;
	}

	protected static String buildMessage(String contextName, String errorName, String errorDetailText) {
		String message = errorName;

		if ((contextName != null) && (!contextName.isEmpty())) {
			message = contextName + ": " + message;
		}

		if ((errorDetailText != null) && (!errorDetailText.isEmpty())) {
			message = message + ": " + errorDetailText;
		}

		return message;
	}

	public String getMessageCascade() {
		String message = getMessage();
		String causeMessage = null;

		Throwable t = getCause();
		if (t instanceof LibSequenceException e) {
			causeMessage = e.getMessageCascade();
		} else if (t != null) {
			causeMessage = t.getMessage();
		}

		if ((causeMessage != null) && (!causeMessage.isEmpty())) {
			message = message + " >>> " + causeMessage;
		}

		return message;
	}

	public boolean hasJavaException() {
		Throwable t = getCause();
		if (t instanceof LibSequenceException e) {
			return e.hasJavaException();
		}
		return (t != null);
	}

}
