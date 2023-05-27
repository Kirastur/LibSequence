package de.polarwolf.libsequence.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.directories.LibSequenceDirectory;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryException;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.logger.LibSequenceLogger;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * The ControlAPI is a simple way to execute the sequences defined in the
 * LibSequence's config.yml or which are published in the Public Directory by
 * other plugings. It does the same as the /sequecne command described in the
 * documentation
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/ControlAPI">Control
 *      API</A> (WIKI)
 *
 */
public final class LibSequenceController {

	protected final LibSequenceDirectory directoryAPI;
	protected final LibSequenceToken ownerToken;
	protected final LibSequenceLogger logger;

	public static final String OK = "OK";
	public static final String FILESECTION = "sequences";

	/**
	 * Builds a new Controller-API object. You need this only if you want to extend
	 * the API.
	 *
	 * @param publishPlugin If set, the controller tries to load sequences
	 *                      automatically from the given plugin config.yml. Ti
	 *                      disable autoloading, use NULL here.
	 * @param directoryAPI  The Controller is set on top of the Directory. You need
	 *                      an existing directoryAPI object containing the public
	 *                      directory to use the controller.
	 * @param logger        Callback for printing error messages. The LibSequence
	 *                      itself does not print error messages on its own, so you
	 *                      must deliver an output provider.
	 */
	public LibSequenceController(Plugin publishPlugin, LibSequenceDirectory directoryAPI, LibSequenceLogger logger) {
		this.directoryAPI = directoryAPI;
		this.logger = logger;
		this.ownerToken = new LibSequenceToken();
		if (publishPlugin != null) {
			boolean result = directoryAPI.addSequencesFromFileLater(publishPlugin, ownerToken, null, FILESECTION);
			if (!result) {
				publishPlugin.getLogger().warning("Error attaching sequences to controller");
			}
		}
	}

	/**
	 * Give you a list of all available sequences. Only sequences which have passed
	 * the syntax check are included. If there are no sequences avail, the list is
	 * empty.
	 *
	 * @return List if sequence names which can be executed. They are taken from the
	 *         public directory.
	 */
	public List<String> getNames() {
		Set<String> names = directoryAPI.getRunnableSequenceNames();
		return new ArrayList<>(names);
	}

	/**
	 * Check if a sequence with a given name is known and executable by the
	 * LibSequence.
	 *
	 * @param sequenceName Name of the sequence to look for
	 * @return TRUE if the sequence was found and is executable. otherwise FALSE
	 */
	public boolean hasSequence(String sequenceName) {
		return directoryAPI.hasRunnableSequence(sequenceName);
	}

	/**
	 * Check if the given CommandSender (player) has the permission to execute the
	 * given sequence. The needed permission is
	 * "libsequence.sequence._sequencename_".
	 *
	 * @param initiator    The CommandSender (e.g. Player) to check the permission
	 *                     for
	 * @param sequenceName The sequence which should be checked for access
	 * @return TRUE if the player has the permission to execute the sequence,
	 *         otherwise FALSE
	 */
	public boolean hasPermission(CommandSender initiator, String sequenceName) {
		String permissionName = "libsequence.sequence." + sequenceName;
		return initiator.hasPermission(permissionName);
	}

	/**
	 * Execute the sequence with the given name. You can run a sequence more than
	 * once at the same time. The CommandSender can be a Player, the Console or
	 * simply NULL. The return value is "OK" if the sequence has started
	 * successfully, otherwise it contains the error text, ready for printout. The
	 * permissions are not checked here, so you should call _hasPermission_ before.
	 *
	 * All errors are returned as string, so we don't expect a java exception here.
	 * If you want to use Placeholders, you should use DirectoryAPI or SequencerAPI
	 * instead.
	 *
	 * @param sequenceName Name of the sequence which should be started
	 * @param initiator    CommandSender who should be assigned as the initiator of
	 *                     the sequence (can be NULL).
	 * @return A String with the result, e.g. "OK" or an error message
	 */
	public String execute(String sequenceName, CommandSender initiator) {
		LibSequenceRunOptions runOptions = new LibSequenceRunOptions();
		if (initiator != null) {
			runOptions.setInitiator(initiator);
		}
		runOptions.setLogger(logger);
		try {
			directoryAPI.execute(ownerToken, sequenceName, runOptions);
			return OK;
		} catch (LibSequenceDirectoryException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Cancel all running sequences with the given name. If more than one instance
	 * is running at the same time, all of them are cancelled. The return value is
	 * the number of sequences cancelled.
	 *
	 * @param sequenceName Name of the sequence which should be cancelled
	 * @return Number of running sequences which were cancelled
	 */
	public int cancel(String sequenceName) {
		try {
			return directoryAPI.cancel(sequenceName);
		} catch (LibSequenceDirectoryException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Get a list of all running sequences which are cancelable. If more than one
	 * sequence with the same name is running, the sequence name is returned only
	 * once.
	 *
	 * @return List of sequence name where at least one instance is running
	 */
	public List<String> getRunningSequenceNames() {
		List<String> sequenceNames = new ArrayList<>();
		for (String name : directoryAPI.getAllSequenceNames()) {
			if (!directoryAPI.findRunningSequences(name).isEmpty()) {
				sequenceNames.add(name);
			}
		}
		return sequenceNames;
	}

	/**
	 * Force a reload of all sequences which are reloadable. This normally contains
	 * the sequences defined in the LibSequence's config.yml. Currently running
	 * sequences are not affected, they are still continuing using the old
	 * definitions. The new definitions are only used in subsequent starts.
	 *
	 * The return value is "OK" if the reload performed successfully for all
	 * sections, otherwise it contains the error text, ready for printout.
	 *
	 * @return A String with the result, e.g. "OK" or an error message
	 */
	public String reload() {
		try {
			directoryAPI.reload();
			return OK;
		} catch (LibSequenceException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
