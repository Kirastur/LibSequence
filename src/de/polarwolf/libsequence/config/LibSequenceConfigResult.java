package de.polarwolf.libsequence.config;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;

// This class is expected to be final
// Please don't change it, even if you implement a custom ConfigManager
// You can do formatting and localization of the Error-Text in the callback object

public final class LibSequenceConfigResult {	

	public final String sequenceName;
	public final int stepNr;
	public final LibSequenceConfigErrors errorCode;
	public final String errorSubText;
	public final LibSequenceActionResult actionResult;

	public LibSequenceConfigResult(String sequenceName, int stepNr, LibSequenceConfigErrors errorCode, String errorSubText, LibSequenceActionResult actionResult) {
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;
		this.errorCode=errorCode;
		this.errorSubText=errorSubText;
		this.actionResult=actionResult;
	}

	public boolean hasError() {
		return errorCode!=LSCERR_OK;
	}

	public String getTextPrefix() {
		String textPrefix = "";
		if ((sequenceName!=null) && (sequenceName.isEmpty())) {
			textPrefix = "Sequence "+sequenceName;
		} 
		if (stepNr > 0) {
			if (!textPrefix.isEmpty()) {
				textPrefix = textPrefix + " ";
			}
			textPrefix = textPrefix +"Step "+stepNr;
		}
		return textPrefix;
	}

	public String getErrorText() {
		String errorText = errorCode.toString();
		if ((errorCode==LSCERR_ACTION) && (actionResult!=null)) {
			errorText = actionResult.toString();
		}
		return errorText;
	}

	@Override
	public String toString() {
		String s = getTextPrefix();
		if (!s.isEmpty() ) {
			s = s + ": ";
		}
		s = s + getErrorText();
		if ((errorSubText!=null) && (!errorSubText.isEmpty())) {
			s = s + ": " + errorSubText;
		}
		return 	s;		
	}
}
