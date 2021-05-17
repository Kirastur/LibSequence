package de.polarwolf.libsequence.directories;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.polarwolf.libsequence.api.LibSequenceSequencer;
import de.polarwolf.libsequence.callback.LibSequenceCallback;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;
import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

import static de.polarwolf.libsequence.directories.LibSequenceDirectoryErrors.*;


public class LibSequenceDirectoryManager {
	
	protected final LibSequenceSequencer sequencerAPI;
		
	protected Map<LibSequenceCallback,LibSequenceDirectoryOptions> providers = new HashMap<>();
	protected Map<String, LibSequenceCallback> sequenceMap = new HashMap<>();
	
	
	public LibSequenceDirectoryManager(LibSequenceSequencer sequencerAPI) {
		this.sequencerAPI = sequencerAPI;
	}
	

	// Register a new Directory Provider
	// If this is a registration for an existing callback, the old registration gets deleted
	public void registerProvider(LibSequenceCallback callback, LibSequenceDirectoryOptions directoryOptions) throws LibSequenceDirectoryException {
		if (callback == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CALLBACK_IS_NULL, null);
		}

		if (directoryOptions == null) {
			directoryOptions = new LibSequenceDirectoryOptions();
		}
		
		sequencerAPI.preregisterSection(callback);
		
