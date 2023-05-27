package de.polarwolf.libsequence.config;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Exception thrown during loading the config
 *
 */
public class LibSequenceConfigException extends LibSequenceException {

	private static final long serialVersionUID = 1L;
	protected final String sectionName;
	protected final String sequenceName;
	protected final int stepNr;
	protected final LibSequenceConfigErrors errorCode;

	public LibSequenceConfigException(String sectionName, LibSequenceConfigErrors errorCode, String errorDetailText) {
		super(buildContext(sectionName, null, 0), errorCode.toString(), errorDetailText);
		this.sectionName = sectionName;
		this.sequenceName = null;
		this.stepNr = 0;
		this.errorCode = errorCode;
	}

	public LibSequenceConfigException(String sectionName, String sequenceName, LibSequenceConfigErrors errorCode,
			String errorDetailText) {
		super(buildContext(sectionName, sequenceName, 0), errorCode.toString(), errorDetailText);
		this.sectionName = sectionName;
		this.sequenceName = sequenceName;
		this.stepNr = 0;
		this.errorCode = errorCode;
	}

	public LibSequenceConfigException(String sectionName, String sequenceName, int stepNr,
			LibSequenceConfigErrors errorCode, String errorDetailText) {
		super(buildContext(sectionName, sequenceName, stepNr), errorCode.toString(), errorDetailText);
		this.sectionName = sectionName;
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}

	public LibSequenceConfigException(String sectionName, String sequenceName, int stepNr, LibSequenceException cause) {
		super(buildContext(sectionName, sequenceName, stepNr), cause.getTitle(), null, cause);
		this.sectionName = sectionName;
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = null;
	}

	public LibSequenceConfigException(String sectionName, String sequenceName, int stepNr,
			LibSequenceConfigErrors errorCode, String errorDetailText, Throwable cause) {
		super(buildContext(sectionName, sequenceName, stepNr), errorCode.toString(), errorDetailText, cause);
		this.sectionName = sectionName;
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}

	@Override
	public String getTitle() {
		return "Config error";
	}

	public String getSectionName() {
		return sectionName;
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

	protected static String buildContext(String sectionName, String sequenceName, int stepNr) {
		String contextName = "";
		if ((sectionName != null) && !sectionName.isEmpty()) {
			contextName = "Section " + sectionName;
		}
		if ((sequenceName != null) && !sequenceName.isEmpty()) {
			if (!contextName.isEmpty()) {
				contextName = contextName + " ";
			}
			contextName = contextName + "Sequence " + sequenceName;
		}
		if (stepNr > 0) {
			contextName = contextName + " Step " + Integer.toString(stepNr);
		}
		return contextName;
	}

}
