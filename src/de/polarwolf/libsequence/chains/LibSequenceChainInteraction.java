package de.polarwolf.libsequence.chains;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

/**
 *  This stores the event when a player presses the button.
 *  On a commandblock initiated sequence the chainresolver
 *  uses this list to detect the nearest player (which is the one
 *  who is then assigned as the initiator).
 *
 */
public record LibSequenceChainInteraction (CommandSender sender, Block target, long timestamp) {

}
