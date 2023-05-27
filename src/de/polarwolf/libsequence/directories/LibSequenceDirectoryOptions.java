package de.polarwolf.libsequence.directories;

/**
 * Set access-options for published sequences. Before you can publish your own
 * sequences in a directory, you must register yourself (which means your
 * ownerToken-object) at the directory. During this registration you must define
 * which permission you will grant to the directory. For this you need the
 * LibSequenceDirectoryOptions-object.
 *
 * @see <A href=
 *      "https://github.com/Kirastur/LibSequence/wiki/directoryOptions">Directory
 *      Options</A> (WIKI)
 */
public record LibSequenceDirectoryOptions(boolean canCancel, boolean includeAll) {

}
