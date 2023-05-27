package de.polarwolf.libsequence.orchestrator;

import static de.polarwolf.libsequence.runnings.LibSequenceRunErrors.LSRERR_NOT_FOUND;

import java.util.List;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.actions.LibSequenceAction;
import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.chains.LibSequenceChain;
import de.polarwolf.libsequence.checks.LibSequenceCheck;
import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.config.LibSequenceConfigSequence;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.exception.LibSequenceExceptionJava;
import de.polarwolf.libsequence.includes.LibSequenceInclude;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholder;
import de.polarwolf.libsequence.reload.LibSequenceReloader;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * The SequencerAPI gives you more control over the sequeces. You can use your
 * own set of sequences and manage them isolated from other plugins. So this is
 * the main way if you want to use sequences as it is desiged for - as a library
 * for your plugin.
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/SequencerAPI">Sequencer
 *      API</A> (WIKI)
 *
 */
public class LibSequenceSequencer {

	protected final LibSequenceToken apiToken;
	protected final LibSequenceOrchestrator orchestrator;

	/**
	 * Builds a new sequencerAPI object. Only needed during startup.
	 *
	 * @param plugin       In this context the Bukkit Scheduled Task is executed
	 * @param apiToken     Set the apiToken which must be used later to shutdown the
	 *                     sequencer
	 * @param startOptions Start options
	 * @throws LibSequenceException Registering one of the predefined actions,
	 *                              integration etc. failed
	 */
	public LibSequenceSequencer(Plugin plugin, LibSequenceToken apiToken, LibSequenceStartOptions startOptions)
			throws LibSequenceException {
		this.apiToken = apiToken;
		orchestrator = createOrchestrator(plugin, startOptions);
		try {
			orchestrator.startup();
		} catch (Exception e) {
			orchestrator.disable();
			if (e instanceof LibSequenceException) {
				throw e;
			}
			throw new LibSequenceExceptionJava("Orchestrator initialization", e);
		}
	}

	// RunManager Interface
	/**
	 * Start a foreign sequence. The sequence is identified by its SecurityToken.
	 * The sequence is running in LibSequence plugin context.
	 *
	 * @param runnerToken   A token which you set to declare you as the owner of the
	 *                      running sequence instance.
	 * @param securityToken A token which you have received from the owner of the
	 *                      sequence to proof you are allowed to start the sequence
	 * @param runOptions    Options you can pass to the sequence, e.g. initiator,
	 *                      logger and authorization tokens
	 * @return The instance of the newly started sequence
	 * @throws LibSequenceRunException An exception is thrown if the security token
	 *                                 does not match to any sequence or the
	 *                                 sequence could not be started.
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/runOptions">runOptions</A>
	 *      (WIKI)
	 */
	public LibSequenceRunningSequence executeForeignSequence(LibSequenceToken runnerToken,
			LibSequenceToken securityToken, LibSequenceRunOptions runOptions) throws LibSequenceRunException {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		if (sequence == null) {
			throw new LibSequenceRunException(null, 0, LSRERR_NOT_FOUND, null);
		}
		return orchestrator.getRunManager().execute(runnerToken, sequence, securityToken, runOptions);
	}

