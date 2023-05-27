package de.polarwolf.libsequence.api;

import de.polarwolf.libsequence.directories.LibSequenceDirectory;
import de.polarwolf.libsequence.orchestrator.LibSequenceSequencer;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Centralized AIP-Access
 *
 */
public class LibSequenceAPI {

	private final LibSequenceSequencer sequencer;
	private final LibSequenceDirectory directory;
	private final LibSequenceController controller;
	private final LibSequenceToken apiToken;

	/**
	 * Generate a valid LibSequenceAPI object by putting all three APIs together.
	 * This object can then be used to register itself to the LibSequenceProvider
	 *
	 * @param apiToken   Set the apiToken which must be used later to shutdown the
	 *                   LibSequence instance
	 * @param sequencer  Existing sequencerAPI (required)
	 * @param directory  Existing directoryAPI (may be NULL)
	 * @param controller Existing controllerAPI (may be NULL)
	 */
	public LibSequenceAPI(LibSequenceToken apiToken, LibSequenceSequencer sequencer, LibSequenceDirectory directory,
			LibSequenceController controller) {
		this.apiToken = apiToken;
		this.sequencer = sequencer;
		this.directory = directory;
		this.controller = controller;
	}

	/**
	 * Get the Sequencer API
	 *
	 * @return sequencerAPI
	 */
	public LibSequenceSequencer getSequencer() {
		return sequencer;
	}

	/**
	 * Get the Directory API
	 *
	 * @return directoryAPI
	 */
	public LibSequenceDirectory getPublicDirectory() {
		return directory;
	}

	/**
	 * Get the Controller API
	 *
	 * @return controllerAPI
	 */
	public LibSequenceController getController() {
		return controller;
	}

	/**
	 * Check if the current LibSequence Instance is already disabled
	 *
	 * @return TRUE if disabled an no new sequences can be started, otherwise FALSE
	 */
	public boolean isDisabled() {
		return sequencer.isDisabled();
	}

	/**
	 * Disable the current LibSequenceInstance
	 *
	 * @param currentApiToken the apiToken you have set when you have created the
	 *                        instance
	 * @return TRUE if the apiTokeb was correct and therefore the Instance has shut
	 *         down, otherwise FALSE
	 */
	public boolean disable(LibSequenceToken currentApiToken) {
		if (!apiToken.equals(currentApiToken)) {
			return false;
		}

		boolean result = true;
		if (directory != null) {
			result = directory.disable(apiToken) && result;
		}
		result = sequencer.disable(apiToken) && result;
		return result;
	}

}
