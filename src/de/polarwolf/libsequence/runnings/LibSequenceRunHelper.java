package de.polarwolf.libsequence.runnings;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.checks.LibSequenceCheckException;
import de.polarwolf.libsequence.checks.LibSequenceCheckManager;
import de.polarwolf.libsequence.conditions.LibSequenceConditionManager;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.includes.LibSequenceIncludeException;
import de.polarwolf.libsequence.includes.LibSequenceIncludeManager;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderException;
import de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager;

/**
 * Helper for the running sequence to call library functions
 *
 */
public class LibSequenceRunHelper {

	protected final LibSequenceRunningSequence runningSequence;
	protected final LibSequencePlaceholderManager placeholderManager;
	protected final LibSequenceConditionManager conditionManager;
	protected final LibSequenceCheckManager checkManager;
	protected final LibSequenceIncludeManager includeManager;
	protected final LibSequenceRunManager runManager;

	public LibSequenceRunHelper(LibSequenceRunningSequence runningSequence,
			LibSequencePlaceholderManager placeholderManager, LibSequenceConditionManager conditionManager,
			LibSequenceCheckManager checkManager, LibSequenceIncludeManager includeManager,
			LibSequenceRunManager runManager) {
		this.runningSequence = runningSequence;
		this.placeholderManager = placeholderManager;
		this.conditionManager = conditionManager;
		this.checkManager = checkManager;
		this.includeManager = includeManager;
		this.runManager = runManager;
	}

	// Gateway to PlaceholderManager
	// PlaceholderManager takes care if messageTest is null
	/**
	 * Resolve Placeholders in a given string. Per default the internal placeholder
	 * and PlaceholderAPI (PAIP) are called.
	 *
	 * @see de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholder</A>
	 *      (WIKI)
	 */
	public String resolvePlaceholder(String messageText, LibSequenceRunOptions runOptions)
			throws LibSequencePlaceholderException {
		return placeholderManager.resolvePlaceholder(messageText, runOptions);
	}

	/**
	 * Resolve placeholders, but for a different player than the initiator
	 *
	 * @see de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholder</A>
	 *      (WIKI)
	 */
	public String resolvePlaceholderForOtherPlayer(String messageText, LibSequenceRunOptions runOptions, Player player)
			throws LibSequencePlaceholderException {
		LibSequenceRunOptions playerRunOptions = runOptions.getCopy();
		playerRunOptions.setInitiator(player);
		return placeholderManager.resolvePlaceholder(messageText, playerRunOptions);
	}

	/**
	 * Test if a sting seems to contain placeholders, so we can warn that the
	 * resolver could not handle all placeholders
	 *
	 * @see de.polarwolf.libsequence.placeholders.LibSequencePlaceholderManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholder</A>
	 *      (WIKI)
	 * @see de.polarwolf.libsequence.logger.LibSequenceLogger#onPlaceholderWarn
	 */
	public boolean containsPlaceholder(String messageText) {
		return placeholderManager.containsPlaceholder(messageText);
	}

	// Gateway to ConditionManager
	/**
	 * Resolve conditions (boolean expressions)
	 *
	 * @see de.polarwolf.libsequence.conditions.LibSequenceConditionManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Conditions">Conditions</A>
	 *      (WIKI)
	 */
	public boolean resolveCondition(String conditionText, LibSequenceRunningSequence runningSequence) {
		return conditionManager.performConditions(conditionText, runningSequence);
	}

	// Gateway to CheckManager
	/**
	 * Perform checks (check_*)
	 *
	 * @see de.polarwolf.libsequence.checks.LibSequenceCheckManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Checks">Checks</A> (WIKI)
	 */
	public boolean performChecks(LibSequenceConfigStep configStep) throws LibSequenceCheckException {
		return checkManager.performChecks(runningSequence, configStep);
	}

	// Gateway to IncludeManager
	/**
	 * Perform includes (include_*)
	 *
	 * @see de.polarwolf.libsequence.includes.LibSequenceIncludeManager
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Includes">Includes</A>
	 *      (WIKI)
	 */
	public Set<CommandSender> performIncludes(LibSequenceConfigStep configStep) throws LibSequenceIncludeException {
		return includeManager.performIncludes(runningSequence, configStep);
	}

	// Gateway to runManager
	/**
	 * Notify the RunManager that the sequence has started
	 */
	protected void onInit() {
		runManager.onInit(runningSequence);
	}

	/**
	 * Notify the RunManager that the sequence was cancelled
	 */
	protected void onCancel() {
		runManager.onCancel(runningSequence);
	}

	/**
	 * Notify the RunManager that the sequence has finished with success
	 */
	protected void onFinish() {
		runManager.onFinish(runningSequence);
	}

	/**
	 * Instruct the RunManager to execute a specieif step of the sequence
	 */
	protected void executeStep(LibSequenceConfigStep configStep) throws LibSequenceRunException {
		runManager.executeStep(runningSequence, configStep);
	}

}
