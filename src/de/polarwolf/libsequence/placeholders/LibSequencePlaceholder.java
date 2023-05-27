package de.polarwolf.libsequence.placeholders;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

/**
 * Interface for developing own placeholder resolvers
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/PlaceholderManager">Placeholder
 *      Manager</A> (WIKI)
 */
public interface LibSequencePlaceholder {

	public String resolvePlaceholders(String messageText, LibSequenceRunOptions runOptions) throws LibSequenceException;

}
