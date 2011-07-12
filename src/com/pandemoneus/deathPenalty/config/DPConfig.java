package com.pandemoneus.deathPenalty.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import com.pandemoneus.deathPenalty.DeathPenalty;
import com.pandemoneus.deathPenalty.logger.Log;

/**
 * The configuration file for the DeathPenalty plugin, uses YML.
 * 
 * @author Pandemoneus
 * 
 */
public final class DPConfig {

	private DeathPenalty plugin;
	private static String pluginName;
	private static String pluginVersion;

	/**
	 * File handling
	 */
	private static String directory = "plugins" + File.separator
			+ DeathPenalty.getPluginName() + File.separator;
	private File configFile = new File(directory + File.separator
			+ "config.yml");
	private Configuration bukkitConfig = new Configuration(configFile);

	/**
	 * Default settings
	 */
	private double targetMinMoney = 0.0;
	private double penaltyMoney = 0.0;
	private double penaltyMoneyInPercent = 0.0;
	private boolean floorAfterSubtraction = false;
	private boolean balanceCanBeNegative = false;
	private boolean noPenaltyInWorldGuardRegions = false;
	private boolean showMsgOnDeath = true;
	private String msgNotEnoughMoney = "Lucky you! You didn't lose any money.";
	private String msgLostMoney = "You lost <Money>!";
	

	/**
	 * Associates this object with a plugin
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public DPConfig(DeathPenalty plugin) {
		this.plugin = plugin;
		pluginName = DeathPenalty.getPluginName();
	}

	/**
	 * Loads the configuration used by this plugin.
	 * 
	 * @return true if config loaded without errors
	 */
	public boolean loadConfig() {
		boolean isErrorFree = true;
		pluginVersion = DeathPenalty.getVersion();

		new File(directory).mkdir();

		if (configFile.exists()) {
			bukkitConfig.load();
			if (bukkitConfig.getString("Version", "").equals(pluginVersion)) {
				// config file exists and is up to date
				Log.info(pluginName + " config file found, loading config...");
				loadData();
			} else {
				// config file exists but is outdated
				Log.info(pluginName
						+ " config file outdated, adding old data and creating new values. "
						+ "Make sure you change those!");
				loadData();
				writeDefault();
			}
		} else {
			// config file does not exist
			try {
				Log.info(pluginName
						+ " config file not found, creating new config file...");
				configFile.createNewFile();
				writeDefault();
			} catch (IOException ioe) {
				Log.severe("Could not create the config file for " + pluginName + "!");
				ioe.printStackTrace();
				isErrorFree = false;
			}
		}

		return isErrorFree;
	}

	private void loadData() {
		penaltyMoney = bukkitConfig.getDouble("Penalty.Money", 0.0);
		targetMinMoney = bukkitConfig.getDouble("Penalty.TargetMinMoney", 0.0);
		penaltyMoneyInPercent = bukkitConfig.getDouble("Penalty.LosePercentage", 0.0);
		floorAfterSubtraction = bukkitConfig.getBoolean("Penalty.FloorAfterSubtraction", false);
		balanceCanBeNegative = bukkitConfig.getBoolean("Penalty.BalanceCanBeNegative", false);
		noPenaltyInWorldGuardRegions = bukkitConfig.getBoolean("Penalty.WorldGuard.noPenaltyInWorldGuardRegions", false);
		showMsgOnDeath = bukkitConfig.getBoolean("Penalty.Messages.ShowMsgOnDeath", true);
		msgNotEnoughMoney = bukkitConfig.getString("Penalty.Messages.NotEnoughMoney", "Lucky you! You didn't lose any money.");
		msgLostMoney = bukkitConfig.getString("Penalty.Messages.LostMoney", "You lost <Money> <Currency>!");
	}

	private void writeDefault() {
		bukkitConfig
				.setHeader("### Learn more about how this config can be edited and changed to your preference on the forum page. ###");
		write("Version", pluginVersion);
		write("Penalty.Money", penaltyMoney);
		write("Penalty.TargetMinMoney", targetMinMoney);
		write("Penalty.LosePercentage", penaltyMoneyInPercent);
		write("Penalty.FloorAfterSubtraction", floorAfterSubtraction);
		write("Penalty.BalanceCanBeNegative", balanceCanBeNegative);
		write("Penalty.WorldGuard.noPenaltyInWorldGuardRegions", noPenaltyInWorldGuardRegions);
		write("Penalty.Messages.ShowMsgOnDeath", showMsgOnDeath);
		write("Penalty.Messages.NotEnoughMoney", msgNotEnoughMoney);
		write("Penalty.Messages.LostMoney", msgLostMoney);

		loadData();
	}

	/**
	 * Reads a string representing a long from the config file.
	 * 
	 * Returns '0' when an exception occurs.
	 * 
	 * @param key
	 *            the key
	 * @param def
	 *            default value
	 * @return the long specified in 'key' from the config file, '0' on errors
	 */
	@SuppressWarnings("unused")
	private long readLong(String key, String def) {
		bukkitConfig.load();

		// Bukkit Config has no getLong(..)-method, so we are using Strings
		String value = bukkitConfig.getString(key, def);

		long tmp = 0;

		try {
			tmp = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			Log.warning("Error parsing a long from the config file. Key=" + key);
			nfe.printStackTrace();
		}

		return tmp;
	}

