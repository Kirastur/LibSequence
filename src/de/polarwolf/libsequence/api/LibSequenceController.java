package de.polarwolf.libsequence.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigResult;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunResult;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public final class LibSequenceController {
	
	protected final LibSequenceSequencer sequencer;
	protected final LibSequenceCallback callback;
	
 	public LibSequenceController (LibSequenceSequencer sequencer, LibSequenceCallback callback) {
 		this.sequencer=sequencer;
 		this.callback=callback;
 	}
 	
 	public List<String> getNames() {
 		Set<String> names = sequencer.getSequenceNames(callback);
 		return new ArrayList<>(names);
 	}
 	
 	public boolean hasSequence(String sequenceName) {
 		return sequencer.hasOwnSequence(callback, sequenceName);
 	}
 	
 	public boolean hasPermission(Player player, String sequenceName) {
 		String permissionName="libsequence.sequence."+sequenceName;
 		return player.hasPermission(permissionName);
 	}
 	
 	public LibSequenceRunResult execute(String sequenceName, LibSequenceRunOptions runOptions) {
 		return sequencer.executeOwnSequence(callback, sequenceName, runOptions);
 	}
 	
 	public LibSequenceRunResult cancel(String sequenceName) {
 		return sequencer.cancelSequenceByName(callback, sequenceName);
 	}
 	
	public Set<LibSequenceRunningSequence> queryRunningSequences() {
		return sequencer.queryRunningSequences(callback);
	}

 	public LibSequenceConfigResult reload() {
 		return sequencer.reloadSection(callback);
 	}
 
}
