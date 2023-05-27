package de.polarwolf.libsequence.reload;

import de.polarwolf.libsequence.exception.LibSequenceException;

/**
 * Interface for developing own reloaders
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/ReloadManager">Reload
 *      Manager</A> (WIKI)
 */
public interface LibSequenceReloader {

	public void setHelper(LibSequenceReloaderHelper reloaderHelper);

	public int reload() throws LibSequenceException;

	public void clear();

}
