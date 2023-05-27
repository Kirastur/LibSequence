package de.polarwolf.libsequence.directories;

import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceSequencer;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Directories are a simple way to share sequences between plugins. One plugin
 * can publish its sequences in a directory and share this directory. Another
 * application can select a sequence from the directory and start it.
 *
 * There can exists more than one Directory on the Minecraft server, but one
 * directory has a prominent position: the Public Directory. The Public
 * Directory can accessed by the DirectoryAPI from any other plugin, so
 * sequences published in the Public Directory can be used from all plugins. The
 * other directories are private, which means it's the job of the sharing
 * application to define a way how other plugins get their own directory.
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/DirectoryAPI">Directory
 *      API</A> (WIKI)
 *
 */
public class LibSequenceDirectory {

	protected final LibSequenceToken apiToken;
	protected final LibSequenceDirectoryManager directoryManager;

	/**
	 * Builds a new Controller-API object. You need this only if you want to extend
	 * the API.
	 *
	 * @param plugin       A plugin is needed because the directory is listening to
	 *                     some minecraft events to get informed about config
	 *                     reloads
	 * @param apiToken     Set the apiToken which must be used later to shutdown the
	 *                     directory
	 * @param sequencerAPI The Directory is set on top of the Sequencer. You need an
	 *                     existing sequencerAPI object to instantiate a directory.
	 */
	public LibSequenceDirectory(Plugin plugin, LibSequenceToken apiToken, LibSequenceSequencer sequencerAPI) {
		this.apiToken = apiToken;
		this.directoryManager = new LibSequenceDirectoryManager(plugin, sequencerAPI);
	}

	/**
	 * Register a new Directory contributor. A contributor is a config section which
	 * wants to publish its sequences in this directory. If this is a registration
	 * for an existing ownerToken, the old registration gets deleted. If the
	 * ownerToken not already has a section, a dummy section is created.
	 *
	 * @param ownerToken       Token associated with an existing or planned config
	 *                         section
	 * @param sectionName      Name of the section to be print in error messages.
	 *                         Mostly the plugin name.
	 * @param directoryOptions Define which permission the owner grants to the
	 *                         directory
	 * @throws LibSequenceDirectoryException Exception thrown if a sequence with the
	 *                                       same name already exists
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/directoryOptions">Directory
	 *      Options</A> (WIKI)
	 *
	 */
	public void registerContributor(LibSequenceToken ownerToken, String sectionName,
			LibSequenceDirectoryOptions directoryOptions) throws LibSequenceDirectoryException {
		directoryManager.registerContributor(ownerToken, sectionName, directoryOptions);
	}

	/**
	 * Remove all published sequences from the directory and unregister the section
	 * associated with the given ownerToken.
	 *
	 * @param ownerToken Token associated with an existing section which should be
	 *                   removed from the directory
	 */
	public void unregisterContributor(LibSequenceToken ownerToken) {
		directoryManager.unregisterContributor(ownerToken);
	}

	/**
	 * Check if the given ownerToken is registered as a contributor for publishing
	 * sequences.
	 *
	 * @param ownerToken Contributor to search for
	 * @return TRUE if the given ownerToken is associated with a contributor,
	 *         otherwise FALSE
	 */
	public boolean hasDirectoryContributor(LibSequenceToken ownerToken) {
		return directoryManager.hasDirectoryContributor(ownerToken);
	}

	/**
	 * Publish a sequence with the given name in the directory. If the name does
	 * already exists, an exception is thrown. If the name exists, but the sequence
	 * behind is not valid, the name gets overwritten instead.
	 *
	 * @param ownerToken   The section from the sequence should taken from
	 * @param sequenceName Name of the sequence which should be published
	 * @throws LibSequenceDirectoryException An exception s thrown if the sequence
	 *                                       does not exists or the name is already
	 *                                       in use in the directory
	 */
	public void addSequence(LibSequenceToken ownerToken, String sequenceName) throws LibSequenceDirectoryException {
		directoryManager.addSequence(ownerToken, sequenceName);
	}

	/**
	 * Remove the sequence with the given name from the directory. You can only
	 * remove your own sequences.
	 *
	 * An exception is thrown if you try to remove a foreign sequence.
	 *
	 * @param ownerToken   The section from which the sequence should be taken away
	 * @param sequenceName The Name of the sequence which should be removed
	 * @throws LibSequenceDirectoryException An exception is thrown if the sequence
	 *                                       was not found or belongs to a different
	 *                                       owner.
	 */
	public void removeSequence(LibSequenceToken ownerToken, String sequenceName) throws LibSequenceDirectoryException {
		directoryManager.removeSequence(ownerToken, sequenceName);
	}

