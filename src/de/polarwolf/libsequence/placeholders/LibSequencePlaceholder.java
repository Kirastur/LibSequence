package de.polarwolf.libsequence.placeholders;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public interface LibSequencePlaceholder {
	
	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions);

}
