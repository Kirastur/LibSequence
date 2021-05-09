package de.polarwolf.libsequence.config;

import de.polarwolf.libsequence.exception.LibSequenceException;

public class LibSequenceConfigException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	protected final String sequenceName;
	protected final int stepNr;
	protected final LibSequenceConfigErrors errorCode;

	
	public LibSequenceConfigException(LibSequenceConfigErrors errorCode, String errorDetailText) {
		super(buildContext(null, 0), errorCode.toString(), errorDetailText);
		this.sequenceName = null;
		this.stepNr = 0;
		this.errorCode = errorCode;
	}

	
	public LibSequenceConfigException(String sequenceName, LibSequenceConfigErrors errorCode, String errorDetailText) {
		super(buildContext(sequenceName, 0), errorCode.toString(), errorDetailText);
		this.sequenceName = sequenceName;
		this.stepNr = 0;
		this.errorCode = errorCode;
	}

	
	public LibSequenceConfigException(String sequenceName, int stepNr, LibSequenceConfigErrors errorCode, String errorDetailText) {
		super(buildContext(sequenceName, stepNr), errorCode.toString(), errorDetailText);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}

	
	public LibSequenceConfigException(String sequenceName, int stepNr, LibSequenceException cause) {
		super(buildContext(sequenceName, stepNr), cause.getTitle(), null, cause);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = null;
	}

	
	public LibSequenceConfigException(String sequenceName, int stepNr, LibSequenceConfigErrors errorCode, String errorDetailText, Throwable cause) {
		super(buildContext(sequenceName, stepNr), errorCode.toString(), errorDetailText, cause);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}
	
	
	@Override
	public String getTitle() {
		return "Config error";
	}


	public String getSequenceName() {
		return sequenceName;
	}


	public int getStepNr() {
		return stepNr;
	}


	public LibSequenceConfigErrors getErrorCode() {
		return errorCode;
	}
	
	protected static String buildContext(String sequenceName, int stepNr) {
		if ((sequenceName == null) || (sequenceName.isEmpty())) {
			return null;
		}
		
		String contextName = sequenceName;
		if (stepNr > 0) { 
			contextName = contextName + " Step " + Integer.toString(stepNr);
		}
		
		return contextName;
	}

}
