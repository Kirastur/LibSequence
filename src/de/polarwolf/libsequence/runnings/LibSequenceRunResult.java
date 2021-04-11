package de.polarwolf.libsequence.runnings;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.*;

import de.polarwolf.libsequence.config.LibSequenceConfigResult;

// This class is expected to be final
// Please don't change it, even if you implement a custom RunManager
// You can formatting and localization of the Error-Text in the callback object

public final class LibSequenceRunResult {

	public final LibSequenceRunningSequence newSequence;
	public final String sequenceName;
	public final LibSequenceRunErrors errorCode;
	public final LibSequenceConfigResult configResult;

	public LibSequenceRunResult(LibSequenceRunningSequence newSequence,  String sequenceName, LibSequenceRunErrors errorCode, LibSequenceConfigResult configResult) {
		this.newSequence=newSequence;
		this.sequenceName=sequenceName;
		this.errorCode=errorCode;
		this.configResult=configResult;
	}

	public boolean hasError() {
		return errorCode!=LSRERR_OK;
	}


	@Override
	public String toString() {
		if (configResult!=null) {
			return configResult.toString();
		}
		String s = sequenceName;
		if (s==null) {
			s = "";
		} else {
			s = s + ": ";
		}
		s = s + errorCode.toString();
		return s;
	}
		
}
