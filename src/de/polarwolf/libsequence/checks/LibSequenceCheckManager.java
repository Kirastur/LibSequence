package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.LSKERR_CHECK_ALREADY_EXISTS;
import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.LSKERR_CHECK_NOT_FOUND;
import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.LSKERR_JAVA_EXCEPTION;
import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.LSKERR_SYNTAX_ERROR;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Manages the possible checks. Checks validates environmental parameters, if
 * they are not met, the sequence will stop.
 *
 * @see de.polarwolf.libsequence.checks.LibSequenceCheck LibSequenceCheck
 * @see <A href="https://github.com/Kirastur/LibSequence/wiki/Checks">Checks</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/CheckManager">Check
 *      Manager</A> (WIKI)
 */
public class LibSequenceCheckManager {

	public static final String CHECK_PREFIX = "check_";

	protected Map<String, LibSequenceCheck> checkMap = new HashMap<>();

	public LibSequenceCheckManager(LibSequenceOrchestrator orchestrator) {
		// Prevent from starting the Manager without having an orchestrator
	}

	public void registerCheck(String checkName, LibSequenceCheck check) throws LibSequenceCheckException {
		if (hasCheck(checkName)) {
			throw new LibSequenceCheckException(checkName, LSKERR_CHECK_ALREADY_EXISTS, null);
		}
		if (!isValidCheckName(checkName)) {
			throw new LibSequenceCheckException(checkName, LSKERR_SYNTAX_ERROR, null);
		}
		checkMap.put(checkName, check);
	}

	public Set<String> getCheckNames() {
		return checkMap.keySet();
	}

	public boolean isValidCheckName(String checkName) {
		if (checkName.length() <= CHECK_PREFIX.length()) {
			return false;
		}
		return checkName.substring(0, CHECK_PREFIX.length()).equals(CHECK_PREFIX);
	}

	public boolean hasCheck(String checkName) {
		return checkMap.containsKey(checkName);
	}

	public LibSequenceCheck getCheckByName(String checkName) throws LibSequenceCheckException {
		LibSequenceCheck check = checkMap.get(checkName);
		if (check == null) {
			throw new LibSequenceCheckException(null, LSKERR_CHECK_NOT_FOUND, checkName);
		}
		return check;
	}

	protected String invertResult(String checkResult, boolean isInverse) {
		if (!isInverse) {
			return checkResult;
		}
		if (checkResult.isEmpty()) {
			return "inverse operator";
		} else {
			return "";
		}
	}

	protected boolean singleCheck(String checkName, LibSequenceRunningSequence runningSequence,
			LibSequenceConfigStep configStep) throws LibSequenceCheckException {
		try {

			LibSequenceCheck check = getCheckByName(checkName);

			// valueText cannot be null because check exists.
			// It is allowed for valueText to be empty, this is handled in the different
			// checks
			// Placeholder is also resolved in the individual checks
			String valueText = configStep.findValue(checkName);
			boolean isInverse = (!valueText.isEmpty()) && (valueText.substring(0, 1).equals("!"));
			if (isInverse) {
				valueText = valueText.substring(1);
			}

			String checkResult = check.performCheck(checkName, valueText, runningSequence);
			checkResult = invertResult(checkResult, isInverse);

			if (!checkResult.isEmpty()) {
				runningSequence.onCheckFailed(checkName, checkResult);
				return false;
			}

			return true;

		} catch (LibSequenceCheckException e) {
			throw e;
		} catch (LibSequenceException e) {
			throw new LibSequenceCheckException(checkName, e);
		} catch (Exception e) {
			throw new LibSequenceCheckException(checkName, LSKERR_JAVA_EXCEPTION, null, e);
		}
	}

	public boolean performChecks(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep)
			throws LibSequenceCheckException {
		for (String keyName : configStep.getAttributeKeys()) {
			if (isValidCheckName(keyName) && (!singleCheck(keyName, runningSequence, configStep))) {
				return false;
			}
		}
		return true;
	}

}
