package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import java.util.HashMap;
import java.util.Map;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckManager {
	
	public static final String CHECK_PREFIX = "check_";

	protected Map<String, LibSequenceCheck> checkMap = new HashMap<>();
	
	public LibSequenceCheckResult registerCheck(String checkName, LibSequenceCheck check) {
		if (hasCheck(checkName)) {
			return new LibSequenceCheckResult(checkName, LSCERR_CHECK_ALREADY_EXISTS, null);
		}
		if (!isValidCheckName(checkName)) {
			return new LibSequenceCheckResult(checkName, LSCERR_SYNTAX_ERROR, null);
		}
		checkMap.put(checkName, check);
		return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
	}
	
	public boolean isValidCheckName(String checkName) {
		if (checkName.length() <= CHECK_PREFIX.length()) {
			return false;
		}
		return checkName.substring(0, CHECK_PREFIX.length()).equals(CHECK_PREFIX);
	}

	public LibSequenceCheck getCheckByName (String checkName) {
		return checkMap.get(checkName);
	}
	
	public boolean hasCheck(String checkName) {
		return (getCheckByName(checkName) != null);
	}
	
	protected LibSequenceCheckResult invertResult(String keyText, LibSequenceCheckResult checkResult, boolean isInverse) {
		if (!isInverse) {
			return checkResult;
		}
		if (checkResult.errorCode == LSCERR_FALSE) {
			return new LibSequenceCheckResult(null, LSCERR_OK, null);
		}
		if (checkResult.errorCode == LSCERR_OK) {
			return new LibSequenceCheckResult(keyText, LSCERR_FALSE, "inverse operator");
		}
		return checkResult;
	}
	
	public LibSequenceCheckResult performChecks(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) {
		for (String keyText : configStep.getKeys()) {
			if (isValidCheckName(keyText)) {

				LibSequenceCheck check = getCheckByName(keyText);
				if (check==null) {
					return new LibSequenceCheckResult(null, LSCERR_CHECK_NOT_FOUND , keyText);				
				}

				// valueText cannot be null because key exists
				// it is allowed for valueText to be empty, this is handled in the different checks
				// Placeholder is also resolved in the individual checks
				String valueText = configStep.getValue(keyText);
				boolean isInverse = (!valueText.isEmpty()) && (valueText.substring(0, 1).equals("!"));
				if (isInverse) {
					valueText = valueText.substring(1);
				}
				
				LibSequenceCheckResult checkResult = check.performCheck(keyText, valueText, runningSequence);
				checkResult = invertResult(keyText,checkResult, isInverse);
				
				if (checkResult.hasError()) {
					return checkResult;
				}
			}
		}
		return new LibSequenceCheckResult(null, LSCERR_OK, null);
	}
		
}