	/**
	 * Start a sequence with the given name. For authorization you must provide your
	 * ownerToken. Only sequences contained in the section associated with this
	 * ownerToken can be started by this way. If you want to start other sequences,
	 * you need the securityToken from the owner.
	 *
	 * @param ownerToken   A token which proofs you as the owner of the sequence. It
	 *                     was set when loading the sequence. The ownerToken is also
	 *                     used as the runnerToken
	 * @param sequenceName Name of the sequence to start
	 * @param runOptions   Options you can pass to the sequence, e.g. initiator,
	 *                     logger and authorization tokens
	 * @return The instance of the newly started sequence
	 * @throws LibSequenceRunException An exception is thrown if the security token
	 *                                 does not match to any sequence or the
	 *                                 sequence could not be started.
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/runOptions">runOptions</A>
	 *      (WIKI)
	 */
	public LibSequenceRunningSequence executeOwnSequence(LibSequenceToken ownerToken, String sequenceName,
			LibSequenceRunOptions runOptions) throws LibSequenceRunException {
		try {
			LibSequenceConfigSequence sequence = orchestrator.getConfigManager().getOwnSequence(ownerToken,
					sequenceName);
			LibSequenceToken securityToken = sequence.getSecurityToken(ownerToken);
			return orchestrator.getRunManager().execute(ownerToken, sequence, securityToken, runOptions);
		} catch (LibSequenceConfigException e) {
			throw new LibSequenceRunException(null, 0, LSRERR_NOT_FOUND, null, e);
		}
	}

	/**
	 * Start the given sequence. This is a low-level-function. You must provide the
	 * sequence itself, so it dosn't matter if the sequence is contained in a
	 * ConfigSection, Directory or other container.
	 *
	 * @param runnerToken    A token which you set to declare you as the owner of
	 *                       the running sequence instance.
	 * @param configSequence Sequence to execute
	 * @param securityToken  A token which you have received from the owner of the
	 *                       sequence to proof you are allowed to start the sequence
	 * @param runOptions     Options you can pass to the sequence, e.g. initiator,
	 *                       logger and authorization tokens
	 * @return The instance of the newly started sequence
	 * @throws LibSequenceRunException An exception is thrown if the security token
	 *                                 does not match to any sequence or the
	 *                                 sequence could not be started.
	 */
	public LibSequenceRunningSequence executeSequence(LibSequenceToken runnerToken,
			LibSequenceConfigSequence configSequence, LibSequenceToken securityToken, LibSequenceRunOptions runOptions)
			throws LibSequenceRunException {
		return orchestrator.getRunManager().execute(runnerToken, configSequence, securityToken, runOptions);
	}

	/**
	 * Cancel the given sequence.
	 *
	 * @param runningSequence Instance to cancel
	 * @throws LibSequenceRunException Throws an exception if the given sequence is
	 *                                 not running (e.g. already finished)
	 */
	public void cancelSequence(LibSequenceRunningSequence runningSequence) throws LibSequenceRunException {
		orchestrator.getRunManager().cancel(runningSequence);
	}

	/**
	 * Cancel all sequences with a given name. You can only cancel sequences which
	 * you own. The return value is the number of sequences cancelled.
	 *
	 * @param runnerToken  For authorization you must provide your
	 *                     runnerToken-object.
	 * @param sequenceName Name of the sequence which should be cancelled
	 * @return Number of sequences cancelled
	 */
	public int cancelSequenceByName(LibSequenceToken runnerToken, String sequenceName) {
		return orchestrator.getRunManager().cancelByName(runnerToken, sequenceName);
	}

	/**
	 * List all currently running sequences which where started by the given
	 * runnerToken, regardless of the sequence-owner.
	 *
	 * @param runnerToken Identify the sequences you are querying about.
	 * @return List of running sequences started by runnerToken
	 */
	public List<LibSequenceRunningSequence> findRunningSequences(LibSequenceToken runnerToken) {
		return orchestrator.getRunManager().findRunningSequences(runnerToken);
	}

	/**
	 * List all currently running sequences which are owned by the given Token,
	 * regardless who has stared them.
	 *
	 * @param ownerToken Identify the sequences you are querying about.
	 * @return List of running sequences owned by ownerToken
	 */
	public List<LibSequenceRunningSequence> sneakRunningSequencesOwnedByMe(LibSequenceToken ownerToken) {
		return orchestrator.getRunManager().sneakRunningSequencesOwnedByMe(ownerToken);
	}

