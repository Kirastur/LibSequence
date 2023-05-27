package de.polarwolf.libsequence.reload;

import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_NO_CONFIGFILE;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_NO_CONFIGSECTION;
import static de.polarwolf.libsequence.config.LibSequenceConfigErrors.LSCERR_SECTION_NOT_FOUND;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.polarwolf.libsequence.api.LibSequenceProvider;
import de.polarwolf.libsequence.config.LibSequenceConfigException;
import de.polarwolf.libsequence.config.LibSequenceConfigSection;
import de.polarwolf.libsequence.exception.LibSequenceException;
import de.polarwolf.libsequence.token.LibSequenceToken;

/**
 * Specific reloader to read sequences from files
 *
 */
public class LibSequenceReloaderConfigFile implements LibSequenceReloader {

	protected LibSequenceReloaderHelper reloaderHelper;
	protected Map<LibSequenceToken, LibSequenceReloaderConfigFileSetting> settings = new HashMap<>();

	public LibSequenceReloaderHelper getReloaderHelper() {
		return reloaderHelper;
	}

	@Override
	public void setHelper(LibSequenceReloaderHelper reloaderHelper) {
		this.reloaderHelper = reloaderHelper;
	}

	public int add(Plugin plugin, LibSequenceToken ownerToken, String fileName, String fileSection)
			throws LibSequenceConfigException {
		int count = 0;
		LibSequenceReloaderConfigFileSetting newSetting = new LibSequenceReloaderConfigFileSetting(plugin, ownerToken,
				fileName, fileSection);
		reloaderHelper.preregisterSection(ownerToken, plugin.getName());
		settings.put(ownerToken, newSetting);
		try {
			count = reloadSection(newSetting);
		} catch (LibSequenceConfigException e) {
			reloaderHelper.sendReloadedEvent(count, true, e);
			throw e;
		}
		reloaderHelper.sendReloadedEvent(count, true, null);
		return count;
	}

	public void addLater(Plugin plugin, LibSequenceToken ownerToken, String fileName, String fileSection) {
		new LibSequenceReloaderConfigFileScheduler(plugin, this, ownerToken, fileName, fileSection);
	}

	public void remove(LibSequenceToken ownerToken) {
		settings.remove(ownerToken);
		removeSection(ownerToken);
		reloaderHelper.sendReloadedEvent(0, true, null);
	}

	protected int reloadSection(LibSequenceReloaderConfigFileSetting setting) throws LibSequenceConfigException {
		Plugin myPlugin = setting.plugin();
		LibSequenceToken myOwnerToken = setting.ownerToken();
		String myFileName = setting.fileName();
		String myFileSection = setting.fileSection();

		ConfigurationSection mySection;
		if ((myFileName == null) || myFileName.isEmpty()) {
			if (LibSequenceProvider.getAPI() != null) {
				myPlugin.reloadConfig();
			}
			mySection = myPlugin.getConfig().getRoot();
		} else {
			File dataFile = new File(myPlugin.getDataFolder(), myFileName);
			if (!dataFile.exists()) {
				throw new LibSequenceConfigException(myPlugin.getName(), LSCERR_NO_CONFIGFILE, myFileName);
			}
			mySection = YamlConfiguration.loadConfiguration(dataFile);
		}
		if ((myFileSection != null) && !myFileSection.isEmpty()) {
			if (mySection.contains(myFileSection, true)) {
				mySection = mySection.getConfigurationSection(myFileSection);
			} else {
				throw new LibSequenceConfigException(myPlugin.getName(), LSCERR_NO_CONFIGSECTION, myFileSection);
			}
		}
		LibSequenceConfigSection newConfigSection = new LibSequenceConfigSection(myOwnerToken,
				reloaderHelper.getActionValidator(), myPlugin.getName(), mySection);
		reloaderHelper.setSection(myOwnerToken, newConfigSection);
		return newConfigSection.getSize();
	}

	protected void removeSection(LibSequenceToken ownerToken) {
		reloaderHelper.removeSection(ownerToken);
	}

	public int partialReload(LibSequenceToken ownerToken) throws LibSequenceException {
		LibSequenceReloaderConfigFileSetting mySetting = settings.get(ownerToken);
		if (mySetting == null) {
			throw new LibSequenceConfigException(null, LSCERR_SECTION_NOT_FOUND, null);
		}
		mySetting.plugin().reloadConfig();
		int count = reloadSection(mySetting);
		reloaderHelper.sendReloadedEvent(count, true, null);
		return count;
	}

	@Override
	public int reload() throws LibSequenceException {
		int count = 0;
		LibSequenceException lastException = null;
		for (LibSequenceReloaderConfigFileSetting myConfigSetting : settings.values())
			try {
				count = count + reloadSection(myConfigSetting);
			} catch (LibSequenceException e) {
				lastException = e;
			}
		if (lastException != null) {
			throw lastException;
		}
		// ReloadedEvent is sent by the ReloadManager
		return count;
	}

	@Override
	public void clear() {
		for (LibSequenceToken myOwnerToken : new ArrayList<>(settings.keySet())) {
			settings.remove(myOwnerToken);
			removeSection(myOwnerToken);
		}
	}
}
