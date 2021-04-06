package de.polarwolf.libsequence.placeholders;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequencePlaceholderManager {
	
	protected List<LibSequencePlaceholder> placeholders = new ArrayList<>();
	
	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		placeholders.add(placeholder);
	}
	
	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions) {
		for (LibSequencePlaceholder placeholder : placeholders) {
			messageText = placeholder.resolvePlaceholders(messageText, runOptions);
		}
		return messageText;
	}

}
