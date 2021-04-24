package de.polarwolf.libsequence.result;

public abstract class LibSequenceResult {
	
	protected final LibSequenceResult subResult;
	
	protected LibSequenceResult(LibSequenceResult subResult) {
		this.subResult = subResult;
	}
	
	public abstract String getLabel();
	protected abstract String getErrorText();
	public abstract boolean hasError();
	
	
	public LibSequenceResult getSubResult() {
		return subResult;
	}
	
	protected String appendSubResult(String errorText) {
		if ((subResult != null) && (subResult.hasError())) {
			String subText = subResult.toString();
			if (!subText.isEmpty()) {
				errorText = errorText + " >>> " + subText; 
			}
		}
		return errorText;
	}
	
	@Override
	public String toString() {
		return appendSubResult(getErrorText());
	}

}
