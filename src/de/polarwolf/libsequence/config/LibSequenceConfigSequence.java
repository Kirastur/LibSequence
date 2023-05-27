package de.polarwolf.libsequence.config;

// The tree is: Manager ==> Section ==> Sequence ==> Step
//
// A sequence is an ordered list of steps
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_NOT_AUTHORIZED;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_STEP_ENUM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Defines a sequence. A sequence is an ordered collection of steps.
 *
 * @see de.polarwolf.libsequence.config.LibSequenceConfigStep
 *      LibSequenceConfigStep
 * @see de.polarwolf.libsequence.config.LibSequenceConfigSection
 *      LibSequenceConfigSection
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Configuration">Configuration</A>
 *      (WIKI)
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/Sequence-definition">Sequence
 *      Definition</A> (WIKI)
 *
 */
public class LibSequenceConfigSequence {

	// Please use the public getter for this
	// Perhaps someone want to override
	private final String sequenceName;

	// The tokens are sensitive elements and therefore only private
	private LibSequenceToken securityToken;
	private final LibSequenceToken ownerToken;

	/**
	 * The actionValidator identifies the LibSequence-Instance this sequence belongs
	 * to. It is called during syntax check.
	 */
	protected final LibSequenceActionValidator actionValidator;

	/**
	 * Ordered list of steps this sequence contains
	 */
	protected final ArrayList<LibSequenceConfigStep> steps = new ArrayList<>();

	/**
	 * Build this sequence (including all steps) by loading it from file (for
	 * example config.yml).
	 *
	 * @param ownerToken      The owner if this sequence.
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param sequenceName    Name of the sequence
	 * @param config          Configuration Section to load the step from
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigSequence(LibSequenceToken ownerToken, LibSequenceActionValidator actionValidator,
			String sequenceName, ConfigurationSection config) throws LibSequenceConfigException {
		this.ownerToken = ownerToken;
		this.actionValidator = actionValidator;
		this.sequenceName = sequenceName;

		recreateSecurityToken(ownerToken);
		loadStepsFromConfig(config);
	}

	/**
	 * Build the sequence by loading it from an in-memory list of step.
	 *
	 * @param ownerToken      The owner if this sequence.
	 * @param actionValidator The LibSequence-Instance this sequence belongs to.
	 *                        Pass-trough-parameter from
	 *                        {@link de.polarwolf.libsequence.reload.LibSequenceReloaderHelper#getActionValidator()
	 *                        ReloaderHelper.getActionValidator}
	 * @param sequenceName    Name of the sequence
	 * @param config          List of Maps containing the steps
	 * @throws LibSequenceConfigException An exception is thrown if the build was
	 *                                    not successful e.g. Syntax Error
	 */
	public LibSequenceConfigSequence(LibSequenceToken ownerToken, LibSequenceActionValidator actionValidator,
			String sequenceName, List<Map<String, String>> config) throws LibSequenceConfigException {
		this.ownerToken = ownerToken;
		this.actionValidator = actionValidator;
		this.sequenceName = sequenceName;

		recreateSecurityToken(ownerToken);
		loadStepsFromList(config);
	}

	/**
	 * Generate a debug substring from the step number. Used for debug- and
	 * error-messages.
	 */
	protected String stepNrToString(int stepNr) {
		return "Step " + Integer.toString(stepNr);
	}

	/**
	 * Helper function to load the sequence from file
	 */
	// The bukkit ConfigurationSection is not able to detect duplicate keys
	// so we cannot check for duplicate steps
	protected void loadStepsFromConfig(ConfigurationSection config) throws LibSequenceConfigException {
		for (int i = 1; i <= config.getKeys(false).size(); i++) {

			if (!config.contains(Integer.toString(i), true)) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM, stepNrToString(i));
			}

