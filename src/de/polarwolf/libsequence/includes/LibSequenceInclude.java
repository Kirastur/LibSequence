package de.polarwolf.libsequence.includes;

import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Interface for drfining custom includes
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Includes">Includes</A> (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/IncludeManager">Include Manager</A> (WIKI)
 */
public interface LibSequenceInclude {

	public Set<CommandSender> performInclude(String includeName, String valueText, boolean inverseSearch,
			LibSequenceRunningSequence runningSequence) throws LibSequenceException;

}
