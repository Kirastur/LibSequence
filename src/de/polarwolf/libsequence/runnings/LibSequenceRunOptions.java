package de.polarwolf.libsequence.runnings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LibSequenceRunOptions {
	
	public static final String RUNOPTION_NAME = "NAME";
	public static final String RUNOPTION_PLAYER = "PLAYER";
	
	protected Map<String,String> placeholders = new HashMap<>();
	protected List<String> authorizationKeys = new ArrayList<>();
	protected CommandSender initiator;
	protected Player chainPlayer;
	protected boolean singleton = false;
	
	public void addPlaceholder (String name, String value) {
		placeholders.put (name, value);
	}
	
	public String findPlaceholder(String name) {
		return placeholders.get(name);
	}
	
	public Set<String> listPlaceholders() {
		return placeholders.keySet();
	}

	public void addAuthorizationKey(String newKey) {
		authorizationKeys.add(newKey);
	}
	
	// This function is final to protected
	// the AuthorizationKey-Stealing during action authorization
	public final boolean verifyAuthorizationKey(String keyToCheck) {
		return authorizationKeys.contains(keyToCheck);
	}

	public CommandSender getInitiator() {
		return initiator;
	}

	public void setInitiator(CommandSender initiator) {
		this.initiator = initiator;
		if (initiator != null) {
			String userName = initiator.getName();
			String displayName = userName;
			if (initiator instanceof Player) {
				Player player = (Player)initiator;
				displayName=player.getDisplayName();
			}
			placeholders.put(RUNOPTION_NAME, userName);
			placeholders.put(RUNOPTION_PLAYER, displayName);
		}
	}
		
	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
	public String getLocale() {
		if (!(initiator instanceof Player)) {
			return null;
		}
		Player player = (Player)initiator;
		return player.getLocale();
		}
	

}
