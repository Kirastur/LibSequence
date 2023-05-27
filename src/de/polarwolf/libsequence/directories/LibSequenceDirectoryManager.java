package de.polarwolf.libsequence.directories;

import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_ACCESS_DENIED;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_CONFIG_HAS_GONE;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_DISABLED;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_ERROR_DURING_SEQUENCE_START;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_NAME_IS_EMPTY;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_ONLY_OWNER_CAN_REMOVE;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_OWNERTOKEN_IN_USE;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_OWNERTOKEN_NOT_REGISTERED;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_SEQUENCE_ALREADY_REGISTERED;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_SEQUENCE_NOT_FOUND;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_SEQUENCE_NOT_RUNNABLE;
import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.LSDERR_TOKEN_IS_NULL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceSequencer;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Manage a directory. Directories are a simple way to share sequences between
 * plugins. One plugin can publish its sequences in a directory and share this
 * directory. Another application can select a sequence from the directory and
 * start it.
 *
 * There can exists more than one Directory on the Minecraft server, but one
 * directory has a prominent position: the Public Directory. The Public
 * Directory can be accessed by the DirectoryAPI from any other plugin, so
 * sequences published in the Public Directory can be used from all plugings.
 * The other directories are private, which means it's the job of the sharing
 * application to define a way how other plugins get that directory.
 *
 */
public class LibSequenceDirectoryManager {

	private boolean bDisabled = false;
	protected final LibSequenceSequencer sequencerAPI;
	protected LibSequenceDirectoryListener directoryListener;

	protected Map<LibSequenceToken, LibSequenceDirectoryOptions> contributors = new HashMap<>();
	protected Map<String, LibSequenceToken> sequences = new HashMap<>();

	protected LibSequenceDirectoryManager(Plugin plugin, LibSequenceSequencer sequencerAPI) {
		this.sequencerAPI = sequencerAPI;
		directoryListener = new LibSequenceDirectoryListener(plugin, this);
		directoryListener.registerListener();
	}

	/**
	 * Register a new Directory contributor. A contributor is a config section which
	 * wants to publish its sequences in this directory. If this is a registration
	 * for an existing ownerToken, the old registration gets deleted.
	 */
	public void registerContributor(LibSequenceToken ownerToken, String sectionName,
			LibSequenceDirectoryOptions directoryOptions) throws LibSequenceDirectoryException {
		if (isDisabled()) {
			throw new LibSequenceDirectoryException(null, LSDERR_DISABLED, null);
		}
		if (ownerToken == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_TOKEN_IS_NULL, null);
		}

		if (directoryOptions == null) {
			directoryOptions = new LibSequenceDirectoryOptions(true, true);
		}

