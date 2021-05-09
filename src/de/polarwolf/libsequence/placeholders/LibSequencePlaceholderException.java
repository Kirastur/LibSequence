package de.polarwolf.libsequence.placeholders;

import de.polarwolf.libsequence.exception.LibSequenceException;

public class LibSequencePlaceholderException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	protected final String playerName;
	protected final String messageText; 


	public LibSequencePlaceholderException(String contextName, String errorName, String playerName, String messageText) {
		super(contextName, errorName, buildErrorDetailText(playerName, messageText));
		this.playerName = playerName;
		this.messageText = messageText;
	}
	

	public LibSequencePlaceholderException(String contextName, String errorName, String playerName, String messageText, Throwable cause) {
		super(contextName, errorName, buildErrorDetailText(playerName, messageText), cause);
		this.playerName = playerName;
		this.messageText = messageText;
	}
	

	@Override
	public String getTitle() {
		return "Placeholder replacement error";
	}

	
	public String getPlayerName() {
		return playerName;
	}


	public String getMessageText() {
		return messageText;
	}


	protected static String buildErrorDetailText(String playerName, String messageText) {
		if (messageText == null) {
			messageText = "";
		}
		messageText = "\"" + messageText + "\"";
		if (playerName == null) {
			return messageText;
		}
		return playerName + ": " + messageText;
	}

}
