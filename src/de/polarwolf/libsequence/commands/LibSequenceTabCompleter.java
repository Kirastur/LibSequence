package de.polarwolf.libsequence.commands;

import static de.polarwolf.libsequence.commands.LibSequenceCommand.CMD_CANCEL;
import static de.polarwolf.libsequence.commands.LibSequenceCommand.CMD_START;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.polarwolf.libsequence.main.Main;

/**
 * Tab Completer for Minecraft "/sequence" command
 */
public class LibSequenceTabCompleter implements TabCompleter {

	protected final LibSequenceCommand command;

	public LibSequenceTabCompleter(Main main, LibSequenceCommand command) {
		this.command = command;
		main.getCommand(command.getCommandName()).setTabCompleter(this);
	}

	protected List<String> listCommandActions(CommandSender sender) {
		return command.filterCommandActions(sender, command.listAllCommandActions());
	}

	protected List<String> listConfigSequences(CommandSender sender) {
		return command.filterSequences(sender, command.listConfigSequences());
	}

	protected List<String> listRunningSequences(CommandSender sender) {
		return command.filterSequences(sender, command.listRunningSequences());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			return listCommandActions(sender);
		}
		if (args.length == 2) {
			String subCommand = args[0];
			if (subCommand.equalsIgnoreCase(CMD_START)) {
				return listConfigSequences(sender);
			}
			if (subCommand.equalsIgnoreCase(CMD_CANCEL)) {
				return new ArrayList<>(new HashSet<>(listRunningSequences(sender)));
			}
		}
		return new ArrayList<>();
	}

}
