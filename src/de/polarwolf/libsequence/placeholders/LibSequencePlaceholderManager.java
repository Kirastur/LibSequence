package de.polarwolf.libsequence.placeholders;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequencePlaceholderManager {
	
	protected List<LibSequencePlaceholder> placeholders = new ArrayList<>();
	

	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		placeholders.add(placeholder);
	}
	

	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions) throws LibSequencePlaceholderException {
		if (messageText == null) {
			return null;
		}

		for (LibSequencePlaceholder placeholder : placeholders) try {
			messageText = placeholder.resolvePlaceholders(messageText, runOptions);
		} catch (LibSequencePlaceholderException e) {
			throw e;
		} catch (LibSequenceException e) {
			throw new LibSequencePlaceholderException(null, e.getTitle(), null, messageText, e);
		} catch (Exception e) {
			throw new LibSequencePlaceholderException(null, LibSequenceException.JAVA_EXCEPTION, null, messageText, e);
		}

		return messageText;
	}
	
	public boolean containsPlaceholder(String messageText) {
		if (messageText == null) {
			return false;
		}
		String[] messageWords = messageText.split(" ");
		for (int i=0; i < messageWords.length; i++) {
			String word = messageWords[i];
			if (word.matches(".*%.*%.*")) {
				return true;
			}
		}
		return false;
	}

}
