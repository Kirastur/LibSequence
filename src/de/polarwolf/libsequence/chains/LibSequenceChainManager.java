package de.polarwolf.libsequence.chains;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequenceChainManager {
	
	protected List<LibSequenceChain> chains = new ArrayList<>();
	
	public void registerChain(LibSequenceChain chain) {
		chains.add(chain);
	}
	
	public void resolveChain(LibSequenceRunOptions runOptions) {
		for (LibSequenceChain chain : chains) {
			if (chain.resolveChain(runOptions)) {
				return;
			}
		}
	}

}
