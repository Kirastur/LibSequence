package de.polarwolf.libsequence.callback;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceCallbackGeneric implements LibSequenceCallback{
	
	private static final String SECTION_NAME_SEQUENCES = "sequences";
	
	protected final Plugin plugin;
	
	public LibSequenceCallbackGeneric(Plugin plugin) {
		this.plugin=plugin;
	}
	
	// Overwrite this if you want to change the name of the section in the config-file where the sections are defined
	protected String getSectionIdentifier() {
		return SECTION_NAME_SEQUENCES;
	}
	
	// Overwrite this if you want to import the section config from another place than the config-file
	public ConfigurationSection getConfigurationSection () {
		plugin.reloadConfig();
		ConfigurationSection sectionRoot = plugin.getConfig().getRoot();
		if (!sectionRoot.contains (getSectionIdentifier(), true)) {
			return null;
		}
		return sectionRoot.getConfigurationSection(getSectionIdentifier());
	}
	
	// Overwrite this if you want to extend the config handling itself
	@Override
	public LibSequenceConfigSection createConfigSection (LibSequenceActionValidator actionValidator) {
		ConfigurationSection config = getConfigurationSection();
		if (config==null) {
			return null;
		}
		return new LibSequenceConfigSection(this, actionValidator, config);
	}
	
	// Override this for custom error handling
	public void onExecutionError (LibSequenceRunningSequence secuence, LibSequenceActionResult actionError) {
		plugin.getLogger().warning(actionError.toString());		
	}
	
	// No need to overwrite this
	// This is just because the Lib itself is not aware of the plugin  
	@Override
	public BukkitTask scheduleTask (BukkitRunnable task, int wait) {
		return task.runTaskLater(plugin, wait);
	}
	
	// Overwrite this for custom message handling
	// Per default all is written to the console
	public void printSequenceMessage(LibSequenceRunningSequence sequence, String message) {
		plugin.getLogger().info("Sequence "+sequence.getName()+" "+message);
			}
	
	@Override
	public void debugSequenceStarted(LibSequenceRunningSequence sequence) {
		printSequenceMessage(sequence, "has started");
	}
	
	@Override
	public void debugSequenceStepReached(LibSequenceRunningSequence sequence, LibSequenceConfigStep step) {
		printSequenceMessage(sequence, "has reached Step "+sequence.getStepNr()+" and will now execute "+step.getActionName());
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
