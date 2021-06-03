package de.polarwolf.libsequence.orchestrator;

import java.util.HashMap;
import java.util.Map;

public class LibSequenceStartOptions {
	
	public static final String OPTION_INCLUDE_COMMAND = "INCLUDE COMMAND";
	public static final String OPTION_ENABLE_CHAIN_EVENTS = "ENABLE CHAIN EVENTS";
	
	protected Map<String,Boolean> options = new HashMap<>();
	protected int maxRunningSequences;
	

	public Boolean getOption(String name) {
		return options.get(name);
	}
	

	public void setOption(String name, boolean value) {
		options.put(name, value);
	}


	public int getMaxRunningSequences() {
		return maxRunningSequences;
	}


	public void setMaxRunningSequences(int maxRunningSequences) {
		this.maxRunningSequences = maxRunningSequences;
	}

}
