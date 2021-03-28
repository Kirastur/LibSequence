package de.polarwolf.libsequence.api;

public class LibSequenceAPI {

	private final LibSequenceSequencer sequencer;
	private final LibSequenceController controller;

	public LibSequenceAPI(LibSequenceSequencer sequencer, LibSequenceController controller) {
		this.sequencer=sequencer;
		this.controller=controller;
	}
	
	public LibSequenceSequencer getSequencer() {
		return sequencer;
	}
	
	public LibSequenceController getController() {
		return controller;
	}
}
