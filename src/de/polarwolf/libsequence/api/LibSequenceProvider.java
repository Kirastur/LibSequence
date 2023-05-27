package de.polarwolf.libsequence.api;

/**
 * Provie access to the shared instance of the LibSequence
 *
 */
public class LibSequenceProvider {

	private static LibSequenceAPI lsAPI = null;

	/**
	 * The class is all-static, so prohibit creating an instance
	 */
	private LibSequenceProvider() {
	}

	/**
	 * Get the API of the shared LibSequence Instance
	 *
	 * @return API
	 */
	public static LibSequenceAPI getAPI() {
		return lsAPI;
	}

	/**
	 * Resister a LibSequenceAPI as shared instance
	 *
	 * @param newAPI new LibSequence Instance
	 * @return TRUE if the given LibSequence instance could be set for global
	 *         access, else FALSE
	 */
	public static boolean setAPI(LibSequenceAPI newAPI) {
		if ((lsAPI != null) && !lsAPI.isDisabled()) {
			return false;
		}
		lsAPI = newAPI;
		return true;
	}

}
