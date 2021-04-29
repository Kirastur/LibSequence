package de.polarwolf.libsequence.includes;

import static de.polarwolf.libsequence.includes.LibSequenceIncludeErrors.*;

import java.util.Set;

import org.bukkit.command.CommandSender;

import de.polarwolf.libsequence.result.LibSequenceResult;

public class LibSequenceIncludeResult extends LibSequenceResult {

	protected Set<CommandSender> senders;
	public final String includeName;
	public final LibSequenceIncludeErrors errorCode;
	public final String errorDetailText;

	public LibSequenceIncludeResult(Set<CommandSender> senders, String includeName, LibSequenceIncludeErrors errorCode, String errorDetailText) {
		super(null);
		this.senders=senders;
		this.includeName=includeName;
		this.errorCode=errorCode;
		this.errorDetailText=errorDetailText;
	}
	
	public Set<CommandSender> getSenders() {
		return senders;
	}


	public void overwriteSenders(Set<CommandSender> senders) {
		this.senders = senders;
	}


	@Override
	public boolean hasError() {
		return errorCode!=LSIERR_OK;
	}

	@Override
	public String getLabel() {
		return errorCode.toString();
	}
	
	@Override
	protected String getErrorText() {
		
		String errorText = getLabel();

		if ((includeName != null) && (!includeName.isEmpty())) {
			errorText = includeName + ": " + errorText; 
		}

		if ((errorDetailText != null) && (!errorDetailText.isEmpty())) {
			errorText = errorText + ": " +  errorDetailText; 
		}

		return errorText; 
	}

}
