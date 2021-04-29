package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.*;

import de.polarwolf.libsequence.result.LibSequenceResult;

//This class is expected to be final
//Please don't change it, even if you implement a custom ActionManager
//You can formatting and localization of the Error-Text in the callback object
public final class LibSequenceActionResult extends LibSequenceResult{
	
	public final String sequenceName;
	public final String actionName;
	public final LibSequenceActionErrors errorCode; 
	public final String errorDetailText;
	

	public LibSequenceActionResult(String sequenceName, String actionName, LibSequenceActionErrors errorCode,  String errorDetailText, LibSequenceResult subResult) {
		super(subResult);
		this.sequenceName=sequenceName;
		this.actionName=actionName;
		this.errorCode=errorCode;
		this.errorDetailText=errorDetailText;
	}


	@Override
	public boolean hasError() {
		return errorCode!=LSAERR_OK;
	}
	

	@Override
	public String getLabel() {
		return errorCode.toString();
	}
	

	@Override
	protected String getErrorText() {
		
		String errorText = getLabel();

		if ((actionName != null) && (!actionName.isEmpty())) {
			errorText = actionName + ": " + errorText; 
		}

		if ((sequenceName != null) && (!sequenceName.isEmpty())) {
			errorText = sequenceName + ": " + errorText;
		}

		if ((errorDetailText != null) && (!errorDetailText.isEmpty())) {
			errorText = errorText + ": " +  errorDetailText; 
		}

		return errorText; 
	}

}
