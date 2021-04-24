package de.polarwolf.libsequence.config;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.*;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.result.LibSequenceResult;

// This class is expected to be final
// Please don't change it, even if you implement a custom ConfigManager
// You can do formatting and localization of the Error-Text in the callback object

public final class LibSequenceConfigResult extends LibSequenceResult{	

	public final String sequenceName;
	public final int stepNr;
	public final LibSequenceConfigErrors errorCode;
	public final String errorDetailText;

	public LibSequenceConfigResult(String sequenceName, int stepNr, LibSequenceConfigErrors errorCode, String errorDetailText, LibSequenceActionResult actionResult) {
		super(actionResult);
		this.sequenceName=sequenceName;
		this.stepNr=stepNr;
		this.errorCode=errorCode;
		this.errorDetailText=errorDetailText;
	}

	@Override
	public boolean hasError() {
		return errorCode!=LSCERR_OK;
	}
	
	@Override
	public String getLabel() {
		return errorCode.toString();
	}
	
	@Override
	protected String getErrorText() {

		String errorText = getLabel();
		
		if (stepNr > 0) {
			errorText = "Step " + Integer.toString(stepNr) + ": " + errorText; 
		}
		
		if ((sequenceName != null) && (!sequenceName.isEmpty())) {
			errorText = sequenceName + ": " + errorText;
		}
		
		if ((errorDetailText != null) && (!errorDetailText.isEmpty())) {
			errorText = errorText + ": " + errorDetailText;
		}
		
		return errorText;
	}
		
}