	// ConfigManager Interface
	/**
	 * Register an empty session. This is normally done, if you need a
	 * section-object (e.g. for directory registrations) but want to fill the
	 * section with sequences later. If a section for the ownerToken already exists,
	 * nothing is changed.
	 *
	 * @param ownerToken  Set the owner of the new session
	 * @param sectionName Name of the section, used only for errormessages
	 */
	public void preregisterSection(LibSequenceToken ownerToken, String sectionName) {
		orchestrator.getConfigManager().preregisterSection(ownerToken, sectionName);
	}

	/**
	 * Add or replace a section. If in ConfigManager no section is associated with
	 * the given owner token, the section is added as new section. If the ownerToken
	 * already has a section associated, the old section is thrown away and replaced
	 * by the new one. This affects only new sequences, all existing running
	 * sequences will continue using the old sequence definition until they are
	 * finished.
	 *
	 * @param ownerToken Set the owner to whom you want to add or replace the
	 *                   section
	 * @param newSection A fully setup section filled with sequences
	 * @throws LibSequenceConfigException If the new section is null or has a syntax
	 *                                    error.
	 */
	public void setSection(LibSequenceToken ownerToken, LibSequenceConfigSection newSection)
			throws LibSequenceConfigException {
		orchestrator.getConfigManager().setSection(ownerToken, newSection);
	}

	/**
	 * Remove a section from the ConfigManager. All sequences of this sections
	 * become invalid. This is only used if your 3rd-party-plugin wants to perform a
	 * manual cleanup.
	 *
	 * @param ownerToken The ownerToken with is associated with the section you want
	 *                   to clean up
	 * @throws LibSequenceConfigException An exception is thrown if the section
	 *                                    could not be removed, e.g. if it does not
	 *                                    exists.
	 */
	public void unregisterSection(LibSequenceToken ownerToken) throws LibSequenceConfigException {
		orchestrator.getConfigManager().unregisterSection(ownerToken);
	}

	/**
	 * Check of the given ownerTpken has already a section associated.
	 *
	 * @param ownerToken ownerToken to Check
	 * @return TRUE if a section was found, otherwise FALSE
	 */
	public boolean hasSection(LibSequenceToken ownerToken) {
		return orchestrator.getConfigManager().hasSection(ownerToken);
	}

	/**
	 * Get the section associated with the given ownerToken.
	 *
	 * @param ownerToken ownerToken to search for
	 * @return The associated section if one is found, otherwise NULL
	 */
	public LibSequenceConfigSection findOwnSection(LibSequenceToken ownerToken) {
		return orchestrator.getConfigManager().findOwnSection(ownerToken);
	}

	/**
	 * Request a security token which is needed to start the sequence. Then you can
	 * handover this token to another plugin. With this SecurityToken that plugin
	 * can start the sequence even if he has no knowledge about the ownerToken.
	 *
	 * @param ownerToken   The ownerToken with is associated with the section you
	 *                     want to access
	 * @param sequenceName Name of the sequence you are requesting the securityToken
	 *                     for
	 * @return The securityToken which is needed to execute the sequence
	 * @throws LibSequenceConfigException An exception is thrown if the sequence
	 *                                    with the given name does not exists in
	 *                                    your section. This can happens if the
	 *                                    sequence is not loaded due to a syntax
	 *                                    error.
	 *
	 */
	public LibSequenceToken getSecurityToken(LibSequenceToken ownerToken, String sequenceName)
			throws LibSequenceConfigException {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().getOwnSequence(ownerToken, sequenceName);
		return sequence.getSecurityToken(ownerToken);
	}

	/**
	 * Test if a given security token is valid, this means if it fits to one
	 * sequence, regardless of the sequence owner or the section where it is
	 * contained in.
	 *
	 * @param securityToken securityTokek to check
	 * @return TRUE if the securityToken identifies a sequence, otherwise FALSE
	 */
	public boolean hasForeignSequence(LibSequenceToken securityToken) {
		LibSequenceConfigSequence sequence = orchestrator.getConfigManager().findForeignSequence(securityToken);
		return (sequence != null);
	}