			ConfigurationSection subConfig = config.getConfigurationSection(Integer.toString(i));
			if (subConfig == null) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM, stepNrToString(i));
			}

			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), i, subConfig));
		}
	}

	/**
	 * Helper function to load the sequence from a List of Maps
	 */
	protected void loadStepsFromList(List<Map<String, String>> config) throws LibSequenceConfigException {
		for (Map<String, String> stepTouples : config) {
			if (stepTouples == null) {
				throw new LibSequenceConfigException(getSequenceName(), LSCERR_STEP_ENUM,
						stepNrToString(getSize() + 1));
			}
			steps.add(new LibSequenceConfigStep(actionValidator, getSequenceName(), getSize() + 1, stepTouples));
		}
	}

	/**
	 * Check if the given ownerToken is the owner of the Sequence. Some sequence
	 * functions are limit and can only be used if the caller identifies himself as
	 * the owner of the sequence. The owner was set during object creation. This
	 * method is final, so no one can override this to steal a foreign ownerToken.
	 *
	 * @param ownerTokenToCkeck The ownerToken we should check
	 * @return TRUE if the ownerTokenToCkeck is the sequence-owner, otherwise FALSE
	 */
	public final boolean isOwner(LibSequenceToken ownerTokenToCkeck) {
		return ownerToken.equals(ownerTokenToCkeck);
	}

	/**
	 * Get a security token which is needed to execute the sequence. You can get it
	 * only if you are the owner of the sequence.
	 *
	 * @param ownerTokenAsAuthentication Only the owner can request the
	 *                                   secutityToken
	 * @return Security Token
	 * @throws LibSequenceConfigException An exception is thrown if the caller can't
	 *                                    identify himself as the owner.
	 */
	public LibSequenceToken getSecurityToken(LibSequenceToken ownerTokenAsAuthentication)
			throws LibSequenceConfigException {
		if (!isOwner(ownerTokenAsAuthentication)) {
			throw new LibSequenceConfigException(getSequenceName(), LSCERR_NOT_AUTHORIZED, null);
		}
		return securityToken;
	}

	/**
	 * Recreates the security token. A security token is used to authorize a
	 * sequence to start. Only the owner of the sequence can query the security
	 * token (and hand it over to a 3rd-party-plugin which wants to execute the
	 * sequence). Normally the security token is generated during sequence
	 * construction, but you can manually force a recreation.
	 *
	 * @param ownerTokenAsAuthentication Only the owner can request to regenerate
	 *                                   the secutityToken
	 * @throws LibSequenceConfigException An exception is thrown if the caller can't
	 *                                    identify himself as the owner.
	 */
	public final void recreateSecurityToken(LibSequenceToken ownerTokenAsAuthentication)
			throws LibSequenceConfigException {
		if (!isOwner(ownerTokenAsAuthentication)) {
			throw new LibSequenceConfigException(getSequenceName(), LSCERR_NOT_AUTHORIZED, null);
		}
		securityToken = new LibSequenceToken();
	}

	/**
	 * Verify the given security token. Even if the security token is not known by
	 * others, every plugin can verify if it has the correct one for this sequence.
	 * This method is final, so no one can override this to steal a foreign token
	 * This is needed because we have a loop which cycles through all sequences to
	 * find the sequence fitting to a given token.
	 *
	 * @param securityTokenToCheck The Security Token we should check
	 * @return TRUE if the security token is correct and fits to the sequence,
	 *         otherwise FALSE.
	 */
	public final boolean isValidSecurityToken(LibSequenceToken securityTokenToCheck) {
		return securityToken.equals(securityTokenToCheck);
	}

	/**
	 * Get the name of this sequence
	 */
	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * Get the number of steps this sequence contains
	 */
	public int getSize() {
		return steps.size();
	}

	/**
	 * Get the Step associated with the given number. The boundary check is done by
	 * Java itself (A java exception is thrown if the step does not exists)
	 *
	 * @param stepNr Position of the step we request
	 * @return The requested step
	 */
	public LibSequenceConfigStep getStep(int stepNr) {
		return steps.get(stepNr - 1);
	}

	/**
	 * Validates the syntax of the complete sequence including all steps
	 *
	 * @throws LibSequenceConfigException A exception is thrown if the verify fails,
	 *                                    e.g. Syntax Error
	 */
	public void validateSyntax() throws LibSequenceConfigException {
		for (LibSequenceConfigStep myStep : steps) {
			myStep.validateSyntax();
		}
	}

}
