package de.polarwolf.libsequence.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.runnings.LibSequenceRunException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public final class LibSequenceController {
	
	protected final LibSequenceSequencer sequencer;
	protected final LibSequenceCallback callback;
	

	public LibSequenceController (LibSequenceSequencer sequencer, LibSequenceCallback callback) {
 		this.sequencer=sequencer;
 		this.callback=callback;
 	}
 	

	public List<String> getNames()  {
		try {
			Set<String> names = sequencer.getSequenceNames(callback);
			return new ArrayList<>(names);
		} catch (Exception e) {
			return new ArrayList<>();			
		}
 	}
 	

	public boolean hasSequence(String sequenceName) {
 		return sequencer.hasOwnSequence(callback, sequenceName);
 	}
 	

	public boolean hasPermission(Player player, String sequenceName) {
 		String permissionName="libsequence.sequence."+sequenceName;
 		return player.hasPermission(permissionName);
 	}
 	

	public void execute(String sequenceName, LibSequenceRunOptions runOptions) throws LibSequenceRunException {
 		sequencer.executeOwnSequence(callback, sequenceName, runOptions);
 	}
 	

	public int cancel(String sequenceName) {
 		return sequencer.cancelSequenceByName(callback, sequenceName);
 	}
 	

	public Set<LibSequenceRunningSequence> findRunningSequences() {
		return sequencer.findRunningSequences(callback);
	}


	public void reload() throws LibSequenceConfigException {
 		sequencer.loadSection(callback);
 	}
 
}
