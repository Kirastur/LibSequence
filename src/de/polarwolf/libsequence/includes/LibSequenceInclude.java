package de.polarwolf.libsequence.includes;

import de.polarwolf.libsequence.runnings.LibSequenceRunningSequence;

public interface LibSequenceInclude {
		
	public LibSequenceIncludeResult performInclude(String includeName, String valueText, boolean inverseSearch, LibSequenceRunningSequence runningSequence);

}
