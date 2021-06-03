package de.polarwolf.libsequence.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.api.LibSequenceController;
import de.polarwolf.libsequence.main.Main;
import static de.polarwolf.libsequence.commands.LibSequenceCommandMessages.*;

public class LibSequenceCommand implements CommandExecutor {
	
	public static final String LOCALE_DEFAULT="default"; 
	public static final String STR_MESSAGES="messages";

	private final Main main;
	private final LibSequenceController controller;
	
	public LibSequenceCommand(Main main, LibSequenceController controller) {
		this.main = main;
		this.controller=controller;
	}
	

	protected String getMessage(LibSequenceCommandMessages message, CommandSender sender) {
		String locale = null;
		String messageText = null;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			locale = player.getLocale();
		}

		if (locale == null) {
			locale = "";
		}
		
		if (locale.length() >= 5) {
			messageText = main.getConfig().getString(STR_MESSAGES+"."+locale+"."+message.toString(), null);
		}
		
		if ((messageText == null) && (locale.length() >= 2)) {
			locale = locale.substring(0, 2);
			messageText = main.getConfig().getString(STR_MESSAGES+"."+locale+"."+message.toString(), null);			
		}
		
		if (messageText == null) {
			messageText = main.getConfig().getString(STR_MESSAGES+"."+LOCALE_DEFAULT+"."+message.toString(), message.defaultMessage());			
		}
		
		return messageText;			
	}
	

	protected void printMessage(CommandSender sender, LibSequenceCommandMessages message, String additionalInfo) {
		String messageText = getMessage(message, sender);
		if ((additionalInfo!=null) && (!additionalInfo.isEmpty())) {
			messageText = messageText + " "+ additionalInfo;
		}
		sender.sendMessage(messageText);
	}
	

	protected boolean checkNrOfArguments1 (CommandSender sender, int argLength) {
		if (argLength > 1) {
			printMessage (sender, MSG_TOO_MANY_PARAMETERS, null);
			return false;
		}
		return true;
	}


	protected boolean checkNrOfArguments2 (CommandSender sender, int argLength) {
		if (argLength < 2) {
			printMessage (sender, MSG_SEQUENCE_NAME_MISSING, null);
			return false;
		}
		if (argLength > 2) {
			printMessage (sender, MSG_TOO_MANY_PARAMETERS, null);
			return false;
		}
		return true;
	}


	protected List<String> listAllCommands() {
		ArrayList<String> cmds = new ArrayList<>();
		cmds.add("start");
		cmds.add("cancel");
		cmds.add("list");
		cmds.add("info");
		cmds.add("reload");
		return cmds;
	}
	

	protected List<String> listConfigSequences() {
		return controller.getNames();
	}
	

	protected List<String> listRunningSequences() {
		return controller.getRunningSequenceNames();
	}


	protected boolean hasCommandPermission(CommandSender sender, String cmd) {
		return sender.hasPermission("libsequence.command."+cmd);			
	}
	

	protected boolean hasSequencePermission(CommandSender sender, String sequenceName) {
		return controller.hasPermission(sender, sequenceName);			
	}


	protected List<String> filterCommands(CommandSender sender, List<String> rawCommands) {
		ArrayList<String> filteredCommands = new ArrayList<>();		
		for (String commandName: rawCommands) {
			if (hasCommandPermission(sender, commandName)) {
				filteredCommands.add(commandName);
			}
		}
		return filteredCommands;
	}
	

	protected List<String> filterSequences(CommandSender sender, List<String> rawSequences) {
		ArrayList<String> filteredSequences = new ArrayList<>();		
		for (String sequenceName: rawSequences) {
			if (hasSequencePermission(sender, sequenceName)) {
				filteredSequences.add(sequenceName);
			}
		}
		return filteredSequences;
	}
	

	protected void cmdHelp(CommandSender sender) {
		String s = String.join(" ", filterCommands(sender, listAllCommands()));  
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
		String sequenceName=args[1];
		if (!controller.hasSequence(sequenceName)) {
			printMessage(sender, MSG_UNKNOWN_SEQUENCE, null);			
		}
		if (!hasSequencePermission(sender, sequenceName)) {
			printMessage(sender, MSG_NO_SEQUENCE_PERMISSION, null);
			return;
		}
		String result = controller.execute(sequenceName, sender);
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
		String sequenceName=args[1];
		if (!hasSequencePermission(sender, sequenceName)) {
			printMessage(sender, MSG_NO_SEQUENCE_PERMISSION, null);
			return;
		}
		int i =	controller.cancel(sequenceName);
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
		String result = controller.reload();
		if (result.equals(LibSequenceController.OK)) {
			printMessage(sender, MSG_RELOAD, null);
		} else {
			printMessage(sender, MSG_GENERAL_ERROR, result);
		}
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==0) {
			printMessage (sender, MSG_OPTION_NAME_MISSING, null);
			return true;
		}
		String subCommand=args[0];
		if (subCommand.equalsIgnoreCase("help")) {
			cmdHelp(sender);
			return true;
		}
		if (!listAllCommands().contains(subCommand)) {
			printMessage (sender, MSG_UNKNOWN_OPTION, null);
			return true;
		}
		if (!hasCommandPermission(sender, subCommand)) {
			printMessage(sender, MSG_NO_OPTION_PERMISSION, null);
			return true;
		}
		if (subCommand.equalsIgnoreCase("start")) {
			cmdStart(sender, args);
			return true;
		}
		if (subCommand.equalsIgnoreCase("cancel")) {
			cmdCancel(sender, args);
			return true;
		}
		if (subCommand.equalsIgnoreCase("list")) {
			cmdList(sender, args);
			return true;
		}
		if (subCommand.equalsIgnoreCase("info")) {
			cmdInfo(sender, args);
			return true;
		}
		if (subCommand.equalsIgnoreCase("reload")) {
			cmdReload(sender, args);
			return true;
		}
		return false;
	}
	
}
