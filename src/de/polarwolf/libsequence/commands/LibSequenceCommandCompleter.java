package de.polarwolf.libsequence.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class LibSequenceCommandCompleter implements TabCompleter {
	
	private final LibSequenceCommand command;
	
	public LibSequenceCommandCompleter(LibSequenceCommand command) {
		this.command=command;
	}
	
	protected List<String> listCommands(CommandSender sender) {
	  return command.filterCommands(sender, command.listAllCommands());
	}
	
	protected List<String> listConfigSequences(CommandSender sender) {
	  return command.filterSequences(sender, command.listConfigSequences());
	}
	
	protected List<String> listRunningSequences(CommandSender sender) {
	  return command.filterSequences(sender, command.listRunningSequences());
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==1) {
			return listCommands(sender);
		}
		if (args.length==2) {
			String subCommand=args[0];
			if (subCommand.equalsIgnoreCase("start")) {
				return listConfigSequences(sender);
			}
			if (subCommand.equalsIgnoreCase("cancel")) {
				return new ArrayList<>(new HashSet<>(listRunningSequences(sender)));
			}
		}
		return new ArrayList<>();
	}

}
