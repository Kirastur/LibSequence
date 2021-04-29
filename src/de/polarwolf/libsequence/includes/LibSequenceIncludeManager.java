package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceIncludeManager {
	
	public static final String INCLUDE_PREFIX = "include_";

	protected Map<String, LibSequenceInclude> includeMap = new HashMap<>();
	

	public LibSequenceIncludeResult registerInclude(String includeName, LibSequenceInclude include) {
		if (hasInclude(includeName)) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_INCLUDE_ALREADY_EXISTS, null);
		}
		if (!isValidIncludeName(includeName)) {
			return new LibSequenceIncludeResult(null, includeName, LSIERR_SYNTAX_ERROR, null);
		}
		includeMap.put(includeName, include);
		return new LibSequenceIncludeResult(null, includeName, LSIERR_OK, null);
	}
	

	public boolean isValidIncludeName(String includeName) {
		if (includeName.length() <= INCLUDE_PREFIX.length()) {
			return false;
		}
		return includeName.substring(0, INCLUDE_PREFIX.length()).equals(INCLUDE_PREFIX);
	}


	public LibSequenceInclude getIncludeByName (String includeName) {
		return includeMap.get(includeName);
	}
	

	public boolean hasInclude(String includeName) {
		return (getIncludeByName (includeName) != null);
	}
	

	protected boolean hasOperator(String valueText, String operatorChar) {
		return (!valueText.isEmpty()) && (valueText.substring(0, 1).equals(operatorChar));		
	}
	

	public LibSequenceIncludeResult performIncludes(LibSequenceRunningSequence runningSequence, LibSequenceConfigStep configStep) {
		Set<CommandSender> sendersInclude = new HashSet<>();
		Set<CommandSender> sendersExclude = new HashSet<>();
		LibSequenceIncludeResult lastFailedIncludeResult = new LibSequenceIncludeResult (null, null, LSIERR_OK, null);

		for (String keyText : configStep.getKeys()) {
			if (isValidIncludeName(keyText)) {

				// ToDo Implement SyntaxManager
				LibSequenceInclude include = getIncludeByName(keyText);
				if (include==null) {
					return new LibSequenceIncludeResult(null, null, LSIERR_INCLUDE_NOT_FOUND , keyText);				
				}

				// valueText cannot be null because key exists
				// it is allowed for valueText to be empty, this is handled in the different includes
				// Placeholder is also resolved in the individual includes
				String valueText = configStep.getValue(keyText);
				
				boolean isExclude = hasOperator(valueText, "-");
				if (isExclude) {
					valueText = valueText.substring(1);
				}
				
				boolean isInverse = hasOperator (valueText, "!");
				if (isInverse) {
					valueText = valueText.substring(1);
				}
				
				LibSequenceIncludeResult includeResult = include.performInclude(keyText, valueText, isInverse, runningSequence);
				if (includeResult.hasError()) {
					lastFailedIncludeResult = includeResult;
					continue;
				}
				
				if (isExclude) {
					sendersExclude.addAll(includeResult.senders);
				} else {
					sendersInclude.addAll(includeResult.senders);
				}
			}
		}
		
		sendersInclude.removeAll(sendersExclude);
		// ToDo legal way without private trick
		lastFailedIncludeResult.senders = sendersInclude;
		return lastFailedIncludeResult;
	}
	
}
