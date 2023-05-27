package de.polarwolf.libsequence.commands;

import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_ALL_OPTION_FORBIDDEN;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_ALL_SEQUENCE_FORBIDDEN;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_EMPTY;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_GENERAL_ERROR;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_HELP;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_NOT_RUNNING;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_NO_API;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_NO_OPTION_PERMISSION;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_NO_SEQUENCE_PERMISSION;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_OPTION_NAME_MISSING;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_RELOAD;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_SEQUENCE_CANCELLED;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_SEQUENCE_NAME_MISSING;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_SEQUENCE_STARTED;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_TOO_MANY_PARAMETERS;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_UNKNOWN_OPTION;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.MSG_UNKNOWN_SEQUENCE;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.api.LibSequenceAPI;
import de.polarwolf.libsequence.api.LibSequenceController;
import de.polarwolf.libsequence.api.LibSequenceProvider;
import de.polarwolf.libsequence.main.Main;

/**
 * Minecraft "/sequence" command handling
 *
 */
public class LibSequenceCommand implements CommandExecutor {

	public static final String LOCALE_DEFAULT = "default";
	public static final String STR_MESSAGES = "messages";

	public static final String CMD_START = "start";
	public static final String CMD_CANCEL = "cancel";
	public static final String CMD_LIST = "list";
	public static final String CMD_INFO = "info";
	public static final String CMD_RELOAD = "reload";
	public static final String CMD_HELP = "help";

	protected final Main main;
	protected final String commandName;
	protected LibSequenceTabCompleter tabCompleter;

	public LibSequenceCommand(Main main, String commandName) {
		this.main = main;
		this.commandName = commandName;
		main.getCommand(commandName).setExecutor(this);
		tabCompleter = new LibSequenceTabCompleter(main, this);
	}

	public String getCommandName() {
		return commandName;
	}

	protected LibSequenceController getController() {
		LibSequenceAPI lsAPI = LibSequenceProvider.getAPI();
		if (lsAPI == null) {
			return null;
		}
		return lsAPI.getController();
	}

	protected String getMessage(LibSequenceCommandMessages message, CommandSender sender) {
		String locale = null;
		String messageText = null;
		if (sender instanceof Player player) {
			locale = player.getLocale();
		}

		if (locale == null) {
			locale = "";
		}

		if (locale.length() >= 5) {
			messageText = main.getConfig().getString(STR_MESSAGES + "." + locale + "." + message.toString(), null);
		}

		if ((messageText == null) && (locale.length() >= 2)) {
			locale = locale.substring(0, 2);
			messageText = main.getConfig().getString(STR_MESSAGES + "." + locale + "." + message.toString(), null);
		}

		if (messageText == null) {
			messageText = main.getConfig().getString(STR_MESSAGES + "." + LOCALE_DEFAULT + "." + message.toString(),
					message.defaultMessage());
		}

		return messageText;
	}

	protected void printMessage(CommandSender sender, LibSequenceCommandMessages message, String additionalInfo) {
		String messageText = getMessage(message, sender);
		if ((additionalInfo != null) && (!additionalInfo.isEmpty())) {
			messageText = messageText + " " + additionalInfo;
		}
		sender.sendMessage(messageText);
	}

	protected boolean checkNrOfArguments1(CommandSender sender, int argLength) {
		if (argLength > 1) {
			printMessage(sender, MSG_TOO_MANY_PARAMETERS, null);
			return false;
		}
		return true;
	}

	protected boolean checkNrOfArguments2(CommandSender sender, int argLength) {
		if (argLength < 2) {
			printMessage(sender, MSG_SEQUENCE_NAME_MISSING, null);
			return false;
		}
		if (argLength > 2) {
			printMessage(sender, MSG_TOO_MANY_PARAMETERS, null);
			return false;
		}
		return true;
	}

	protected List<String> listAllCommandActions() {
		ArrayList<String> cmds = new ArrayList<>();
		cmds.add(CMD_START);
		cmds.add(CMD_CANCEL);
		cmds.add(CMD_LIST);
		cmds.add(CMD_INFO);
		cmds.add(CMD_RELOAD);
		return cmds;
	}

	protected List<String> listConfigSequences() {
		return getController().getNames();
	}

	protected List<String> listRunningSequences() {
		return getController().getRunningSequenceNames();
	}

	protected boolean hasCommandPermission(CommandSender sender, String cmd) {
		return sender.hasPermission("libsequence.command." + cmd);
	}

	protected boolean hasSequencePermission(CommandSender sender, String sequenceName) {
		return getController().hasPermission(sender, sequenceName);
	}

