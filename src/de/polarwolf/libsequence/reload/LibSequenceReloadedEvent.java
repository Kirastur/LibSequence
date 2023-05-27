package de.polarwolf.libsequence.reload;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.polarwolf.libsequence.actions.LibSequenceActionValidator;
import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Event thrown after all sequences are reloaded
 *
 */
public class LibSequenceReloadedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final LibSequenceActionValidator actionValidator;
	private final int count;
	private final boolean bPartial;
	private final LibSequenceException lastException;

	LibSequenceReloadedEvent(LibSequenceActionValidator actionValidator, int count, boolean isPartial,
			LibSequenceException lastException) {
		this.actionValidator = actionValidator;
		this.count = count;
		this.bPartial = isPartial;
		this.lastException = lastException;
	}

	public LibSequenceActionValidator getactionValidator() {
		return actionValidator;
	}

	public int getCount() {
		return count;
	}

	public boolean isPartial() {
		return bPartial;
	}

	public LibSequenceException getLastException() {
		return lastException;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}