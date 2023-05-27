package de.polarwolf.libsequence.actions;

import static de.polarwolf.libsequence.actions.LibSequenceActionErrors.LSAERR_NOT_AUTHORIZED;

import java.util.HashSet;
import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Generic implementation of the LibSequenceAction interface, you can use this
 * as a base to implement your own action. See
 * {@link de.polarwolf.libsequence.actions.LibSequenceAction LibSequenceAction}
 * for a detailed description.
 *
 * @see LibSequenceActionManager ActionManager
 */
public abstract class LibSequenceActionGeneric implements LibSequenceAction {

	private final LibSequenceToken authorizationToken;

	protected LibSequenceActionGeneric() {
		this.authorizationToken = null;
	}

	protected LibSequenceActionGeneric(LibSequenceToken authorizationToken) {
		this.authorizationToken = authorizationToken;
	}

	@Override
	public void onInit(LibSequenceRunningSequence sequence) {
	}

	@Override
	public void onCancel(LibSequenceRunningSequence sequence) {
	}

	@Override
	public void onFinish(LibSequenceRunningSequence sequence) {
	}

	// Syntax Check is mostly done in SyntaxManager
	@Override
	public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException {
		// Valid per default
	}

	// Use runOption for authorization
	protected final void validateAuthorizationByToken(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep)
			throws LibSequenceActionException {
		if ((authorizationToken != null) && (!runOptions.verifyAuthorizationToken(authorizationToken))) {
			throw new LibSequenceActionException(configStep.getActionName(), LSAERR_NOT_AUTHORIZED, null);
		}
	}

	@Override
	public void validateAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep)
			throws LibSequenceException {
		validateAuthorizationByToken(runOptions, configStep);
	}

	// Settings for the Syntax Manager
	@Override
	public boolean skipAttributeVerification() {
		return false;
	}

	@Override
	public boolean hasInclude() {
		return false;
	}

	@Override
	public boolean hasCheck() {
		return false;
	}

	@Override
	public Set<String> getRequiredAttributes() {
		return new HashSet<>();
	}

	@Override
	public Set<String> getOptionalAttributes() {
		return new HashSet<>();
	}

}
