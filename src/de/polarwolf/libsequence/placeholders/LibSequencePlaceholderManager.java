package de.polarwolf.libsequence.placeholders;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

/**
 * Manage different placeholder resolve methods
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/PlaceholderManager">Placeholder
 *      Manager</A> (WIKI)
 */
public class LibSequencePlaceholderManager {

	protected List<LibSequencePlaceholder> placeholders = new ArrayList<>();

	public LibSequencePlaceholderManager(LibSequenceOrchestrator orchestrator) {
		// Prevent from starting the Manager without having an orchestrator
	}

	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		placeholders.add(placeholder);
	}

	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions)
			throws LibSequencePlaceholderException {
		if (messageText == null) {
			return null;
		}

		for (LibSequencePlaceholder placeholder : placeholders)
			try {
				messageText = placeholder.resolvePlaceholders(messageText, runOptions);
			} catch (LibSequencePlaceholderException e) {
				throw e;
			} catch (LibSequenceException e) {
				throw new LibSequencePlaceholderException(null, e.getTitle(), null, messageText, e);
			} catch (Exception e) {
				throw new LibSequencePlaceholderException(null, LibSequenceException.JAVA_EXCEPTION, null, messageText,
						e);
			}

		return messageText;
	}

	public boolean containsPlaceholder(String messageText) {
		if (messageText == null) {
			return false;
		}
		String[] messageWords = messageText.split(" ");
		for (String myWord : messageWords) {
			if (myWord.matches(".*%.*%.*")) {
				return true;
			}
		}
		return false;
	}

}
