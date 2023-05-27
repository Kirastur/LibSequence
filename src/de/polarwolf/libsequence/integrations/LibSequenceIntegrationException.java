package de.polarwolf.libsequence.integrations;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Gateway to 3rd party plugins
 *
 */
public class LibSequenceIntegrationException extends LibSequenceException {

	private static final long serialVersionUID = 1L;

	public LibSequenceIntegrationException(String contextName, String errorName, String errorDetailText) {
		super(contextName, errorName, errorDetailText);
	}

	public LibSequenceIntegrationException(String contextName, String errorName, String errorDetailText,
			Throwable cause) {
		super(contextName, errorName, errorDetailText, cause);
	}

	@Override
	public String getTitle() {
		return "Integration call error";
	}

}