	/**
	 * Removes all sequences from the directory which are previously registered by
	 * the ownerToken.
	 *
	 * @param ownerToken owner which sequences should be removed from the directory
	 * @return Number of sequences removed
	 */
	public int clearAllMySequences(LibSequenceToken ownerToken) {
		return directoryManager.clearAllMySequences(ownerToken);
	}

	/**
	 * Check if a sequence with the given name does exist in the directory and is
	 * ready to be executed (which means the sequence has no syntax error).
	 *
	 * @param sequenceName Sequence name to check for
	 * @return TRUE if the sequence exists and is executable, otherwise FALSE
	 */
	public boolean hasRunnableSequence(String sequenceName) {
		return directoryManager.hasRunnableSequence(sequenceName);
	}

	/**
	 * Check if the sequence with the given name belongs to the given owner
	 *
	 * @param ownerToken   owner to check for
	 * @param sequenceName sequence to check for
	 * @return TRUE if the given sequence exists and belongs to the given owner,
	 *         otherwise FALSE
	 */
	public boolean isMySequence(LibSequenceToken ownerToken, String sequenceName) {
		return directoryManager.isMySequence(ownerToken, sequenceName);
	}

	/**
	 * Sequences are published in the directory by name. You only need to know the
	 * name of the sequence to start it. It dosn't matter which plugin has published
	 * the sequence. But in some circumstances you want to know witch pluging has
	 * contributed a specific sequence, so you can use this function to query this.
	 *
	 * An exception is thrown if the given sequenceName is not found in the
	 * directory.
	 *
	 * @param sequenceName Name of the sequence to search for
	 * @return Name of the section this sequence is contained in. Normally the
	 *         section name is the name of the plugin
	 * @throws LibSequenceDirectoryException Sequence not found
	 */
	public String getSequenceOwnerName(String sequenceName) throws LibSequenceDirectoryException {
		return directoryManager.getSequenceOwnerName(sequenceName);
	}

	/**
	 * Return a list with the name of all sequences which are published in the
	 * directory and are ready to start. Broken sequences (e.g. sequences with
	 * syntax error) are not included.
	 *
	 * @return List of sequence names
	 */
	public Set<String> getRunnableSequenceNames() {
		return directoryManager.getRunnableSequenceNames();
	}

	/**
	 * Return a list of all sequence names published in the directory.
	 *
	 * @return List of sequence names
	 */
	public Set<String> getAllSequenceNames() {
		return directoryManager.getAllSequenceNames();
	}

	/**
	 * Starts a sequence listed in the directory. You must provide a runnerToken to
	 * claim the ownership of the the new running instance of the sequence. You can
	 * specify additional information like the _initiator_ in the runOptions.
	 *
	 * The result is a handle to the running sequence instance. If the sequence is
	 * not found or can not start, an exception is thrown.
	 *
	 * @param runnerToken  Create a Token with "new LibSequenceToken()" and provide
	 *                     it here to claim your ownership of the newly started
	 *                     sequence. You need this Token if you e.g. want to cancel
	 *                     the sequence.
	 * @param sequenceName Name of the sequence which should be started
	 * @param runOptions   Additional options, e.g. about the initiator
	 * @return The instance of running sequence
	 * @throws LibSequenceDirectoryException Error with starting the sequence, e.g.
	 *                                       sequence not found or authorization
	 *                                       keys are missing.
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 * @see <A href= "https://github.com/Kirastur/LibSequence/wiki/runOptions">Run
	 *      Options</A> (WIKI)
	 *
	 */
	public LibSequenceRunningSequence execute(LibSequenceToken runnerToken, String sequenceName,
			LibSequenceRunOptions runOptions) throws LibSequenceDirectoryException {
		return directoryManager.execute(runnerToken, sequenceName, runOptions);
	}

	/**
	 * The owner can define if other plugins can cancel a sequence after the
	 * sequence was started (Remember: If the sequence was started by yourself, you
	 * already have the LibSequenceRunningSequence-object, so you can always cancel
	 * this sequence). But if you want to cancel sequences started by other plugins,
	 * you must first find this object. With findRunningSequences you get a list of
	 * LibSequenceRunningSequence objects with all the sequences which are currently
	 * running and where the owner has allowed others to cancel them.
	 *
	 * @param sequenceName Name of the sequence to search for
	 * @return List of running sequences
	 */
	public List<LibSequenceRunningSequence> findRunningSequences(String sequenceName) {
		return directoryManager.findRunningSequences(sequenceName);
	}

	/**
	 * Cancel all currently running sequences with the given name. The return value
	 * is the number of sequences that are cancelled by this action (and zero if no
	 * sequence with this name is running).
	 *
	 * An exception is thrown if the given name is unknown or the owner has
	 * forbidden the cancellation.
	 *
	 * @param sequenceName Name of the sequence where you want to cancel all running
	 *                     instances
	 * @return Number of sequences cancelled
	 * @throws LibSequenceDirectoryException Sequence Name unknown or the owner has
	 *                                       forbidden to cancel them.
	 */
	public int cancel(String sequenceName) throws LibSequenceDirectoryException {
		return directoryManager.cancel(sequenceName);
	}

