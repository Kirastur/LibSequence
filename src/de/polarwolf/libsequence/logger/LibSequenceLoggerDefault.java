package de.polarwolf.libsequence.logger;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

/**
 * To help you with handling the logger, we have created a very generic
 * implementation of the logger interface. In most circumstances you can use it
 * without any modifications. But you can inherited it and add your own business
 * logic if you want.
 *
 */
public class LibSequenceLoggerDefault implements LibSequenceLogger {

	protected final Plugin plugin;
	protected boolean enableConsoleNotifications;
	protected boolean enableInitiatorNotifications;

	/**
	 * Create the object with all default settings
	 *
	 * @param plugin 3rd-party plugin which owns the logger. It is only used to get
	 *               the plugin-specifiv minecraft logger so messages to the console
	 *               are print out with the correct prefix. If the Plugin is NULL,
	 *               the generic server logger is used.
	 */
	public LibSequenceLoggerDefault(Plugin plugin) {
		this.plugin = plugin;
		this.enableConsoleNotifications = false;
		this.enableInitiatorNotifications = false;
	}

	/**
	 * Get the plugin which is set during object creation
	 */
	protected Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Flag if notifications should be printed to the console. The Default Logger
	 * has an integrated mechanism to printout all info-messages received through
	 * the debugSequence... functions to the server-console. With this function you
	 * can control this feature. It also effects Placeholder-warnings and check-fail
	 * reports.
	 *
	 * @return TRUE if notifications should be printed to the console, otherwise
	 *         FALSE. Default is false.
	 */
	public boolean isEnableConsoleNotifications() {
		return enableConsoleNotifications;
	}

	/**
	 * Define if notification should be printed to the console. The Default Logger
	 * has an integrated mechanism to printout all info-messages received through
	 * the debugSequence... functions to the server-console. With this function you
	 * can control this feature. It also effects Placeholder-warnings and check-fail
	 * reports.
	 *
	 * @param enableConsoleNotifications TRUE if notifications should be printed to
	 *                                   the console, otherwise FALSE.
	 */
	public void setEnableConsoleNotifications(boolean enableConsoleNotifications) {
		this.enableConsoleNotifications = enableConsoleNotifications;
	}

	/**
	 * Flag if notifications should be printed to the initiator (CommandSender).
	 * Normally a sequence is started by a chain or by another business logic. The
	 * logic starts the sequence and is done, the lateron errors and exceptions are
	 * reported to the server console. But for debugging it could be useful if the
	 * initiator (CommandSender) also gets a notification that something has failed
	 * on the sequence. So you can enable this feature here.
	 *
	 * @return TRUE if notifications should be printed to the initiator, otherwise
	 *         FALSE. Default is false.
	 */
	public boolean isEnableInitiatorNotifications() {
		return enableInitiatorNotifications;
	}

	/**
	 * Define if notifications should be printed to the initiator (CommandSender).
	 * Normally a sequence is started by a chain or by another business logic. The
	 * logic starts the sequence and is done, the lateron errors and exceptions are
	 * reported to the server console. But for debugging it could be useful if the
	 * initiator (CommandSender) also gets a notification that something has failed
	 * on the sequence. So you can enable this feature here.
	 *
	 * @param enableInitiatorNotifications TRUE if notifications should be printed
	 *                                     to the initiator, otherwise FALSE.
	 */
	public void setEnableInitiatorNotifications(boolean enableInitiatorNotifications) {
		this.enableInitiatorNotifications = enableInitiatorNotifications;
	}

	/**
	 * Get a logger. If a plugin is set, use the plugin's logger, else the default
	 * Bukkit logger
	 */
	protected Logger getLogger() {
		if (plugin != null) {
			return plugin.getLogger();
		}
		return Bukkit.getLogger();
	}

	@Override
	public void onCheckFailed(LibSequenceRunningSequence sequence, String checkName, String failMessage) {
		if (enableConsoleNotifications) {
			String sequenceName = sequence.getName();
			int stepNr = sequence.getStepNr();
			String messageText = "Check failed: " + sequenceName + ": Step " + Integer.toString(stepNr) + ": "
					+ checkName + ": " + failMessage;
			getLogger().info(messageText);
		}
	}

	@Override
	public void onPlaceholderWarn(LibSequenceRunningSequence sequence, String attributeName, String valueText) {
		if (enableConsoleNotifications) {
			String sequenceName = sequence.getName();
			int stepNr = sequence.getStepNr();
			String messageText = "Possible placeholder did not resolve: " + sequenceName + ": Step "
					+ Integer.toString(stepNr) + ": " + attributeName + ": " + valueText;
			getLogger().info(messageText);
		}
	}

	@Override
	public void onExecutionError(LibSequenceRunningSequence sequence, LibSequenceRunException e) {
		if (enableInitiatorNotifications) {
			CommandSender initiator = sequence.getRunOptions().getInitiator();
			if (initiator instanceof Player) {
				initiator.sendMessage(e.getMessage());
			}
		}
		getLogger().warning(e.getMessageCascade());
		if (e.hasJavaException()) {
			e.printStackTrace();
		}
	}

	/**
	 * Print a debug message to the console or initiator.
	 */
	protected void printSequenceMessage(LibSequenceRunningSequence sequence, String message) {
		if (enableConsoleNotifications) {
			String s = "Sequence " + sequence.getName() + " " + message;
			getLogger().info(s);
		}
	}

	@Override
	public void debugSequenceStarted(LibSequenceRunningSequence sequence) {
		printSequenceMessage(sequence, "has started");
	}

	@Override
	public void debugSequenceStepReached(LibSequenceRunningSequence sequence, LibSequenceConfigStep step) {
		printSequenceMessage(sequence,
				"has reached Step " + sequence.getStepNr() + " and will now execute " + step.getActionName());
	}

	@Override
	public void debugSequenceCancelled(LibSequenceRunningSequence sequence) {
		printSequenceMessage(sequence, "was cancelled");
	}

	@Override
	public void debugSequenceFinished(LibSequenceRunningSequence sequence) {
		printSequenceMessage(sequence, "has finished");
	}

}
