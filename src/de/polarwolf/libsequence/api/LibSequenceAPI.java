package de.polarwolf.libsequence.api;

public class LibSequenceAPI {

	private final LibSequenceSequencer sequencer;
	private final LibSequenceDirectory directory;
	private final LibSequenceController controller;


	public LibSequenceAPI(LibSequenceSequencer sequencer, LibSequenceDirectory directory, LibSequenceController controller) {
		this.sequencer=sequencer;
		this.directory = directory;
		this.controller=controller;
	}
	

	public LibSequenceSequencer getSequencer() {
		return sequencer;
	}

	
	public LibSequenceDirectory getPublicDirectory() {
		return directory;
	}
	

	public LibSequenceController getController() {
		return controller;
	}

}
