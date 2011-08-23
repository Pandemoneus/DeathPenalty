package com.pandemoneus.deathPenalty;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

/**
 * Command class. Available commands are:
 * dp
 * dp reload
 * dp info
 * 
 * @author Pandemoneus
 * 
 */
public final class DPCommands implements CommandExecutor {

	private DeathPenalty plugin;
	private static String pluginName;
	
	private boolean permissionsFound = false;
	private PermissionHandler ph = null;

	/**
	 * Associates this object with a plugin
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public DPCommands(DeathPenalty plugin) {
		this.plugin = plugin;
		pluginName = DeathPenalty.getPluginName();
		
		permissionsFound = plugin.getPermissionsFound();
		ph = plugin.getPermissionsHandler();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (args != null) {
			if (sender instanceof Player) {
				determineCommand((Player) sender, cmd, commandLabel, args);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "Sorry, you are not a player!");
			}
		}

		return true;
	}

	private void determineCommand(Player sender, Command cmd,
			String commandLabel, String[] args) {
		if (args.length == 0) {
			// show help
			if (hasPerm(sender, ".help")) {
				showHelp(sender);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You are not authorized to use this command.");
			}
		} else if (args.length == 1) {
			// commands with 0 arguments
			String command = args[0];

			if (command.equalsIgnoreCase("reload")) {
				// reload
				if (hasPerm(sender, ".config.reload")) {
					reloadPlugin(sender);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You are not authorized to use this command.");
				}
			} else if (command.equalsIgnoreCase("info")) {
				// info
				if (hasPerm(sender, ".config.info")) {
					getConfigInfo(sender);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You are not authorized to use this command.");
				}
			}
		}
	}
	
	private boolean hasPerm(Player sender, String perm) {
		return (permissionsFound && ph.has(sender, pluginName.toLowerCase() + perm)) || (sender.hasPermission(pluginName.toLowerCase() + perm));

	}

	private void showHelp(Player sender) {
		sender.sendMessage(ChatColor.YELLOW + "Available commands:");
		sender.sendMessage(ChatColor.GOLD
				+ "/dp reload - reloads the plugin's config file");
		sender.sendMessage(ChatColor.GOLD
				+ "/dp info - shows the currently loaded config");
	}

	private void reloadPlugin(Player sender) {
		Log.info("'" + sender.getName()
				+ "' requested reload of " + pluginName);
		sender.sendMessage(ChatColor.GREEN + "Reloading " + pluginName);

		if (plugin.reload()) {
			sender.sendMessage(ChatColor.GREEN + "Success!");
		}
	}

	private void getConfigInfo(Player sender) {
		DPConfig config = plugin.getConfig();
		sender.sendMessage(ChatColor.YELLOW
				+ "Currently loaded config of " + pluginName + ":");
		sender.sendMessage(ChatColor.YELLOW
				+ "---------------------------------------------");

		if (config.getConfigFile().exists()) {
			for (String s : config.printLoadedConfig()) {
				sender.sendMessage(ChatColor.YELLOW + s);
			}
		} else {
			sender.sendMessage(ChatColor.RED
					+ "None - Config file deleted - please reload");
		}
	}
}
