package de.polarwolf.libsequence.config;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_ACTION;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_KEY_SYNTAX_ERROR;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_MISSING_ACTION;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_WAIT_NOT_NUMERIC;

// The tree is: Manager ==> Section ==> Sequence ==> Step

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.libsequence.actions.LibSequenceActionException;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;

/**
 * Defines a single step in a sequence. It's organized as an unordered list of
 * name/value pairs, called "attributes". A syntax check validates the
 * attributes during load.
 *
 * @see de.polarwolf.libsequence.config.LibSequenceConfigSequence
 *      LibSequenceConfigSequence
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Configuration">Configuration</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
 *      Definition</A> (WIKI)
 */

public class LibSequenceConfigStep {

	/**
	 * Name of the default attribute containing the wait-time ("wait-after-action")
	 */
	public static final String KEYNAME_WAIT = "wait-after-action";

	/**
	 * Name of the default attribute containing the action ("action")
	 */
	public static final String KEYNAME_ACTION = "action";

	// Please use the public getter for this
	// Perhaps someone wants to override it
	private final String sequenceName;
	private final int stepNr;

	/**
	 * The actionValidator identifies the LibSequence-Instance this step belongs to.
	 * It is called during syntax check.
	 */
	protected final LibSequenceActionValidator actionValidator;

	/**
	 * Container for the Name/Value pairs of the step
	 */
	protected final Map<String, String> attributes = new HashMap<>();