	/**
	 * Checks if the section contains a sequence with the given name. Please
	 * remember: Sequences with syntax error or which are not executable for any
	 * reason are not listed anywhere, so this is a valid mechanism to check if a
	 * session was loaded properly before using it.
	 *
	 * @param ownerToken   The ownerToken which is associated to the section you
	 *                     want to access
	 * @param sequenceName Name of the sequence to check
	 * @return TRUE if a sequence with the given name exists in the section
	 *         identified by the ownerToken, otherwise FALSE
	 */
	public boolean hasOwnSequence(LibSequenceToken ownerToken, String sequenceName) {
		return orchestrator.getConfigManager().hasOwnSequence(ownerToken, sequenceName);
	}

	/**
	 * Return a list of all valid sequences contained in the section.
	 *
	 * @param ownerToken The ownerToken which is associated to the section you want
	 *                   to access
	 * @return List of all sequence names contained in the given section
	 * @throws LibSequenceConfigException If no section exists with the given
	 *                                    ownerToken
	 */
	public Set<String> getSequenceNames(LibSequenceToken ownerToken) throws LibSequenceConfigException {
		return orchestrator.getConfigManager().getSequenceNames(ownerToken);
	}

	// ReloadManager
	/**
	 * Reload all sequences.
	 *
	 * @return Number of sequences loaded
	 * @throws LibSequenceException An exception is thrown if any of the
	 *                              section-reloads detect a failure (e.g. a syntax
	 *                              error in sequence definition).
	 */
	public int reload() throws LibSequenceException {
		if (!isDisabled()) {
			return orchestrator.getReloadManager().reload();
		} else {
			return 0;
		}

	}

	/**
	 * Attach a new Reloader
	 *
	 * @param newReloader New Reloader to add
	 */
	public void registerReloader(LibSequenceReloader newReloader) {
		orchestrator.getReloadManager().add(newReloader);
	}

	/**
	 * Remove an existing reloader
	 *
	 * @param oldReloader Existing reloader to remove
	 */
	public void unregisterReloader(LibSequenceReloader oldReloader) {
		orchestrator.getReloadManager().remove(oldReloader);
	}

	/**
	 * Add a section to the reloader. The section gets automatically loaded.
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
	 * @throws LibSequenceConfigException Thrown if there is an error with loading
	 *                                    the sequences, e.g. syntax error
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
	 *      definition</A> (WIKI)
	 *
	 */
	public int attachConfigFileToReloader(Plugin plugin, LibSequenceToken ownerToken, String fileName,
			String fileSection) throws LibSequenceConfigException {
		return orchestrator.getReloaderConfigFile().add(plugin, ownerToken, fileName, fileSection);
	}

	/**
	 * Add a section to the reloader at next tick. The section gets automatically
	 * loaded.
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
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Tokens">Tokens</A> (WIKI)
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
	 *      definition</A> (WIKI)
	 *
	 */
	public void attachConfigFileToReloaderLater(Plugin plugin, LibSequenceToken ownerToken, String fileName,
			String fileSection) {
		orchestrator.getReloaderConfigFile().addLater(plugin, ownerToken, fileName, fileSection);
	}

	/**
	 * Remove a config file section from the reloader.
	 *
	 * @param ownerToken Identifies the section you want to remove
	 */
	public void detachConfigFileFromReloader(LibSequenceToken ownerToken) {
		orchestrator.getReloaderConfigFile().remove(ownerToken);
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
		if (!isDisabled()) {
			return orchestrator.getReloaderConfigFile().partialReload(ownerToken);
		} else {
			return 0;
		}
	}

