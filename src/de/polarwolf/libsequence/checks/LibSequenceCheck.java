package de.polarwolf.libsequence.checks;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public interface LibSequenceCheck {
	
	public LibSequenceCheckResult performCheck (String checkName, String valueText, Plugin plugin, LibSequenceRunOptions runOptions);

}
