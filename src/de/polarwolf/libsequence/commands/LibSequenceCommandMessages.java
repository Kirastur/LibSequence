package de.polarwolf.libsequence.commands;

public enum LibSequenceCommandMessages {

	MSG_UNKNOWN_OPTION ("Unknown option. Use '/libsequence help' for a list of possible options."),
	MSG_UNKNOWN_SEQUENCE ("Unknown sequence. Use '/libsequence list' for a list of available sequences"),
	MSG_OPTION_NAME_MISSING ("Option needed. Use '/libsequence help' for a list of possible options."),
	MSG_SEQUENCE_NAME_MISSING ("Sequence name needed"),
	MSG_NO_OPTION_PERMISSION ("Sorry, you don't have the permission to use this option"),
	MSG_NO_SEQUENCE_PERMISSION ("Sorry, you don't have the right to access this sequence"),
	MSG_TOO_MANY_PARAMETERS ("Too many parameters"),
	MSG_NOT_RUNNING ("There are no running sequences of this type"),
	MSG_OPTION_FORBIDDEN ("You have no permission on any option"),
	MSG_SEQUENCE_FORBIDDEN ("You have no right on any sequence"),
	
	MSG_SEQUENCE_STARTED ("Sequence started"),
	MSG_SEQUENCE_CANCELLED ("Sequence cancelled"),
	MSG_RELOAD ("Reload successfull"),
	
	MSG_HELP ("Possible options are: ");
					
	private final String messageText;
	
	private LibSequenceCommandMessages(String messageText) {
		this.messageText=messageText;
	}
	
	public String defaultMessage() {
		return messageText;
	}

}

