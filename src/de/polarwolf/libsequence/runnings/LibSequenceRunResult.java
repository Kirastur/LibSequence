package de.polarwolf.libsequence.runnings;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.result.LibSequenceResult;

// This class is expected to be final
// Please don't change it, even if you implement a custom RunManager
// You can formatting and localization of the Error-Text in the callback object

public final class LibSequenceRunResult extends LibSequenceResult{

	public final LibSequenceRunningSequence newSequence;
	public final String sequenceName;
	public final LibSequenceRunErrors errorCode;

	public LibSequenceRunResult(LibSequenceRunningSequence newSequence,  String sequenceName, LibSequenceRunErrors errorCode, LibSequenceConfigResult configResult) {
		super(configResult);
		this.newSequence=newSequence;
		this.sequenceName=sequenceName;
		this.errorCode=errorCode;
	}

	@Override
	public boolean hasError() {
		return errorCode!=LSRERR_OK;
	}
	
	@Override
	public String getLabel() {
		return errorCode.toString();
	}
	
	@Override
	protected String getErrorText() {

		String errorText = errorCode.toString();
		
		if ((sequenceName != null) && (!sequenceName.isEmpty())) {
			errorText = sequenceName + ": " + errorText;
		}
		
		return errorText;
	}
		
}
