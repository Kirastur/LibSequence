package de.polarwolf.libsequence.callback;

import de.polarwolf.libsequence.exception.LibSequenceException;

public interface LibSequenceCallbackExtended extends LibSequenceCallback {

	public void printException(LibSequenceException e); 

}