	private void write(String key, Object o) {
		bukkitConfig.load();
		bukkitConfig.setProperty(key, o);
		bukkitConfig.save();
	}

	/**
	 * Returns the amount of money subtracted on death.
	 * 
	 * @return the amount of money subtracted on death
	 */
	public double getPenaltyMoney() {
		return penaltyMoney;
	}
	
	/**
	 * Returns the amount of money a target must have before money will be subtracted.
	 * 
	 * @return the amount of money a target must have before money will be subtracted
	 */
	public double getTargetMinMoney() {
		return targetMinMoney;
	}
	
	/**
	 * Returns the percentage of the money that should be lost on death.
	 * 
	 * @return the percentage of the money that should be lost on death
	 */
	public double getPenaltyMoneyInPercent() {
		return penaltyMoneyInPercent;
	}
	
	/**
	 * Returns whether the player's balance should be floored after the penality money was subtracted.
	 * 
	 * @return true if player's balance should be floored after subtracting, otherwise false
	 */
	public boolean getFloorAfterSubtraction() {
		return floorAfterSubtraction;
	}
	
	/**
	 * Returns whether the player's balance can be negative after subtracting.
	 * 
	 * @return true if player's balance can be negative after subtracting, otherwise false
	 */
	public boolean getBalanceCanBeNegative() {
		return balanceCanBeNegative;
	}
	
	/**
	 * Returns whether a player may receive death penalty when in a WorldGuard region.
	 * 
	 * @return true if a player may receive death penalty when in a WorldGuard region, other false
	 */
	public boolean getNoPenaltyInWorldGuardRegions() {
		return noPenaltyInWorldGuardRegions;
	}
	
	/**
	 * Returns whether a message should be shown on death.
	 * 
	 * @return true if message should be shown, otherwise false
	 */
	public boolean getShowMsgOnDeath() {
		return showMsgOnDeath;
	}
	
	/**
	 * Returns the message that appears when a player does not have enough money.
	 * 
	 * @return the message that appears when a player does not have enough money
	 */
	public String getMsgNotEnoughMoney() {
		return msgNotEnoughMoney;
	}
	
	/**
	 * Returns the message that appears when a player dies and loses money.
	 * 
	 * @return the message that appears when a player dies and loses money
	 */
	public String getMsgLostMoney() {
		return msgLostMoney;
	}

	/**
	 * Returns a list containing all loaded keys.
	 * 
	 * @return a list containing all loaded keys
	 */
	public String[] printLoadedConfig() {
		bukkitConfig.load();

		String[] tmp = bukkitConfig.getAll().toString().split(",");
		int n = tmp.length;

		tmp[0] = tmp[0].substring(1);
		tmp[n - 1] = tmp[n - 1].substring(0, tmp[n - 1].length() - 1);

		for (String s : tmp) {
			s = s.trim();
		}

		return tmp;
	}

	/**
	 * Returns the config file.
	 * 
	 * @return the config file
	 */
	public File getConfigFile() {
		return configFile;
	}

	/**
	 * Returns the associated plugin.
	 * 
	 * @return the associated plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Replaces the first tag in a message.
	 * 
	 * @param message the message
	 * @param tag the tag to replace
	 * @param replacement the replacement string to replace the tag with
	 * @return a string with the first tag replaced
	 */
	public static String replaceTag(String message, String tag, String replacement) {
		if (message == null || tag == null || tag.equals("") || replacement == null) {
			return "";
		}
		
		String tmpMsg = message.toLowerCase();
		String tmpTag = "<" + tag.toLowerCase() + ">";
		
		if (!(tmpMsg.contains(tmpTag))) {
			return message;
		}
		
		int start = tmpMsg.indexOf(tmpTag);
		int end = tmpTag.length();
			
		String result = message.substring(0, start) + replacement + message.substring(start + end, message.length());
		
		return result;
	}
	
	/**
	 * Replaces all occurrences of the tag in the message.
	 * @param message the message
	 * @param tag the tag to replace
	 * @param replacement the replacement string to replace all tags with
	 * @return a string with all matching tags replaced
	 */
	public static String replaceTags(String message, String tag, String replacement) {
		if (message == null || tag == null || tag.equals("") || replacement == null) {
			return "";
		}
		
		String tmp = new String(message);
		
		for (int i = 0; i < amountOfTagsContained(message, tag); i++) {
			tmp = DPConfig.replaceTag(tmp, tag, replacement);
		}
		
		return tmp;
	}
	
	/**
	 * Counts the amount of tags in a string.
	 * 
	 * @param message the string
	 * @param tag the tag to check
	 * @return the amount of tags in the string
	 */
	public static int amountOfTagsContained(String message, String tag) {
		if (message == null || tag == null || tag.equals("")) {
			return 0;
		}
		
		String tmpMsg = message.toLowerCase();
		String tmpTag = "<" + tag.toLowerCase() + ">";
		boolean done = false;
		int i = 0;
		int counter = 0;
		
		while(!done) {
			tmpMsg = tmpMsg.substring(i);
			
			if (tmpMsg.contains(tmpTag)) {
				counter++;
				i = tmpMsg.indexOf(tmpTag) + tmpTag.length();
			} else {
				done = true;
			}
		}
		
		return counter;
	}
}
