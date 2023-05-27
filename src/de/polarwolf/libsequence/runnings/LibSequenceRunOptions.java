package de.polarwolf.libsequence.runnings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.polarwolf.libsequence.logger.LibSequenceLogger;
import de.polarwolf.libsequence.logger.LibSequenceLoggerDefault;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * The runOption defines additional options for sequence execution.
 *
 */
public class LibSequenceRunOptions {

	public static final String RUNOPTION_NAME = "NAME";
	public static final String RUNOPTION_PLAYER = "PLAYER";

	private List<LibSequenceToken> authorizationTokens = new ArrayList<>();

	protected Map<String, String> placeholders = new HashMap<>();
	protected CommandSender initiator;
	protected boolean singleton = false;
	protected LibSequenceLogger logger;

	/**
	 * Create a new RunOption with a default Logger
	 */
	public LibSequenceRunOptions() {
		logger = new LibSequenceLoggerDefault(null);
	}

	/**
	 * Add a Placeholder. The action can lateron call "resolvePlaceholder" to
	 * replace a placeholder with a given value.
	 *
	 * @param name  Name of the placeholder (the String to search for)
	 * @param value Replacement (The String to which the placeholder should replaced
	 *              with)
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
	 *      (WIKI)
	 */
	public void addPlaceholder(String name, String value) {
		placeholders.put(name, value);
	}

	/**
	 * Get the value for a given placeholder
	 *
	 * @param name The placeholder to search for
	 * @return The value to replace the given placeholder with
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
	 *      (WIKI)
	 */
	public String findPlaceholder(String name) {
		return placeholders.get(name);
	}

	/**
	 * Get a list of defined placeholders
	 *
	 * @return set of all defined placeholders
	 *
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Placeholders">Placeholders</A>
	 *      (WIKI)
	 */
	public Set<String> listPlaceholders() {
		return placeholders.keySet();
	}

	/**
	 * Set an authorization key. It is used by some actions to verify that the
	 * sequence is allowed to use this action.
	 *
	 * @param newToken An AuthorizationToken to proof that the sequence has the
	 *                 permission to exectute a specific action.
	 *
	 * @see de.polarwolf.libsequence.actions.LibSequenceActionGeneric#validateAuthorizationByToken
	 */
	public void addAuthorizationToken(LibSequenceToken newToken) {
		if (newToken != null) {
			authorizationTokens.add(newToken);
		}
	}

	// This function is final to protected
	// AuthorizationKey-Stealing during action authorization
	/**
	 * Test if the runOptions contains a specific AuthorizationToken. This function
	 * is used by custom actions to verify that the caller has provided the
	 * authorizationToken so he is allowed to execute the action.
	 *
	 * @param tokenToCheck The AuthorizationToken the custom action expects to be
	 *                     include din the runOptions
	 * @return TRUE if the runOpion contains the needed Token, otherwise FALSE
	 */
	public final boolean verifyAuthorizationToken(LibSequenceToken tokenToCheck) {
		for (LibSequenceToken myToken : authorizationTokens) {
			if (myToken.equals(tokenToCheck)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the initiator of the sequence. Can be NULL if no initiator was set.
	 *
	 * @return Initiator
	 */
	public CommandSender getInitiator() {
		return initiator;
	}

	/**
	 * Set the initiator of a sequence. This automatically creates the two
	 * predefined placeholders %NAME% and "%PLAYER%.
	 *
	 * Together with the "initiator" attribute of the "command"-action you can
	 * control in which context the command should be executed.
	 *
	 * The "check" action performs its checks against the given initiator.
	 *
	 * @param initiator The initiator of the sequence start
	 */
	public void setInitiator(CommandSender initiator) {
		this.initiator = initiator;
		if (initiator != null) {
			String userName = initiator.getName();
			String displayName = userName;
			if (initiator instanceof Player player) {
				displayName = player.getDisplayName();
			}
			placeholders.put(RUNOPTION_NAME, userName);
			placeholders.put(RUNOPTION_PLAYER, displayName);
		}
	}

	/**
	 * Query if the section is declared as "singleton" which means that only one
	 * instance of this sequence can be run at the same time
	 *
	 * @return TUE if this runRotopns declare the execution as singleton, otherwise
	 *         FALSE
	 */
	public boolean isSingleton() {
		return singleton;
	}

	/**
	 * A sequence set to Singleton can run only once at the same time.
	 *
	 * @param singleton True if the sequence should be treated as Singleton,
	 *                  otherwise FALSE
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	/**
	 * Get the locale of the initiator to prepare localized messages
	 *
	 * @return String containing the locale, e.g. "de_de", or NULL if the locale
	 *         could not be determined
	 */
	public String getLocale() {
		if (initiator instanceof Player player) {
			return player.getLocale();
		}
		return null;
	}

	/**
	 * Get the current logger for printing info and debug messages
	 *
	 * @return Current logger
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Logger">Logger</A> (WIKI)
	 */
	public LibSequenceLogger getLogger() {
		return logger;
	}

	/**
	 * Set the current logger for printing info and debug messages
	 *
	 * @param logger New Logger
	 * @see <A href=
	 *      "https://github.com/Kirastur/LibSequence/wiki/Logger">Logger</A> (WIKI)
	 */
	public void setLogger(LibSequenceLogger logger) {
		this.logger = logger;
	}

	// Authorization keys are not included in the copy
	/**
	 * Build a copy of the current runOption. The copy doesn't include the
	 * authorizationTokens. The copy is needed to resolve placeholders for a
	 * different player than the initiator.
	 *
	 * @return
	 */
	public LibSequenceRunOptions getCopy() {
		LibSequenceRunOptions newRunOptions = new LibSequenceRunOptions();
		newRunOptions.initiator = initiator;
		newRunOptions.singleton = singleton;
		newRunOptions.logger = logger;
		for (Map.Entry<String, String> entry : placeholders.entrySet()) {
			String attributeName = entry.getKey();
			String attributeValue = entry.getValue();
			newRunOptions.placeholders.put(attributeName, attributeValue);
		}
		return newRunOptions;
	}

}
