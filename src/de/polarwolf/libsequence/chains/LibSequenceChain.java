package de.polarwolf.libsequence.chains;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public interface LibSequenceChain {

	// Only one chain can touch the runOptions on a sequence start
	// FALSE: No hit, the runOptions are not modified
	// TRUE: Hit, the runOptions are modified (the ongoing resolvers will not be called by the ChainManager)
	public boolean resolveChain(LibSequenceRunOptions runOptions);

}