	/**
	 * Repopulate the directory with all sequences from contributors which have the
	 * directotyOption Flag _includeAll_ set. Normally this is done automatically
	 * when you add/remove a contributor or perform a reload. So I hope you never
	 * need this.
	 *
	 * @throws LibSequenceDirectoryException Duplicate names found
	 */
	public void refreshDirectory() throws LibSequenceDirectoryException {
		directoryManager.refreshDirectory();
	}

	/**
	 * Reloads all sequences. Simply calls the sequeceAPI for this. So you don't
	 * need to leave the directoyAPI only for reloading sequences.
	 *
	 * @return Number of sequences reloaded
	 * @throws LibSequenceException An exception is thrown if any of the
	 *                              section-reloads detect a failure (e.g. a syntax
	 *                              error in sequence definition).
	 */
	public int reload() throws LibSequenceException {
		return directoryManager.reload();
	}

	/**
	 * Register the ownerToken as distributor, attach the given config-file and
	 * -section to the reloader, so that the file content is loaded as a section
	 * into LibSequence, and register the section with the directory option
	 * "includeAll" so all sequences in this section are published automatically.
	 *
	 * @param plugin      The plugin defines the data directory in the filesystem
	 *                    where the YAML file reside. The plugin name is also used
	 *                    as the section name.
	 * @param ownerToken  You must create an ownerToken first which will be used as
	 *                    an authentication handler to this section
	 * @param fileName    The file where the sequences should be read from. Can be
	 *                    NULL or "" if it's the default config.yml
	 * @param fileSection Section inside the file where the sequences are stored. We
	 *                    suggest to use "sequences". If empty the root section is
	 *                    used.
	 * @throws LibSequenceConfigException    Thrown if there is an error with
	 *                                       loading the sequences, e.g. syntax
	 *                                       error
	 * @throws LibSequenceDirectoryException Thrown if a sequence with one of the
	 *                                       names already exists.
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
	 *      definition</A> (WIKI)
	 *
	 */
	public int addSequencesFromFile(Plugin plugin, LibSequenceToken ownerToken, String fileName, String fileSection)
			throws LibSequenceConfigException, LibSequenceDirectoryException {
		return directoryManager.addSequencesFromFile(plugin, ownerToken, fileName, fileSection);
	}

	/**
	 * Register the ownerToken as distributor, attach the given config-file and
	 * -section to the reloader, so that the file content is loaded as a section
	 * into LibSequence, and register the section with the directory option
	 * "includeAll" so all sequences in this section are published automatically at
	 * the next tick.
	 *
	 * @param plugin      The plugin defines the data directory in the filesystem
	 *                    where the YAML file reside. The plugin name is also used
	 *                    as the section name.
	 * @param ownerToken  You must create an ownerToken first which will be used as
	 *                    an authentication handler to this section
	 * @param fileName    The file where the sequences should be read from. Can be
	 *                    NULL or "" if it's the default config.yml
	 * @param fileSection Section inside the file where the sequences are stored. We
	 *                    suggest to use "sequences". If empty the root section is
	 *                    used.
	 * @return TRUE if the sequence are scheduled for later adding, FALSE if the
	 *         ownerToken already has an existing ConfigSection or DirecoryProvider.
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
	 *      definition</A> (WIKI)
	 *
	 */
	public boolean addSequencesFromFileLater(Plugin plugin, LibSequenceToken ownerToken, String fileName,
			String fileSection) {
		return directoryManager.addSequencesFromFileLater(plugin, ownerToken, fileName, fileSection);
	}

	/**
	 * Reload the given section from file
	 *
	 * @param ownerToken The ownerToken which is associated to the section you want
	 *                   to reload
	 * @return Number of sequences reloaded
	 * @throws LibSequenceException Thrown if there is an error with loading the
	 *                              sequences, e.g. syntax error
	 */
	public int partialReloadFromConfigFile(LibSequenceToken ownerToken) throws LibSequenceException {
		return directoryManager.partialReloadFromConfigFile(ownerToken);
	}

	/**
	 * Check if the directory has already shut down (e.g. because server is
	 * stopping). In this case you cannot start new sequences.
	 *
	 * @return TRUE if the directory is disabled and cannot start new sequences,
	 *         otherwise FALSE
	 */
	public boolean isDisabled() {
		return directoryManager.isDisabled();
	}

	/**
	 * Used internally to perform a clean shutdown
	 */
	public boolean disable(LibSequenceToken currentApiToken) {
		if (!apiToken.equals(currentApiToken)) {
			return false;
		}
		directoryManager.disable();
		return true;
	}

}
