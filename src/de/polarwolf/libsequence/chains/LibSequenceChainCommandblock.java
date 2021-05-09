package de.polarwolf.libsequence.chains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequenceChainCommandblock implements Listener, LibSequenceChain {
	
	// Given in Milliseconds
	public static final long LIMIT_TIMEFRAME = 1000;
	
	// given in blocks
	public static final double LIMIT_DISTANCE = 2.5;
	
	// Materials we should listen to
	public static final String MATERIAL_BUTTON = "_BUTTON";
	public static final String MATERIAL_PRESSURE_PLATE = "_PRESSURE_PLATE";
	
	protected List<LibSequenceChainInteraction> chainInteractions = new ArrayList<>();
	protected List<String> validMaterials = new ArrayList<>();
	

	public LibSequenceChainCommandblock (Plugin plugin) {
		initializeMaterials();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	
	// Handle Materials
	protected void initializeMaterials() {
		validMaterials.clear();
		validMaterials.add(MATERIAL_BUTTON);
		validMaterials.add(MATERIAL_PRESSURE_PLATE);
	}

	
	protected boolean isValidMaterial(String materialName) {
		for (String s : validMaterials) {
			if (materialName.contains(s)) {
				return true;
			}
		}
		return false;
	}

	
	// Low-level functions to handle the interaction-list
	protected void cleanupChainInteractions() {
		long cutover = System.currentTimeMillis() - LIMIT_TIMEFRAME;
		Iterator<LibSequenceChainInteraction> i = chainInteractions.iterator();
		while (i.hasNext()) {
			LibSequenceChainInteraction chainInteraction = i.next();
			if (chainInteraction.getTimestamp() < cutover) {
				i.remove();
			}
		}
	}

	
	protected void addChainInteraction(CommandSender sender, Block target) {
		cleanupChainInteractions();
		chainInteractions.add(new LibSequenceChainInteraction(sender, target));
	}
	

	protected Player findBestPlayer(Block target) {
		List<LibSequenceChainInteraction> chainHits = new ArrayList<>();
		
		// Option 1: The player has directly interacted with the given block
		for (LibSequenceChainInteraction chainInteraction: chainInteractions) {
			if ((chainInteraction.getTarget() == target) && (chainInteraction.getSender() instanceof Player)) {
				chainHits.add(chainInteraction);
			}
		}
		
		// Option2: we don't have a direct interaction, so let's search for a nearest
		if (chainHits.isEmpty()) {
			for (LibSequenceChainInteraction chainInteraction: chainInteractions) {
				double currentDistance = target.getLocation().distance(chainInteraction.getTarget().getLocation()); 
				if ((currentDistance < LIMIT_DISTANCE) && (chainInteraction.getSender() instanceof Player)) {
					chainHits.add(chainInteraction);
				}
			}			
		}
		

		// If we have more than one hit, let's take the newest
		// We do not need to care about TIMEFRAME here, this is done in resolveChain
		long maxTimestamp = 0;
		LibSequenceChainInteraction bestInteraction = null;
		for (LibSequenceChainInteraction chainInteraction: chainHits) {
			if (chainInteraction.getTimestamp() > maxTimestamp) {
				bestInteraction = chainInteraction;
				maxTimestamp = 	chainInteraction.getTimestamp();		
			}
		}
		
		if (bestInteraction == null) {
			return null;
		}
		return (Player)bestInteraction.getSender();			
	}
	

	protected void updateRunOptions(LibSequenceRunOptions runOptions, Player player) {
		runOptions.setInitiator(player);
	}

		

	@Override
	public boolean resolveChain(LibSequenceRunOptions runOptions) {

		// cleanup list now, so we don't deed to care about Timeframe
		cleanupChainInteractions();
		
		// Check if initiator is given
		if (runOptions.getInitiator() == null) {
			return false;
		}
		CommandSender initiator = runOptions.getInitiator();
		
		// Check if initiator is CommandBlock
		if (!(initiator instanceof BlockCommandSender)) {
			return false;
		}
		BlockCommandSender blockInitiator = (BlockCommandSender)initiator;
		
		Block block = blockInitiator.getBlock();
		Player player = findBestPlayer (block);
		if (player == null) {
			return false;
		}

		updateRunOptions(runOptions, player);
		return true;
	}
			

	public void handleBlockInteraction(Block clickedBlock, Player player, Action action) {

		// right clicks and physicals (walk over pressure plate) only
		if ((action != Action.RIGHT_CLICK_BLOCK) && (action != Action.PHYSICAL)) {
			return;
		}
		
		// collect Buttons only
		Material material = clickedBlock.getType();
		if (!isValidMaterial(material.toString())) {
			return;
		}

		// add action to chains
		addChainInteraction(player, clickedBlock);
	}
	

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		// The follow check is needed, but useless, because not all plugins are doing their cancel correctly
		if ((e.useInteractedBlock() == Result.DENY) || (e.useItemInHand() == Result.DENY)) {
			return;
		}
		
		Action action = e.getAction();
		Block clickedBlock = e.getClickedBlock();
		Player player = e.getPlayer();
		handleBlockInteraction(clickedBlock, player, action);
	}
	
}