	protected List<String> filterCommandActions(CommandSender sender, List<String> rawCommandActions) {
		ArrayList<String> filteredCommandActions = new ArrayList<>();
		for (String myCommandActionName : rawCommandActions) {
			if (hasCommandPermission(sender, myCommandActionName)) {
				filteredCommandActions.add(myCommandActionName);
			}
		}
		return filteredCommandActions;
	}

	protected List<String> filterSequences(CommandSender sender, List<String> rawSequences) {
		ArrayList<String> filteredSequences = new ArrayList<>();
		for (String sequenceName : rawSequences) {
			if (hasSequencePermission(sender, sequenceName)) {
				filteredSequences.add(sequenceName);
			}
		}
		return filteredSequences;
	}

	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", filterCommandActions(sender, listAllCommandActions()));
		if (s.isEmpty()) {
			printMessage(sender, MSG_ALL_OPTION_FORBIDDEN, null);
		} else {
			printMessage(sender, MSG_HELP, s);
		}
	}

	protected void cmdStart(CommandSender sender, String[] args) {
		if (!checkNrOfArguments2(sender, args.length)) {
			return;
		}
		String sequenceName = args[1];
		if (!getController().hasSequence(sequenceName)) {
			printMessage(sender, MSG_UNKNOWN_SEQUENCE, null);
		}
		if (!hasSequencePermission(sender, sequenceName)) {
			printMessage(sender, MSG_NO_SEQUENCE_PERMISSION, null);
			return;
		}
		String result = getController().execute(sequenceName, sender);
		if (result.equals(LibSequenceController.OK)) {
			printMessage(sender, MSG_SEQUENCE_STARTED, null);
		} else {
			printMessage(sender, MSG_GENERAL_ERROR, result);
		}
	}

	protected void cmdCancel(CommandSender sender, String[] args) {
		if (!checkNrOfArguments2(sender, args.length)) {
			return;
		}
		String sequenceName = args[1];
		if (!hasSequencePermission(sender, sequenceName)) {
			printMessage(sender, MSG_NO_SEQUENCE_PERMISSION, null);
			return;
		}
		int i = getController().cancel(sequenceName);
		if (i > 0) {
			printMessage(sender, MSG_SEQUENCE_CANCELLED, null);
		} else {
			printMessage(sender, MSG_NOT_RUNNING, null);
		}
	}

	protected void cmdList(CommandSender sender, String[] args) {
		if (!checkNrOfArguments1(sender, args.length)) {
			return;
		}
		String s = String.join(" ", filterSequences(sender, listConfigSequences()));
		if (s.isEmpty()) {
			printMessage(sender, MSG_ALL_SEQUENCE_FORBIDDEN, null);
			return;
		}
		sender.sendMessage(s);
	}

	protected void cmdInfo(CommandSender sender, String[] args) {
		if (!checkNrOfArguments1(sender, args.length)) {
			return;
		}
		String s = String.join(" ", filterSequences(sender, listRunningSequences()));
		if (s.isEmpty()) {
			printMessage(sender, MSG_EMPTY, null);
			return;
		}
		sender.sendMessage(s);
	}

	protected void cmdReload(CommandSender sender, String[] args) {
		if (!checkNrOfArguments1(sender, args.length)) {
			return;
		}
		String result = getController().reload();
		if (result.equals(LibSequenceController.OK)) {
			printMessage(sender, MSG_RELOAD, null);
		} else {
			printMessage(sender, MSG_GENERAL_ERROR, result);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (getController() == null) {
			printMessage(sender, MSG_NO_API, null);
			return true;
		}
		if (args.length == 0) {
			printMessage(sender, MSG_OPTION_NAME_MISSING, null);
			return true;
		}
		String commandAction = args[0];
		if (commandAction.equalsIgnoreCase(CMD_HELP)) {
			cmdHelp(sender);
			return true;
		}
		if (!listAllCommandActions().contains(commandAction)) {
			printMessage(sender, MSG_UNKNOWN_OPTION, null);
			return true;
		}
		if (!hasCommandPermission(sender, commandAction)) {
			printMessage(sender, MSG_NO_OPTION_PERMISSION, null);
			return true;
		}
		if (commandAction.equalsIgnoreCase(CMD_START)) {
			cmdStart(sender, args);
			return true;
		}
		if (commandAction.equalsIgnoreCase(CMD_CANCEL)) {
			cmdCancel(sender, args);
			return true;
		}
		if (commandAction.equalsIgnoreCase(CMD_LIST)) {
			cmdList(sender, args);
			return true;
		}
		if (commandAction.equalsIgnoreCase(CMD_INFO)) {
			cmdInfo(sender, args);
			return true;
		}
		if (commandAction.equalsIgnoreCase(CMD_RELOAD)) {
			cmdReload(sender, args);
			return true;
		}
		return false;
	}

}
