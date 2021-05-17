package de.polarwolf.libsequence.api;

import java.util.Set;

import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryException;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryManager;
import de.polarwolf.libsequence.directories.LibSequenceDirectoryOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public class LibSequenceDirectory {
	
	protected final LibSequenceDirectoryManager directoryManager;
	
	public LibSequenceDirectory(LibSequenceSequencer sequencerAPI) {
		directoryManager = new LibSequenceDirectoryManager(sequencerAPI);
	}

	// Register a new Directory Provider
	// If this is a registration for an existing callback, the old registration gets deleted
	public void registerProvider(LibSequenceCallback callback, LibSequenceDirectoryOptions directoryOptions) throws LibSequenceDirectoryException {
		directoryManager.registerProvider(callback, directoryOptions);
	}
	
	
	// Remove a Directory Provider
	public void unregisterProvider(LibSequenceCallback callback) {
		directoryManager.unregisterProvider(callback);
	}
	

	public boolean hasDirectoryProvider(LibSequenceCallback callback) {
		return directoryManager.hasDirectoryProvider(callback);
	}
	

	// Register an new sequence name.
	// You cannot register a name which is already registered for another provider (callback/plugin)
	// except the old register is no longer valid
	public void addSequence(String sequenceName, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		directoryManager.addSequence(sequenceName, callback);
	}
	

	public void removeSequence(String sequenceName, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		directoryManager.removeSequence(sequenceName, callback);
	}
	

	public int clearAllMySequences(LibSequenceCallback callback) {
		return directoryManager.clearAllMySequences(callback);
	}
	

	public int syncAllMySequences(LibSequenceCallback callback) throws LibSequenceDirectoryException {
		return directoryManager.syncAllMySequences(callback);
	}
	

	public boolean hasRunnableSequence(String sequenceName) {
		return directoryManager.hasRunnableSequence(sequenceName);
	}

	
	public final boolean isMySequence(String sequenceName, LibSequenceCallback callback) {
		return directoryManager.isMySequence(sequenceName, callback);
	}
	

	public String getSequenceOwnerName(String sequenceName) throws LibSequenceDirectoryException {
		return directoryManager.getSequenceOwnerName(sequenceName);
	}
	

	public Set<String> getRunnableSequenceNames() {
		return directoryManager.getRunnableSequenceNames();
	}
	

	public Set<String> getAllSequenceNames() {
		return directoryManager.getAllSequenceNames();
	}

		
	public LibSequenceRunningSequence execute(String sequenceName, LibSequenceRunOptions runOptions, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		return directoryManager.execute(sequenceName, runOptions, callback);
	}

	
	public Set<LibSequenceRunningSequence> findRunningSequences(String sequenceName) {
		return directoryManager.findRunningSequences(sequenceName);
	}
	

	public int cancel(String sequenceName) throws LibSequenceDirectoryException {
		return directoryManager.cancel(sequenceName);
	}
	

	public void reload() throws LibSequenceDirectoryException {
		directoryManager.reload();
	}

}
