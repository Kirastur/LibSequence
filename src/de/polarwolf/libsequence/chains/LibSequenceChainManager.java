package de.polarwolf.libsequence.chains;

import java.util.ArrayList;
import java.util.List;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.orchestrator.LibSequenceOrchestrator;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

/**
 * Manage all possible chains.
 *
 * @see de.polarwolf.libsequence.chains.LibSequenceChain LibSequenceChain
 * @see <A href="https://github.com/Kirastur/LibSequence/wiki/Chains">Chains</A>
 *      (WIKI)
 */
public class LibSequenceChainManager {

	protected List<LibSequenceChain> chains = new ArrayList<>();

	public LibSequenceChainManager(LibSequenceOrchestrator orchestrator) {
		// Prevent from starting the Manager without having an orchestrator
	}

	public void registerChain(LibSequenceChain chain) {
		chains.add(chain);
	}

	public void resolveChain(LibSequenceRunOptions runOptions) throws LibSequenceException {
		for (LibSequenceChain myChain : chains) {
			if (myChain.resolveChain(runOptions)) {
				return;
			}
		}
	}

	public void disable() {
		for (LibSequenceChain myChain : chains) {
			myChain.disable();
		}
	}

}