	/**
	 * Build this step by loading it from file (for example config.yml).
	 *
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param sequenceName    Name of the sequence for building error messages
	 * @param stepNr          Position of this step in the sequence (for building
	 *                        error messages)
	 * @param config          Configuration Section to load the step from
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr,
			ConfigurationSection config) throws LibSequenceConfigException {
		this.actionValidator = actionValidator;
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		loadStepFromConfig(config);
	}

	/**
	 * Build this step from an in-memory list of name/value pairs
	 *
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param sequenceName    Name of the sequence for building error messages
	 * @param stepNr          Position of this step in the sequence (for building
	 *                        error messages)
	 * @param config          List if Name/Value-Pairs containing the attributes
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigStep(LibSequenceActionValidator actionValidator, String sequenceName, int stepNr,
			Map<String, String> config) throws LibSequenceConfigException {
		this.actionValidator = actionValidator;
		this.sequenceName = sequenceName;
		this.stepNr = stepNr;
		loadStepFromMap(config);
	}

	/**
	 * Helper function to load the attributes from file
	 */
	// The bukkit ConfigurationSection is not able to detect duplicate keys
	// so we cannot check for it
	protected void loadStepFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (String keyName : config.getKeys(false)) {
			String valueText = config.getString(keyName);
			if (valueText == null) {
				throw new LibSequenceConfigException(null, getSequenceName(), getStepNr(), LSCERR_KEY_SYNTAX_ERROR,
						keyName);
			}
			attributes.put(keyName, valueText);
		}
	}

	/**
	 * Helper function to load the attributes from a Map structure
	 */
	protected void loadStepFromMap(Map<String, String> config) throws LibSequenceConfigException {
		for (Entry<String, String> entry : config.entrySet()) {
			String keyName = entry.getKey();
			String valueText = entry.getValue();
			if (valueText == null) {
				throw new LibSequenceConfigException(null, getSequenceName(), getStepNr(), LSCERR_KEY_SYNTAX_ERROR,
						keyName);
			}
			attributes.put(keyName, valueText);
		}
	}

	/**
	 * Multi-Instance-Check. You can have more than one instance of the LibSequence
	 * Orchestrator running (e.g. for private sequences). But we must take care that
	 * a step is always executed in the instance it was created. This is done by
	 * comparing the instance-specific ActionValidator.<BR>
	 * This is not a security feature because the ActionValidator is public.
	 *
	 * @param actionValidatorToTest Foreign ActionValidator to compare with the
	 *                              step's own ActionValidator
	 * @return TRUE if both belong to the same instance (so the step is valid to be
	 *         executed), FALSE otherwise.
	 */
	public final boolean isSameInstance(LibSequenceActionValidator actionValidatorToTest) {
		return actionValidator.isSameInstance(actionValidatorToTest);
	}

	/**
	 * Get the name of the sequence this step belongs to
	 *
	 * @return Sequence-name as set during object-creation
	 */
	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * Get the position of this step in the sequence
	 *
	 * @return Position as defined during object-creation
	 */
	public int getStepNr() {
		return stepNr;
	}

	/**
	 * Get the value of an attribute. A step is an unordered List of attributes.
	 * Here you can search this list for a specific attribute identified by its
	 * name.
	 *
	 * @param keyName Attribute-Name to search for
	 * @return Attribute-value if a matching attribute was found, NULL otherwise
	 */
	public String findValue(String keyName) {
		return attributes.get(keyName);
	}

	/**
	 * Get the value of a multilanguage-attribute. Attributes can have values in
	 * different languages (e.g. for sending a message to a player). Here you can
	 * search for an attribute and get the localized value.
	 *
	 * @param keyName Attribute-Name to search for
	 * @param locale  Locale to search for, This can be 5-digit (e.g. "de_de"),
	 *                2-digit (e.g. "de") or NULL if the locale is not known.
	 * @return Attribute-value in the given locale. It first tries to get the full
	 *         5-digit locale, if not found cut of the last 3 digits and try to get
	 *         the 2-digit locale, and if not found try to get the non-multilanguage
	 *         value. Returns NULL if none of them found
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Multilanguage">Multilanguage</A>
	 *      (WIKI)
	 */
	public String findValueLocalized(String keyName, String locale) {
		if (locale != null) {

			// 1st try: take the full language (e.g. "de_de")
			if (locale.length() >= 5) {
				String s = findValue(keyName + "_" + locale.substring(0, 5));
				if (s != null) {
					return s;
				}
			}

			// 2nd try: take the group language (e.g. "de")
			if (locale.length() >= 2) {
				String s = findValue(keyName + "_" + locale.substring(0, 2));
				if (s != null) {
					return s;
				}
			}
		}

		// No localized string found, return default
		return findValue(keyName);
	}

	/**
	 * Get the number of seconds to wait after the action is executed. This value is
	 * taken from the attribute "wait-after-action"
	 *
	 * @return Waittime in seconds.
	 * @see de.polarwolf.libsequence.config.LibSequenceConfigStep#KEYNAME_WAIT
	 *      KEYNAME_WAIT
	 */
	public int getWait() {
		String waitTime = findValue(KEYNAME_WAIT);
		if (waitTime == null) {
			return 0; // it could be that the step contains no wait, then set the wait to zero
		}
		// No validation here because we expect a checkSyntax() before
		return Integer.parseUnsignedInt(waitTime);
	}

	/**
	 * Get the name of the action which should be executed. This value is taken from
	 * the attribute "action"
	 *
	 * @return ActionName
	 * @see de.polarwolf.libsequence.config.LibSequenceConfigStep#KEYNAME_ACTION
	 *      KEYNAME_ACTION
	 */
	public String getActionName() {
		return findValue(KEYNAME_ACTION);
	}

	/**
	 * Get a list of all attributes defined in this step
	 *
	 * @return List of attribute names
	 */
	public Set<String> getAttributeKeys() {
		return new HashSet<>(attributes.keySet());
	}

	/**
	 * Perform a Syntax Check on this step. The syntax check is called during
	 * initial section load and before every run. An invalid sequence is not
	 * runnable. A Sequence is immutable (it cannot be changed after load, except
	 * explicit section unload/reload)
	 *
	 * @throws LibSequenceConfigException An exception is thrown if the validation
	 *                                    fails, e.g. Syntax Error.
	 */
	public void validateSyntax() throws LibSequenceConfigException {

		// verify if actionName does exists
		String actionName = getActionName();
		if ((actionName == null) || actionName.isEmpty()) {
			throw new LibSequenceConfigException(null, getSequenceName(), getStepNr(), LSCERR_MISSING_ACTION, null);
		}

		// test if wait is numeric
		String wait = findValue(KEYNAME_WAIT);
		if (wait != null) {
			try {
				Integer.parseUnsignedInt(wait);
			} catch (Exception e) {
				throw new LibSequenceConfigException(null, getSequenceName(), getStepNr(), LSCERR_WAIT_NOT_NUMERIC,
						wait);
			}
		}

		// check if the action itself does not have an error
		try {
			actionValidator.validateSyntax(this);
		} catch (LibSequenceActionException e) {
			throw new LibSequenceConfigException(null, getSequenceName(), getStepNr(), LSCERR_ACTION, null, e);
		}
	}

}
