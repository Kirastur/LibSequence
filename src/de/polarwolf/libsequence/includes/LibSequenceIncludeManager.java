package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.LSIERR_INCLUDE_ALREADY_EXISTS;
import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.LSIERR_INCLUDE_NOT_FOUND;
import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.LSIERR_JAVA_EXCEPTION;
import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.LSIERR_SYNTAX_ERROR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Manage the different includes
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Includes">Includes</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/IncludeManager">Include
 *      Manager</A> (WIKI)
 */
public class LibSequenceIncludeManager {

	public static final String INCLUDE_PREFIX = "include_";

	protected Map<String, LibSequenceInclude> includeMap = new HashMap<>();

	public LibSequenceIncludeManager(LibSequenceOrchestrator orchestrator) {
		// Prevent from starting the Manager without having an orchestrator
	}

	public void registerInclude(String includeName, LibSequenceInclude include) throws LibSequenceIncludeException {
		if (hasInclude(includeName)) {
			throw new LibSequenceIncludeException(includeName, LSIERR_INCLUDE_ALREADY_EXISTS, null);
		}
		if (!isValidIncludeName(includeName)) {
			throw new LibSequenceIncludeException(includeName, LSIERR_SYNTAX_ERROR, null);
		}
		includeMap.put(includeName, include);
	}

	public Set<String> getIncludeNames() {
		return includeMap.keySet();
	}

	public boolean isValidIncludeName(String includeName) {
		if (includeName.length() <= INCLUDE_PREFIX.length()) {
			return false;
		}
		return includeName.substring(0, INCLUDE_PREFIX.length()).equals(INCLUDE_PREFIX);
	}

	public boolean hasInclude(String includeName) {
		return includeMap.containsKey(includeName);
	}

	public LibSequenceInclude getIncludeByName(String includeName) throws LibSequenceIncludeException {
		LibSequenceInclude include = includeMap.get(includeName);
		if (include == null) {
			throw new LibSequenceIncludeException(null, LSIERR_INCLUDE_NOT_FOUND, includeName);
		}
		return include;
	}

	protected boolean hasOperator(String valueText, String operatorChar) {
		return (!valueText.isEmpty()) && (valueText.substring(0, 1).equals(operatorChar));
	}

	protected void singleInclude(String includeName, LibSequenceRunningSequence runningSequence,
			LibSequenceConfigStep configStep, Set<CommandSender> sendersInclude, Set<CommandSender> sendersExclude)
			throws LibSequenceIncludeException {
		try {
			LibSequenceInclude include = getIncludeByName(includeName);

			// valueText cannot be null because key exists
			// it is allowed for valueText to be empty, this is handled in the different
			// includes.
			// Placeholder is also resolved in the individual includes
			String valueText = configStep.findValue(includeName);

			boolean isExclude = hasOperator(valueText, "-");
			if (isExclude) {
				valueText = valueText.substring(1);
			}

			boolean isInverse = hasOperator(valueText, "!");
			if (isInverse) {
				valueText = valueText.substring(1);
			}

			Set<CommandSender> mySenders = include.performInclude(includeName, valueText, isInverse, runningSequence);
			if (isExclude) {
				sendersExclude.addAll(mySenders);
			} else {
				sendersInclude.addAll(mySenders);
			}
		} catch (LibSequenceIncludeException e) {
			throw e;
		} catch (LibSequenceException e) {
			throw new LibSequenceIncludeException(includeName, e);
		} catch (Exception e) {
			throw new LibSequenceIncludeException(includeName, LSIERR_JAVA_EXCEPTION, null, e);
		}
	}

	public Set<CommandSender> performIncludes(LibSequenceRunningSequence runningSequence,
			LibSequenceConfigStep configStep) throws LibSequenceIncludeException {
		Set<CommandSender> sendersInclude = new HashSet<>();
		Set<CommandSender> sendersExclude = new HashSet<>();

		for (String keyText : configStep.getAttributeKeys()) {

			// Tests if key is an include (because it could also be another action-related
			// attribute)
			if (isValidIncludeName(keyText)) {
				singleInclude(keyText, runningSequence, configStep, sendersInclude, sendersExclude);
			}
		}
		sendersInclude.removeAll(sendersExclude);
		return sendersInclude;
	}

}
