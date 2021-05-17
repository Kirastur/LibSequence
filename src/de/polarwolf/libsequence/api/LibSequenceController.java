package de.polarwolf.libsequence.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.callback.LibSequenceCallbackExtended;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryException;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public final class LibSequenceController {
	
	protected final LibSequenceDirectory directory;
	protected final LibSequenceCallbackExtended callback;
	
	public static final String OK = "OK";
	

	public LibSequenceController (LibSequenceDirectory directory, LibSequenceCallbackExtended callback, boolean publishLocalSequences) throws LibSequenceDirectoryException {
 		this.directory=directory;
 		this.callback=callback;
 		if (publishLocalSequences) {
 			LibSequenceDirectoryOptions directoryOptions = new LibSequenceDirectoryOptions();
 			directoryOptions.setCanCancel(true);
 			directoryOptions.setCanReload(true);
 			directoryOptions.setIncludeAll(true);
 			directory.registerProvider(callback, directoryOptions);
 		}
 	}
 	

	public List<String> getNames()  {
		Set<String> names = directory.getRunnableSequenceNames();
		return new ArrayList<>(names);
 	}
 	

	public boolean hasSequence(String sequenceName) {
 		return directory.hasRunnableSequence(sequenceName);
 	}
 	

	public boolean hasPermission(CommandSender initiator, String sequenceName) {
 		String permissionName="libsequence.sequence."+sequenceName;
 		return initiator.hasPermission(permissionName);
 	}
 	

	public String execute(String sequenceName, CommandSender initiator) {
		LibSequenceRunOptions runOptions = new LibSequenceRunOptions();
		if (initiator != null) {
			runOptions.setInitiator(initiator);
		}
		try {
			directory.execute(sequenceName, runOptions, callback);
			return OK;
		} catch (LibSequenceDirectoryException e) {
			callback.printException(e);
			return e.getMessage();
		}
 	}
 	

	public int cancel(String sequenceName) {
		try {
			return directory.cancel(sequenceName);
		} catch (LibSequenceDirectoryException e) {
			callback.printException(e);
			return 0;
		}
 	}
 	

	public List<String> getRunningSequenceNames() {
		List<String> sequenceNames = new ArrayList<>();
		for (String name: directory.getAllSequenceNames()) {
			if (!directory.findRunningSequences(name).isEmpty()) {
				sequenceNames.add(name);
			}
		}
		return sequenceNames;
	}


	public String reload(){
		try {
			directory.reload();
			return OK;
		} catch (LibSequenceDirectoryException e) {
			callback.printException(e);
			return e.getMessage();
		}
 	}
 
}
