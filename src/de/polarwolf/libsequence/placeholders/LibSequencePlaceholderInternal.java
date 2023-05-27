package de.polarwolf.libsequence.placeholders;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

/**
 * Resolve placeholders set in runOptions
 *
 */
public class LibSequencePlaceholderInternal implements LibSequencePlaceholder {

	@Override
	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions) {
		for (String placeholderName : runOptions.listPlaceholders()) {
			String placeholderValue = runOptions.findPlaceholder(placeholderName);
			placeholderName = "%" + placeholderName + "%";
			messageText = messageText.replaceAll(placeholderName, placeholderValue);
		}
		return messageText;
	}

}
