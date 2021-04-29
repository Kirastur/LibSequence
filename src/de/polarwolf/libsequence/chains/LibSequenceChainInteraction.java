package de.polarwolf.libsequence.chains;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

public class LibSequenceChainInteraction {
	
	protected final CommandSender sender;
	protected final Block target;
	protected final long timestamp;
	

	public LibSequenceChainInteraction(CommandSender sender, Block target) {
		this.sender = sender;
		this.target = target;
		this.timestamp = System.currentTimeMillis();		
	}
	

	public CommandSender getSender() {
		return sender;
	}


	public Block getTarget() {
		return target;
	}


	public long getTimestamp() {
		return timestamp;
	}

}
