package de.polarwolf.libsequence.runnings;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Exception thrown during sequence execution
 *
 */
public class LibSequenceRunException extends LibSequenceException {

	private static final long serialVersionUID = 1L;

	private final String sequenceName;
	private final int stepNr;
	private final LibSequenceRunErrors errorCode;

	public LibSequenceRunException(String sequenceName, int stepNr, LibSequenceRunErrors errorCode,
			String errorDetailText) {
		super(buildContext(sequenceName, stepNr), errorCode.toString(), errorDetailText);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}

	public LibSequenceRunException(String sequenceName, int stepNr, LibSequenceException cause) {
		super(buildContext(sequenceName, stepNr), cause.getTitle(), null, cause);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = null;
	}

	public LibSequenceRunException(String sequenceName, int stepNr, LibSequenceRunErrors errorCode,
			String errorDetailText, Throwable cause) {
		super(buildContext(sequenceName, stepNr), errorCode.toString(), errorDetailText, cause);
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		this.errorCode = errorCode;
	}

	@Override
	public String getTitle() {
		return "Sequence run error";
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public int getStepNr() {
		return stepNr;
	}

	public LibSequenceRunErrors getErrorCode() {
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
