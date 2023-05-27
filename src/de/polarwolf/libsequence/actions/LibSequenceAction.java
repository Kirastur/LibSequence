package de.polarwolf.libsequence.actions;

import java.util.Set;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * Defines an action. Most real implementation don't implement the interface
 * directly, instead they use
 * {@link de.polarwolf.libsequence.actions.LibSequenceActionGeneric
 * LibSequenceActionGeneric}.
 *
 * <UL>
 * <LI>The action-object can access the runOptions by using
 * "sequence.getRunOptions()"</LI>
 * <LI>The action-object can get the Plugin by using "sequence.getPlugin()"
 * (needed for some Bukkit-functions).</LI>
 * <LI>The action-object can test a condition by using
 * "sequence.resolveCondition(String conditionText)"</LI>
 * <LI>The action-object can call the Placeholder resolver by using
 * "sequence.resolvePlaceholder(String messageText)"</LI>
 * <LI>The action-object can get a player-list by analyzing the includes_* using
 * "sequence.performIncludes(LibSequenceConfigStep configStep)"</LI>
 * <LI>The action-object can perform a check like the check-action by using
 * "LibSequenceCheckResult performChecks(LibSequenceConfigStep configStep)"</LI>
 * <LI>The action-object can test for an authorization token by using
 * "runOptions.verifyAuthorizationKey(authorizationToken)"</LI>
 * <LI>There is no need for the action-object to call the chain-resolver,
 * because chains are resolved on sequence start</LI>
 * <LI>It is intended that the action-object does not have access to the
 * complete sequence</LI>
 * </UL>
 *
 * @see LibSequenceActionManager ActionManager
 * @see <A href="https://github.com/Kirastur/LibSequence/wiki/Custom-Actions">Custom Actions</A> (WIKI)
 *
 */
public interface LibSequenceAction {

	/**
	 * This procedure is called every time a sequence is started, regardless if this
	 * sequence contains a step with this action or not.
	 */
	public void onInit(LibSequenceRunningSequence sequence);

	/**
	 * This procedure is called every time a sequence is cancelled, regardless if
	 * this sequence contains a step with this action or not.<BR>
	 * It is guaranteed that you receive either an onCancel or onFinish at
	 * the end of every sequence.
	 */
	public void onCancel(LibSequenceRunningSequence sequence);

	/**
	 * This procedure is called every time a sequence is regular finished,
	 * regardless if this sequence contains a step with this action or not.<BR>
	 * It is guaranteed that you receive either an onCancel or onFinish at
	 * the end of every sequence.
	 */
	public void onFinish(LibSequenceRunningSequence sequence);

	/**
	 * Define if the {@link de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager
	 * SyntaxManager} should perform some basic validations, e.g. for required and
	 * optional attributes.
	 *
	 * @return TRUE if you want to disable the syntax check, FALSE for normal syntax
	 *         validation
	 */
	public boolean skipAttributeVerification();

	/**
	 * Defines if your action is using
	 * <A href="https://github.com/Kirastur/LibSequence/wiki/Includes">
	 * includes</A>, so the {@link de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager
	 * SyntaxManager} will accept the include-attributes here.
	 *
	 * @return TRUE if your action accepts includes, otherwise FALSE
	 */
	public boolean hasInclude();

	/**
	 * Defines if your action is using
	 * <A href="https://github.com/Kirastur/LibSequence/wiki/Checks">checks</A>, so
	 * the {@link de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager SyntaxManager} will
	 * accept the check-attributes here.
	 *
	 * @return TRUE if your action accepts checks, otherwise FALSE
	 */
	public boolean hasCheck();

	/**
	 * List of required attributes. If one of these attributes is missing in the
	 * Sequence Step, the {@link de.polarwolf.libsequence.syntax.LibSequenceSyntaxManager
	 * SyntaxManager} will handle this as a syntax error.
	 *
	 * @return List of required Attributes
	 */
	public Set<String> getRequiredAttributes();

	/**
	 * List of optional attributes. If the Syntax Manager finds an attribute in the
	 * Sequence Step which is not required and and not optional (and not belongs to
	 * include or check), the SyntaxManager will handle this as a syntax error.
	 *
	 * @return List of optional Attributes
	 */
	public Set<String> getOptionalAttributes();

	/**
	 * Perform syntax validation. The sequences are validated during load. If the
	 * ActionManager wants the action to validate a specific step, the ActionManager
	 * calls this procedure. Now the ActionManager expects that the action-object do
	 * action specific validations. The standard validation process performed by the
	 * SyntaxManager is done before, so you do not need to care about it. You must
	 * throw an Exception if the step is invalid.
	 *
	 * @param configStep Step-Definition from the config
	 * @throws LibSequenceException Thrown if step is detected as invalid
	 */
	public void validateSyntax(LibSequenceConfigStep configStep) throws LibSequenceException;

	/**
	 * Perform authorization. Normally, an action can be used by all sequences. But
	 * sometimes you need to restrict the sequences who can use a specific action,
	 * e.g. limit the use of an action to sequences delivered with the action-owner
	 * plugin. Before a sequence is executed, the RunManager checks all steps if the
	 * action is allowed to execute. Normally this validation would simply return,
	 * but it could also call e.g. "runOptions.verifyAuthorizationKey" to check if
	 * the owner has a key for the action. You must throw an exception if you do not
	 * permit the execution of this action here.
	 *
	 * Important: The difference between validateSyntax and validateAuthorization is
	 * that validateSyntax is called on sequence load, and validateAuthorization on
	 * sequence start.
	 *
	 * @param runOptions Runtime-Information about the current sequence
	 * @param configStep Step-Definition from the config
	 * @throws LibSequenceException Thrown if sequence is not allowed to execute
	 *                              this action
	 */
	public void validateAuthorization(LibSequenceRunOptions runOptions, LibSequenceConfigStep configStep)
			throws LibSequenceException;

	/**
	 * Execute action. This method is called when a sequence reaches a step where
	 * the action name matches the registered name for this action handler. Now the
	 * ActionManager expects that the action-object handles the action. You must
	 * throw an exception if there is any error during action execution. The
	 * sequence will report this via the logger-object, wait for the
	 * "wait-after-action" time and jumpover to the next step.
	 *
	 * @param sequence   Sequence this action belongs to
	 * @param configStep Step-Definition from the config
	 * @throws LibSequenceException Thrown if an error occurs during step execution
	 */
	public void execute(LibSequenceRunningSequence sequence, LibSequenceConfigStep configStep)
			throws LibSequenceException;

}
