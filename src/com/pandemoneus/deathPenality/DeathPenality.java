package com.pandemoneus.deathPenality;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.pandemoneus.deathPenality.commands.DPCommands;
import com.pandemoneus.deathPenality.config.DPConfig;
import com.pandemoneus.deathPenality.listeners.DPEntityListener;
import com.pandemoneus.deathPenality.logger.Log;
import com.iConomy.*;

/**
 * DeathPenality plugin.
 * 
 * Deductes money from a player when he dies.
 * 
 * @author Pandemoneus
 * 
 */
public class DeathPenality extends JavaPlugin {
	/**
	 * Plugin related stuff
	 */
	private final DPCommands cmdExecutor = new DPCommands(this);
	private DPConfig config = new DPConfig(this);
	private final DPEntityListener entityListener = new DPEntityListener(this);
	
	private PermissionHandler permissionsHandler = null;
	private boolean permissionsFound = false;
	
	public iConomy iConomy = null;
	public boolean iConomyFound = false;

	private static String version;
	private static final String PLUGIN_NAME = "DeathPenality";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDisable() {
		Log.info(PLUGIN_NAME + " disabled");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		version = pdfFile.getVersion();
		
		Log.info(PLUGIN_NAME + " v" + version + " enabled");
		
		setupIconomy();	
		setupPermissions();
		
		getCommand("deathpenality").setExecutor(cmdExecutor);
		getCommand("dp").setExecutor(cmdExecutor);
		config.loadConfig();

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Highest, this);
	}

	/**
	 * Returns the version of the plugin.
	 * 
	 * @return the version of the plugin
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * Returns the name of the plugin.
	 * 
	 * @return the name of the plugin
	 */
	public static String getPluginName() {
		return PLUGIN_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getPluginName();
	}

	/**
	 * Returns whether the permissions plugin could be found.
	 * 
	 * @return true if permissions plugin could be found, otherwise false
	 */
	public boolean getPermissionsFound() {
		return permissionsFound;
	}
	
	/**
	 * Returns whether the iConomy plugin could be found.
	 * 
	 * @return true if iConomy plugin could be found, otherwise false
	 */
	public boolean getIConomyFound() {
		return iConomyFound;
	}

	/**
	 * Returns the permissionsHandler of this plugin if it exists.
	 * 
	 * @return the permissionsHandler of this plugin if it exists, otherwise
	 *         null
	 */
	public PermissionHandler getPermissionsHandler() {
		PermissionHandler ph = null;

		if (getPermissionsFound()) {
			ph = permissionsHandler;
		}

		return ph;
	}

	private void setupPermissions() {
		if (permissionsHandler != null) {
			return;
		}

		Plugin permissionsPlugin = getServer().getPluginManager().getPlugin(
				"Permissions");

		if (permissionsPlugin == null) {
			Log.warning("Permissions not detected, using normal command structure.");
			return;
		}

		permissionsFound = true;
		permissionsHandler = ((Permissions) permissionsPlugin).getHandler();
	}
	
	private void setupIconomy() {
		getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, new Server(this), Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, new Server(this), Priority.Monitor, this);		
	}
	
	/**
	 * Method that handles what gets reloaded
	 * 
	 * @return true if everything loaded properly, otherwise false
	 */
	public boolean reload() {
		boolean success = config.loadConfig();
		
		return success;
	}
	
	/**
	 * Returns the plugin's config object.
	 * 
	 * @return the plugin's config object
	 */
	public DPConfig getConfig() {
		return config;
	}
}
