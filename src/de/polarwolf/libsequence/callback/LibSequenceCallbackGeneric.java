package de.polarwolf.libsequence.callback;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCallbackGeneric implements LibSequenceCallbackExtended{
	
	private static final String SECTION_NAME_SEQUENCES = "sequences";
	
	protected final Plugin plugin;
	protected boolean enableConsoleNotifications;
	protected boolean enableInitiatorNotifications;

	
	//
	// Object creation
	//
	
	// 1. Option: Create the object with all default settings
	public LibSequenceCallbackGeneric(Plugin plugin) {
		this.plugin=plugin;
		this.enableConsoleNotifications = false;
		this.enableInitiatorNotifications = false;
	}
	
	
	//
	// Callback Options
	//
	
	public boolean isEnableConsoleNotifications() {
		return enableConsoleNotifications;
	}

	public void setEnableConsoleNotifications(boolean enableConsoleNotifications) {
		this.enableConsoleNotifications = enableConsoleNotifications;
	}

	public boolean isEnableInitiatorNotifications() {
		return enableInitiatorNotifications;
	}

	public void setEnableInitiatorNotifications(boolean enableInitiatorNotifications) {
		this.enableInitiatorNotifications = enableInitiatorNotifications;
	}


	// If you want to register some of your sequences in a directory
	// the directory needs a displayable identifier for the sequence-owner.
	// Normally we would simply return the Plugin-Name here.
	public String getOwnerName() {
		return plugin.getName();
	}
	

	//
	// Load sequences
	//
	
	// Overwrite this if you want to change the name of the section in the config-file where the sections are defined
	protected String getSectionIdentifier() {
		return SECTION_NAME_SEQUENCES;
	}
	
	// Overwrite this if you want to import the section config from another file than the standard plugin config-file
	protected ConfigurationSection getConfigurationSection() {
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
		ConfigurationSection configurationSection = getConfigurationSection();
		if (configurationSection==null) {
			return null;
		}
		return new LibSequenceConfigSection(this, actionValidator, configurationSection);
	}
	

	//
	// Run Sequence (Task creation)
	//
	
	// No need to overwrite this
	// This is just because the Lib itself is not aware of the plugin  
	@Override
	public final BukkitTask scheduleTask(BukkitRunnable task, int wait) {
		return task.runTaskLater(plugin, wait);
	}
	
	
	//
	// Error handling
	//
	
	// Print an info in an check fails
	@Override
	public void onCheckFailed(LibSequenceRunningSequence sequence, String checkName, String failMessage) {
		if (enableConsoleNotifications) {
			String sequenceName = sequence.getName();
			int stepNr = sequence.getStepNr();
			String messageText = "Check failed: " + sequenceName + ": Step " + Integer.toString(stepNr) + ": " + checkName + ": " + failMessage;
			plugin.getLogger().info(messageText);
		}
	}
	
	// Print an info if it seems that a placeholder was not resolved
	@Override
	public void onPlaceholderWarn(LibSequenceRunningSequence sequence, String attributeName, String valueText) {
		if (enableConsoleNotifications) {
			String sequenceName = sequence.getName();
			int stepNr = sequence.getStepNr();
			String messageText = "Possible placeholder did not resolve: " + sequenceName + ": Step " + Integer.toString(stepNr) + ": " + attributeName + ": " + valueText;
			plugin.getLogger().info(messageText);
		}
	}
	
	@Override
	public void printException(LibSequenceException e) {
		plugin.getLogger().warning(e.getMessageCascade());
		if (e.hasJavaException()) {
			e.printStackTrace();
		}		
	}
		
	// Print an waring and optional StackTrace on Error
	public void onExecutionError(LibSequenceRunningSequence sequence, LibSequenceRunException e) {
		if (enableInitiatorNotifications) {
			CommandSender initiator = sequence.getRunOptions().getInitiator();
			if (initiator instanceof Player) {
				initiator.sendMessage(e.getMessage());
			}
		}
		printException(e);
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
