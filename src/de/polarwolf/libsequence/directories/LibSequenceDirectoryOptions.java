package de.polarwolf.libsequence.directories;

public class LibSequenceDirectoryOptions {
	
	protected boolean bCanCancel = false;
	protected boolean bCanReload = false;
	protected boolean bIncludeAll = false;

	
	public boolean canCancel() {
		return bCanCancel;
	}


	public void setCanCancel(boolean canCancel) {
		this.bCanCancel = canCancel;
	}


	public boolean canReload() {
		return bCanReload;
	}


	public void setCanReload(boolean canReload) {
		this.bCanReload = canReload;
	}


	public boolean isIncludeAll() {
		return bIncludeAll;
	}

	
	public void setIncludeAll(boolean includeAll) {
		this.bIncludeAll = includeAll;
	}
	

}
