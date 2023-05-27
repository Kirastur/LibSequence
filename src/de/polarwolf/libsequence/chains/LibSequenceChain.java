package de.polarwolf.libsequence.chains;

import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

/**
 * Defines a chain-resolver. A chain is used at sequence start to identify the
 * initiator if there are more than one element involved, e.g. <I>Player =>
 * Button => CommandBlock => Sequence</I> or <I>Player => Pressure Plate =>
 * CommandBlock => Sequence</I><BR>
 * For this, the Chain Resolver listens to PlayerInteraction Events and collect
 * them into a buffer. You can disable the listener in the configuration
 * file.<BR>
 * The out-of-the-box chain resolver does not detect Tripwire or Redstone. But
 * you can extend the LibSequence with your own custom chain resolver using this
 * interface.<BR>
 * Only one chain can touch the runOptions on a sequence start
 *
 * @see de.polarwolf.libsequence.chains.LibSequenceChainManager ChainManager
 * @see <A href="https://github.com/Kirastur/LibSequence/wiki/Chains">Chains</A>
 *      (WIKI)
 */
public interface LibSequenceChain {

	/**
	 * Perform a chain resolving at sequence start
	 *
	 * @param runOptions Sequence to check for
	 * @return TRUE if this chain-resolver has detected a hit and therefore modified
	 *         the runOptions (the ongoing resolvers will not be called by the
	 *         ChainManager), otherwise FALSE.
	 * @throws LibSequenceException Exception thrown during chain resolving
	 */
	public boolean resolveChain(LibSequenceRunOptions runOptions) throws LibSequenceException;

	/**
	 * Disable the chain resolver because the LibSequence instance is shutting down.
	 * You must unregister all listener now.
	 */
	public void disable();

}
