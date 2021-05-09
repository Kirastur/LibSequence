package de.polarwolf.libsequence.callback;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCallbackGeneric implements LibSequenceCallback{
	
	private static final String SECTION_NAME_SEQUENCES = "sequences";
	
	protected final Plugin plugin;
	protected final boolean enableConsoleNotifications;

	
	//
	// Object creation
	//
	
	// 1. Option: Create the object with all default settings
	public LibSequenceCallbackGeneric(Plugin plugin) {
		this.plugin=plugin;
		this.enableConsoleNotifications = false;
	}
	
	// 2. Option: Create the object and select if you want to printout debug messages to server console
	public LibSequenceCallbackGeneric(Plugin plugin, boolean enableConsoleNotifications) {
		this.plugin=plugin;
		this.enableConsoleNotifications = enableConsoleNotifications;
	}
	
	
	//
	// Load sequences
	//
	
	// Overwrite this if you want to change the name of the section in the config-file where the sections are defined
	protected String getSectionIdentifier() {
		return SECTION_NAME_SEQUENCES;
	}
	
	// Overwrite this if you want to import the section config from another file than the standard plugin config-file
	public ConfigurationSection getConfigurationSection() {
		plugin.reloadConfig();
		ConfigurationSection sectionRoot = plugin.getConfig().getRoot();
		if (!sectionRoot.contains (getSectionIdentifier(), true)) {
			return null;
		}
		return sectionRoot.getConfigurationSection(getSectionIdentifier());
	}
	
	// Overwrite this if you want to extend the config handling itself
	@Override
	public LibSequenceConfigSection createConfigSection (LibSequenceActionValidator actionValidator) throws LibSequenceConfigException {
		ConfigurationSection config = getConfigurationSection();
		if (config==null) {
			return null;
		}
		return new LibSequenceConfigSection(this, actionValidator, config);
	}
	

	//
	// Error handling
	//
	
	// Print an info in an check fails
	public void onCheckFailed(LibSequenceRunningSequence sequence, String checkName, String failMessage) {
		if (enableConsoleNotifications) {
			String sequenceName = sequence.getName();
			int stepNr = sequence.getStepNr();
			String messageText = "Check failed: " + sequenceName + ": Step " + Integer.toString(stepNr) + ": " + checkName + ": " + failMessage;
			plugin.getLogger().info(messageText);
		}
	}
	
	// Print an waring and optional StackTrace on Error
	public void onExecutionError(LibSequenceRunningSequence secuence, LibSequenceRunException e) {
		plugin.getLogger().warning(e.getMessageCascade());
		if (e.hasJavaException()) {
			e.printStackTrace();
		}
	}


	//
	// Task creation
	//
	
	// No need to overwrite this
	// This is just because the Lib itself is not aware of the plugin  
	@Override
	public final BukkitTask scheduleTask(BukkitRunnable task, int wait) {
		return task.runTaskLater(plugin, wait);
	}
	
	
	//
	// Debug messages
	//
	
	// Per default all is written to the server console
	public void printSequenceMessage(LibSequenceRunningSequence sequence, String message) {
		if (enableConsoleNotifications) {
			plugin.getLogger().info("Sequence "+sequence.getName()+" "+message);
		}
	}
	
	@Override
	public void debugSequenceStarted(LibSequenceRunningSequence sequence) {
		printSequenceMessage(sequence, "has started");
	}
	
	@Override
	public void debugSequenceStepReached(LibSequenceRunningSequence sequence, LibSequenceConfigStep step) {
		printSequenceMessage(sequence, "has reached Step "+sequence.getStepNr()+" and will now execute "+step.findActionName());
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
