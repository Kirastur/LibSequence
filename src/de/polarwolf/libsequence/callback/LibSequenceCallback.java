package de.polarwolf.libsequence.callback;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.polarwolf.libsequence.actions.LibSequenceActionResult;
import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.config.LibSequenceConfigStep;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceCallback {
	
	
	// If using config-file as input, in which section of the config file we can find our sequences to load
	public ConfigurationSection getConfigurationSection();
	
	// This is called when a Load or reload is called so the ConfugManager must read the config data from external source, e.g. config-file
	public LibSequenceConfigSection createConfigSection (LibSequenceActionValidator actionValidator);
	
	// The wait task is always executed in the context of the plugin who created the sequence
	// not the plugin which has registered the action
	// not the plugin which has called the DoExecute
	public BukkitTask scheduleTask (BukkitRunnable task, Integer wait);
	
	// This method is called if an error occurs during action execution 
	public void onExecutionError (LibSequenceRunningSequence secuence, LibSequenceActionResult actionError);
	
	// Only notification on certain milestones. It is safe to ignore
	public void debugSequenceStarted(LibSequenceRunningSequence sequence);
	public void debugSequenceStepReached(LibSequenceRunningSequence sequence, LibSequenceConfigStep step);
	public void debugSequenceCancelled(LibSequenceRunningSequence sequence);
	public void debugSequenceFinished(LibSequenceRunningSequence sequence);

}