		sequencerAPI.preregisterSection(ownerToken, sectionName);
		contributors.put(ownerToken, directoryOptions);
		if (directoryOptions.includeAll()) {
			syncAllMySequences(ownerToken);
		}
	}

	/**
	 * Remove a Directory contributor
	 */
	public void unregisterContributor(LibSequenceToken ownerToken) {
		clearAllMySequences(ownerToken);
		contributors.remove(ownerToken);
	}

	/**
	 * Check if a given section identified by its ownerToken exists.
	 */
	public boolean hasDirectoryContributor(LibSequenceToken ownerToken) {
		return contributors.containsKey(ownerToken);
	}

	/**
	 * Register an new sequence name. You cannot register a name which is already
	 * registered for another contributor (ownerToken/plugin) except the old
	 * register is no longer valid. You need to call this function manually if in
	 * the directory options you had decided to not publish all sequences
	 * automatically.
	 */
	public void addSequence(LibSequenceToken ownerToken, String sequenceName) throws LibSequenceDirectoryException {
		if ((sequenceName == null) || (sequenceName.isEmpty())) {
			throw new LibSequenceDirectoryException(null, LSDERR_NAME_IS_EMPTY, null);
		}
		if (ownerToken == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_TOKEN_IS_NULL, sequenceName);
		}
		if (!hasDirectoryContributor(ownerToken)) {
			throw new LibSequenceDirectoryException(null, LSDERR_OWNERTOKEN_NOT_REGISTERED, sequenceName);
		}

		LibSequenceConfigSection mySection = sequencerAPI.findOwnSection(ownerToken);
		if (mySection == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CONFIG_HAS_GONE, sequenceName);
		}

		LibSequenceToken oldOwnerToken = sequences.get(sequenceName);
		if (oldOwnerToken != null) {
			if (oldOwnerToken.equals(ownerToken)) {
				return;
			}
			if (sequencerAPI.hasOwnSequence(oldOwnerToken, sequenceName)) {
				throw new LibSequenceDirectoryException(sequenceName, LSDERR_SEQUENCE_ALREADY_REGISTERED,
						mySection.getSectionName());
			}
		}
		sequences.put(sequenceName, ownerToken);
	}

	/**
	 * Remove a given sequence from the directory
	 */
	public void removeSequence(LibSequenceToken ownerToken, String sequenceName) throws LibSequenceDirectoryException {
		LibSequenceConfigSection mySection = sequencerAPI.findOwnSection(ownerToken);
		if (mySection == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CONFIG_HAS_GONE, sequenceName);
		}

		LibSequenceToken oldOwnerToken = sequences.get(sequenceName);
		if (oldOwnerToken == null) {
			return;
		}

		if (!oldOwnerToken.equals(ownerToken) && sequencerAPI.hasOwnSequence(oldOwnerToken, sequenceName)) {
			throw new LibSequenceDirectoryException(sequenceName, LSDERR_ONLY_OWNER_CAN_REMOVE,
					mySection.getSectionName());
		}
		sequences.remove(sequenceName);
	}

	/**
	 * Remove all sequences from the directory which belongs to the section
	 * identified by the ownerToken
	 */
	public int clearAllMySequences(LibSequenceToken ownerToken) {
		int count = 0;
		Iterator<LibSequenceToken> myTokens = sequences.values().iterator();
		while (myTokens.hasNext()) {
			LibSequenceToken mySequenceToken = myTokens.next();
			if (mySequenceToken.equals(ownerToken)) {
				myTokens.remove();
				count = count + 1;
			}
		}
		return count;
	}

	/**
	 * Update the directory after the section has changed
	 */
	protected int syncAllMySequences(LibSequenceToken ownerToken) throws LibSequenceDirectoryException {
		LibSequenceConfigSection mySection = sequencerAPI.findOwnSection(ownerToken);
		if (mySection == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CONFIG_HAS_GONE, null);
		}

		clearAllMySequences(ownerToken);
		int count = 0;
		Set<String> sequenceNames = new HashSet<>(mySection.getSequenceNames());
		LibSequenceDirectoryException lastDirectoryException = null;

		for (String mySequenceName : sequenceNames) {
			try {
				addSequence(ownerToken, mySequenceName);
				count = count + 1;
			} catch (LibSequenceDirectoryException e) {
				lastDirectoryException = e;
			}
		}

		if (lastDirectoryException != null) {
			throw lastDirectoryException;
		}
		return count;
	}

	/**
	 * Check if the directory contains a sequence with the given name
	 */
	public boolean hasRunnableSequence(String sequenceName) {
		LibSequenceToken myOwnerToken = sequences.get(sequenceName);
		if (myOwnerToken == null) {
			return false;
		}
		return sequencerAPI.hasOwnSequence(myOwnerToken, sequenceName);
	}

	/**
	 * Check if a given sequence belongs to the section identified by the ownerToken
	 */
	public boolean isMySequence(LibSequenceToken ownerToken, String sequenceName) {
		return sequencerAPI.hasOwnSequence(ownerToken, sequenceName);
	}

	/**
	 * Get the owner (plugin name) of a given sequence. The owner is the return
	 * value of the "getOwnerName" Function of the associated ownerToken.
	 */
	public String getSequenceOwnerName(String sequenceName) throws LibSequenceDirectoryException {
		LibSequenceToken ownerToken = sequences.get(sequenceName);
		if (ownerToken == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}

		LibSequenceConfigSection mySection = sequencerAPI.findOwnSection(ownerToken);
		if (mySection == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CONFIG_HAS_GONE, sequenceName);
		}

		return mySection.getSectionName();
	}

	/**
	 * Get a list of all sequences contained in this directory. Only sequences that
	 * has passed the syntax-check are returned.
	 */
	public Set<String> getRunnableSequenceNames() {
		Set<String> runnableSequenceNames = new HashSet<>();

		for (Map.Entry<String, LibSequenceToken> entry : sequences.entrySet()) {
			String mySequenceName = entry.getKey();
			LibSequenceToken myOwnerToken = entry.getValue();
			if (sequencerAPI.hasOwnSequence(myOwnerToken, mySequenceName)) {
				runnableSequenceNames.add(mySequenceName);
			}
		}
		return runnableSequenceNames;
	}

	/**
	 * Get a list of all sequences contained in this directory, regardless if they
	 * can be started ore not.
	 */
	public Set<String> getAllSequenceNames() {
		return new HashSet<>(sequences.keySet());
	}

	/**
	 * Run a sequence from the directory
	 */
	public LibSequenceRunningSequence execute(LibSequenceToken runnerToken, String sequenceName,
			LibSequenceRunOptions runOptions) throws LibSequenceDirectoryException {
		LibSequenceToken ownerToken = sequences.get(sequenceName);
		if (ownerToken == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}

		if (!sequencerAPI.hasOwnSequence(ownerToken, sequenceName)) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_RUNNABLE, sequenceName);
		}

		try {
			LibSequenceToken securityToken = sequencerAPI.getSecurityToken(ownerToken, sequenceName);
			return sequencerAPI.executeForeignSequence(runnerToken, securityToken, runOptions);
		} catch (LibSequenceException e) {
			throw new LibSequenceDirectoryException(sequenceName, LSDERR_ERROR_DURING_SEQUENCE_START, null, e);
		}
	}

	/**
	 * Get a list of all sequences which are currently running and cancelable
	 */
	public List<LibSequenceRunningSequence> findRunningSequences(String sequenceName) {
		List<LibSequenceRunningSequence> allSequences = new ArrayList<>();
		LibSequenceToken ownerToken = sequences.get(sequenceName);
		if (ownerToken == null) {
			return allSequences;
		}

		LibSequenceDirectoryOptions directoryOptions = contributors.get(ownerToken);
		if (!directoryOptions.canCancel()) {
			return allSequences;
		}

		List<LibSequenceRunningSequence> sectionSequences = sequencerAPI.sneakRunningSequencesOwnedByMe(ownerToken);
		for (LibSequenceRunningSequence mySequence : sectionSequences) {
			if (sequenceName.equals(mySequence.getName())) {
				allSequences.add(mySequence);
			}
		}
		return allSequences;
	}

	/**
	 * Cancel all currently running sequences with the given name if the contributor
	 * has allowed this.
	 */
	public int cancel(String sequenceName) throws LibSequenceDirectoryException {
		LibSequenceToken ownerToken = sequences.get(sequenceName);
		if (ownerToken == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}

		LibSequenceDirectoryOptions directoryOptions = contributors.get(ownerToken);
		if (!directoryOptions.canCancel()) {
			throw new LibSequenceDirectoryException(null, LSDERR_ACCESS_DENIED, sequenceName);
		}

		List<LibSequenceRunningSequence> allSequences = findRunningSequences(sequenceName);
		for (LibSequenceRunningSequence sequence : allSequences) {
			sequence.cancel();
		}

		return allSequences.size();
	}

	/**
	 * UpdateDirectroyCache from all section where the contributor has allowed this
	 * by setting includeAll=true. This is normally called automatically from the
	 * ReloaderManager. So if you manage your Sequence Configuration with
	 * ReloadManager, you don't need to call this.
	 *
	 * @throws LibSequenceDirectoryException
	 */
	public void refreshDirectory() throws LibSequenceDirectoryException {
		LibSequenceDirectoryException lastDirectoryException = null;
		for (Map.Entry<LibSequenceToken, LibSequenceDirectoryOptions> entry : contributors.entrySet()) {
			LibSequenceToken myOwnerToken = entry.getKey();
			LibSequenceDirectoryOptions myDirectoryOptions = entry.getValue();
			if (myDirectoryOptions.includeAll())
				try {
					syncAllMySequences(myOwnerToken);
				} catch (LibSequenceDirectoryException e) {
					lastDirectoryException = e;
				}
		}
		if (lastDirectoryException != null) {
			throw lastDirectoryException;
		}
	}

	protected void onReloadedEvent(LibSequenceActionValidator actionValidator, LibSequenceException lastException) {
		if (!sequencerAPI.getActionValidator().isSameInstance(actionValidator)) {
			return;
		}
		try {
			refreshDirectory();
		} catch (Exception e) {
			if (lastException == null) {
				e.printStackTrace();
			}
		}
	}

	public int reload() throws LibSequenceException {
		return sequencerAPI.reload();
	}

	public int addSequencesFromFile(Plugin plugin, LibSequenceToken ownerToken, String fileName, String fileSection)
			throws LibSequenceConfigException, LibSequenceDirectoryException {
		if (hasDirectoryContributor(ownerToken)) {
			throw new LibSequenceDirectoryException(plugin.getName(), LSDERR_OWNERTOKEN_IN_USE, null);
		}

		LibSequenceDirectoryOptions directoryOptions = new LibSequenceDirectoryOptions(true, true);
		registerContributor(ownerToken, plugin.getName(), directoryOptions);
		return sequencerAPI.attachConfigFileToReloader(plugin, ownerToken, fileName, fileSection);
	}

	public boolean addSequencesFromFileLater(Plugin plugin, LibSequenceToken ownerToken, String fileName,
			String fileSection) {
		if (hasDirectoryContributor(ownerToken)) {
			return false;
		}
		LibSequenceDirectoryOptions directoryOptions = new LibSequenceDirectoryOptions(true, true);
		try {
			registerContributor(ownerToken, plugin.getName(), directoryOptions);
		} catch (Exception e) {
			return false;
		}
		sequencerAPI.attachConfigFileToReloaderLater(plugin, ownerToken, fileName, fileSection);
		return true;
	}

	public int partialReloadFromConfigFile(LibSequenceToken ownerToken) throws LibSequenceException {
		return sequencerAPI.partialReloadFromConfigFile(ownerToken);
	}

	public final boolean isDisabled() {
		return bDisabled;
	}

	protected final void setDisabled() {
		bDisabled = true;
	}

	public void disable() {
		if (!isDisabled()) {
			if (directoryListener != null) {
				directoryListener.unregisterListener();
				directoryListener = null;
			}
			setDisabled();
			sequences.clear();
			contributors.clear();
		}
	}
}
