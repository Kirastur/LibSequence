package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import de.polarwolf.libsequence.result.LibSequenceResult;

public class LibSequenceCheckResult extends LibSequenceResult {


	public final String checkName;
	public final LibSequenceCheckErrors errorCode;
	public final String errorDetailText;

	public LibSequenceCheckResult(String checkName, LibSequenceCheckErrors errorCode, String errorDetailText) {
		super(null);
		this.checkName=checkName;
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

		if ((checkName != null) && (!checkName.isEmpty())) {
			errorText = checkName + ": " + errorText; 
		}

		if ((errorDetailText != null) && (!errorDetailText.isEmpty())) {
			errorText = errorText + ": " +  errorDetailText; 
		}

		return errorText; 
	}
		
}