		providers.put(callback, directoryOptions);
		if (directoryOptions.isIncludeAll()) {
			syncAllMySequences(callback);			
		}
	}
	
	
	// Remove a Directory Provider
	public void unregisterProvider(LibSequenceCallback callback) {
		clearAllMySequences(callback);
		providers.remove(callback);
	}
	

	public boolean hasDirectoryProvider(LibSequenceCallback callback) {
		return providers.containsKey(callback);
	}
	

	// Register an new sequence name.
	// You cannot register a name which is already registered for another provider (callback/plugin)
	// except the old register is no longer valid
	public void addSequence(String sequenceName, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		if ((sequenceName == null) || (sequenceName.isEmpty())) {
			throw new LibSequenceDirectoryException(null, LSDERR_NAME_IS_EMPTY, null);
		}
		if (callback == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_CALLBACK_IS_NULL, sequenceName);
		}
		if (!hasDirectoryProvider(callback)) {
			throw new LibSequenceDirectoryException(null, LSDERR_CALLBACK_NOT_REGISTERED, null);
		}

		LibSequenceCallback owner = sequenceMap.get(sequenceName);
		if ((owner != null) && (owner != callback) && sequencerAPI.hasOwnSequence(owner, sequenceName)) {
			throw new LibSequenceDirectoryException(sequenceName, LSDERR_SEQUENCE_ALREADY_REGISTERED, owner.getOwnerName());
		}
		sequenceMap.put(sequenceName, callback);
	}
	

	public void removeSequence(String sequenceName, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		if (!sequenceMap.containsKey(sequenceName)) {
			return;
		}

		LibSequenceCallback owner = sequenceMap.get(sequenceName);
		if ((owner != null) && (owner != callback) && sequencerAPI.hasOwnSequence(owner, sequenceName)) {
			throw new LibSequenceDirectoryException(sequenceName, LSDERR_ONLY_OWNER_CAN_REMOVE, owner.getOwnerName());							
		}
		sequenceMap.remove(sequenceName);		
	}
	

	public int clearAllMySequences(LibSequenceCallback callback) {
		int count = 0;
		Iterator<LibSequenceCallback> it = sequenceMap.values().iterator();
		while (it.hasNext()) {
		    LibSequenceCallback sequenceCallback = it.next();
		    if (sequenceCallback == callback) {
		    	it.remove();
		    	count = count +1;
		    }
		}
		return count;
	}
	

	public int syncAllMySequences(LibSequenceCallback callback) throws LibSequenceDirectoryException {
		int count = 0;
		Set<String>sequenceNames = new HashSet<>();
		LibSequenceDirectoryException lastDirectoryException = null;
		clearAllMySequences(callback);

		try {
			sequenceNames.addAll(sequencerAPI.getSequenceNames(callback));
		} catch (LibSequenceConfigException e) {
			throw new LibSequenceDirectoryException(null, LSDERR_FAILED_GETTING_NAMES, callback.getOwnerName(), e);
		}

		for (String sequenceName : sequenceNames) {
			try {
				addSequence(sequenceName, callback);
				count = count +1;
			} catch (LibSequenceDirectoryException e) {
				lastDirectoryException = e;
			}
		}
		
		if (lastDirectoryException != null) {
			throw lastDirectoryException;
		}

		return count;
	}
	

	public boolean hasRunnableSequence(String sequenceName) {
		LibSequenceCallback callback = sequenceMap.get(sequenceName);
		if (callback == null) {
			return false;
		}
		return sequencerAPI.hasOwnSequence(callback, sequenceName);	
	}

	
	public final boolean isMySequence(String sequenceName, LibSequenceCallback callback) {
		return (sequenceMap.get(sequenceName) == callback);
	}
	

	public String getSequenceOwnerName(String sequenceName) throws LibSequenceDirectoryException {
		LibSequenceCallback callback = sequenceMap.get(sequenceName);
		if (callback == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}

		return callback.getOwnerName();
	}
	

	public Set<String> getRunnableSequenceNames() {
		Set<String> runnableSequenceNames = new HashSet<>();
				
		for (Map.Entry<String, LibSequenceCallback> entry : sequenceMap.entrySet()) {
			String sequenceName = entry.getKey();
			LibSequenceCallback callback = entry.getValue();
			if (sequencerAPI.hasOwnSequence(callback, sequenceName)) {
				runnableSequenceNames.add(sequenceName);
			}
		}		
		return runnableSequenceNames;		
	}
	
	public Set<String> getAllSequenceNames() {
		return sequenceMap.keySet();
	}
	

	public LibSequenceRunningSequence execute(String sequenceName, LibSequenceRunOptions runOptions, LibSequenceCallback callback) throws LibSequenceDirectoryException {
		LibSequenceCallback owner = sequenceMap.get(sequenceName);
		if (owner == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}
		
		if (!sequencerAPI.hasOwnSequence(owner, sequenceName)) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_RUNNABLE, sequenceName);
		}
		
		try {
			String token = sequencerAPI.getSecurityToken(owner, sequenceName);
			return sequencerAPI.executeForeignSequence(callback, token, runOptions);
		} catch (LibSequenceException e) {
			throw new LibSequenceDirectoryException(sequenceName, LSDERR_ERROR_DURING_SEQUENCE_START, null, e);
		}
	}

	
	public Set<LibSequenceRunningSequence> findRunningSequences(String sequenceName) {
		Set<LibSequenceRunningSequence> allSequences = new HashSet<>();

		LibSequenceCallback callback = sequenceMap.get(sequenceName);
		if (callback == null) {
			return allSequences;
		}
		
		LibSequenceDirectoryOptions directoryOptions = providers.get(callback);
		if (!directoryOptions.bCanCancel) {
			return allSequences;
		}

		Set<LibSequenceRunningSequence> sectionSequences = sequencerAPI.sneakRunningSequencesOwnedByMe(callback);
	    for (LibSequenceRunningSequence sequence : sectionSequences) {
	    	if (sequenceName.equals(sequence.getName())) {
	    		allSequences.add(sequence);
	    	}
	    }
		return allSequences;
	}
	

	public int cancel(String sequenceName) throws LibSequenceDirectoryException {
		LibSequenceCallback callback = sequenceMap.get(sequenceName);
		if (callback == null) {
			throw new LibSequenceDirectoryException(null, LSDERR_SEQUENCE_NOT_FOUND, sequenceName);
		}
		
		LibSequenceDirectoryOptions directoryOptions = providers.get(callback);
		if (!directoryOptions.bCanCancel) {
			throw new LibSequenceDirectoryException(null, LSDERR_ACCESS_DENIED, sequenceName);
		}

		Set<LibSequenceRunningSequence> allSequences = findRunningSequences(sequenceName);
		for (LibSequenceRunningSequence sequence : allSequences) {
			sequence.cancel();
		}

		return allSequences.size();
	}
	

	public void reload() throws LibSequenceDirectoryException {
		LibSequenceConfigException lastConfigException = null;
		LibSequenceDirectoryException lastDirectoryException = null;
		
		for (Map.Entry<LibSequenceCallback, LibSequenceDirectoryOptions> entry : providers.entrySet()) {
			LibSequenceCallback callback = entry.getKey();
			LibSequenceDirectoryOptions directoryOptions = entry.getValue();
			if (directoryOptions.bCanReload) try {
				sequencerAPI.loadSection(callback);
			} catch (LibSequenceConfigException e) {
				lastConfigException = e;
			}
			if (directoryOptions.bIncludeAll) try {
				syncAllMySequences(callback);
			} catch (LibSequenceDirectoryException e) {
				lastDirectoryException = e;
			}
		}
		
		if (lastConfigException != null) {
			throw new LibSequenceDirectoryException(null, LSDERR_ERROR_DURING_SEQUENCE_LOAD, lastConfigException.getContextName(), lastConfigException);
		}
		if (lastDirectoryException != null ) {
			throw lastDirectoryException;
		}
	}
	
}
