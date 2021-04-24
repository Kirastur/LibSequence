package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCheckManager {
	
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
		return checkName.substring(0, 6).equals("check_");
	}

	public boolean hasCheck(String checkName) {
		for (String checkKey: checkMap.keySet()) {
			if (checkName.equals(checkKey)) {
				return true;
			}
		}
		return false;
	}
	
	public LibSequenceCheck getCheckByName (String checkName) {
		return checkMap.get(checkName);
	}
	
	public LibSequenceCheckResult performChecks(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) {
		Set<String> keys = configStep.getKeys();
		for (String keyText : keys) {
			if (isValidCheckName(keyText)) {

				String valueText = configStep.getValue(keyText);
				if ((valueText == null)  || (valueText.isEmpty()))  {
					continue;
				}
				valueText = runningSequence.resolvePlaceholder(valueText);

				LibSequenceCheck check = getCheckByName(keyText);
				if (check==null) {
					return new LibSequenceCheckResult(null, LSCERR_CHECK_NOT_FOUND , keyText);				
				}

				LibSequenceCheckResult checkResult = check.performCheck(keyText, valueText, runningSequence.getPlugin(), runningSequence.getRunOptions());
				if (checkResult.hasError()) {
					return checkResult;
				}
			}
		}
		return new LibSequenceCheckResult(null, LSCERR_OK, null);
	}
		
}