	// ActionManager
	/**
	 * Get the action validator of the LibSequence Instance. The action validator is
	 * needed to build a sequence (syntax check) and is used as an identifier that
	 * the sequence belongs to the current instance of LibSequence.
	 *
	 * @return The ActionValidator of this LibSequence Instance
	 */
	public LibSequenceActionValidator getActionValidator() {
		return orchestrator.getActionManager().getActionValidator();
	}

	/**
	 * Register a new action. The _actionName_ will be the value of the "action"
	 * attribute in a sequence step. The _action_ is the object handling the action.
	 * There is no way to remove an action once it was registered.
	 *
	 * @param actionName Will be used as parameter in the "action" step
	 * @param action     Object handling the action
	 * @throws LibSequenceActionException If an Action with the given name already
	 *                                    exists
	 */
	public void registerAction(String actionName, LibSequenceAction action) throws LibSequenceActionException {
		orchestrator.getActionManager().registerAction(actionName, action);
	}

	// PlaceholderManager
	/**
	 * Register a new placeholder resolver.
	 *
	 * @param placeholder new Placeholder resolver
	 */
	public void registerPlaceholder(LibSequencePlaceholder placeholder) {
		orchestrator.getPlaceholderManager().registerPlaceholder(placeholder);
	}

	// CheckManager
	/**
	 * Register a new check which can be used in checks ("check_* ")
	 *
	 * @param checkName Will be used as check name
	 * @param check     Object handling this check
	 * @throws LibSequenceCheckException If a check with the given name already
	 *                                   exists
	 */
	public void registerCheck(String checkName, LibSequenceCheck check) throws LibSequenceCheckException {
		orchestrator.getCheckManager().registerCheck(checkName, check);
	}

	// IncludeManager
	/**
	 * Register an new include which can be used in include ("include_*")
	 *
	 * @param includeName Will be used as include name
	 * @param include     Object handling this include
	 * @throws LibSequenceIncludeException If an include with the given name already
	 *                                     exists
	 */
	public void registerInclude(String includeName, LibSequenceInclude include) throws LibSequenceIncludeException {
		orchestrator.getIncludeManager().registerInclude(includeName, include);
	}

	// ChainManager
	/**
	 * Register a new chain resolver
	 *
	 * @param chain new chain resolver
	 */
	public void registerChain(LibSequenceChain chain) {
		orchestrator.getChainManager().registerChain(chain);
	}

	// IntegrationManager Interface
	/**
	 * Check if the PlaceholderAPI plugin was found. The Placeholder resolver can
	 * use the PlaceholderAPI to resolve foreign %...% placeholders.
	 *
	 * @return TRUE true if PlaceholderAPI was detected, otherwise FALSE
	 */
	public boolean hasIntegrationPlaceholderAPI() {
		return orchestrator.getIntegrationManager().hasPlaceholderAPI();
	}

	/**
	 * Check if the Worldguard plugin was found. The "check" action can use the
	 * "check_region" Attribute to verify if the player stays inside the given
	 * region.
	 *
	 * @return TRUE if Worldguard was detected, otherwise FALSE
	 */
	public boolean hasIntegrationWorldguard() {
		return orchestrator.getIntegrationManager().hasWorldguard();
	}

	/**
	 * Internally used for startup
	 */
	protected LibSequenceOrchestrator createOrchestrator(Plugin plugin, LibSequenceStartOptions startOptions) {
		return new LibSequenceOrchestrator(plugin, startOptions);
	}

	/**
	 * Check if the sequencer has already shut down (e.g. because server is
	 * stopping). In this case you cannot start new sequences.
	 *
	 * @return TRUE if the sequencer is disabled and cannot start new sequences,
	 *         otherwise FALSE
	 */
	public boolean isDisabled() {
		return orchestrator.isDisabled();
	}

	/**
	 * Used internally to perform a clean shutdown
	 */
	public boolean disable(LibSequenceToken currentApiToken) {
		if (!apiToken.equals(currentApiToken)) {
			return false;
		}
		orchestrator.disable();
		return true;
	}

}
