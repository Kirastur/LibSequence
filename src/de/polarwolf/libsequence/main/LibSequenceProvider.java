package de.polarwolf.libsequence.main;

import de.polarwolf.libsequence.api.LibSequenceAPI;

public class LibSequenceProvider {

	private static LibSequenceAPI lsAPI;
	
	private LibSequenceProvider () {
	}

    protected static void setAPI (LibSequenceAPI newAPI) {
    	lsAPI=newAPI;
    }
    
    public static LibSequenceAPI getAPI() {
    	return lsAPI;
    }
    
}
