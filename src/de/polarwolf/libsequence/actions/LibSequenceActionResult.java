package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

//This class is expected to be final
//Please don't change it, even if you implement a custom ActionManager
//You can formatting and localization of the Error-Text in the callback object
public final class LibSequenceActionResult {
	
	public final String sequenceName;
	public final String actionName;
	public final LibSequenceActionErrors errorCode; 
	public final String errorSubText;

	public LibSequenceActionResult(String sequenceName, String actionName, LibSequenceActionErrors errorCode,  String errorSubText) {
		this.sequenceName=sequenceName;
		this.actionName=actionName;
		this.errorCode=errorCode;
		this.errorSubText=errorSubText;
	}

	public Boolean hasError() {
		return errorCode!=LSAERR_OK;
	}

	@Override
	public String toString() {
		String s = actionName;
		if (s==null) {
			s = "";
		} else {
			s = s + ": ";
		}
		s = s + errorCode.toString();
		if (errorSubText!=null) {
			s = s + ": " + errorSubText; 
		}
		if ((sequenceName != null) && (!sequenceName.isEmpty())) {
			s = s + " (" + sequenceName + ")";
		}
		return s;
	}

}
