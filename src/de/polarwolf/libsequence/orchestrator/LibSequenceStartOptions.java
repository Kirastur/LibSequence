package de.polarwolf.libsequence.orchestrator;

/**
 * Options for orchestrator/sequencer start
 *
 */
public record LibSequenceStartOptions(int maxRunningSequences, boolean includeCommand, boolean includeChain) {

}
