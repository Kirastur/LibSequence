package de.polarwolf.libsequence.checks;

import static de.polarwolf.libsequence.checks.LibSequenceCheckErrors.*;

import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.runnings.LibSequenceRunOptions;

public class LibSequenceCheckCondition implements LibSequenceCheck {

	@Override
	public LibSequenceCheckResult performCheck (String checkName, String valueText, Plugin plugin, LibSequenceRunOptions runOptions) {
		if ((valueText == null) || (valueText.isEmpty())) {
			return new LibSequenceCheckResult(checkName, LSCERR_VALUE_MISSING, null);
		}
		
    	if (valueText.equalsIgnoreCase("yes") || valueText.equalsIgnoreCase("true")) {
    		return new LibSequenceCheckResult(checkName, LSCERR_OK, null);
    	}
    	
    	try {
    		double d = Double.parseDouble(valueText);
    		if (d >= 1) {
        		return new LibSequenceCheckResult(checkName, LSCERR_OK, null);   			
    		} else {    			
        		return new LibSequenceCheckResult(checkName, LSCERR_FALSE, valueText);   			
    		}
    	} catch (Exception e) {
    		return new LibSequenceCheckResult(checkName, LSCERR_NOT_NUMERIC, valueText);   			
    	}
    }
}
