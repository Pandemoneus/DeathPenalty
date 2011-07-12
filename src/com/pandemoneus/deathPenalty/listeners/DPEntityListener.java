package com.pandemoneus.deathPenalty.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.iConomy.*;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;
import com.pandemoneus.deathPenalty.DeathPenalty;
import com.pandemoneus.deathPenalty.config.DPConfig;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;

/**
 * Entity Listener for the DeathPenalty plugin. Triggers on entity deaths.
 * 
 * @author Pandemoneus
 * 
 */
public final class DPEntityListener extends EntityListener {
	private DeathPenalty plugin;
	private DPConfig config;
	
	/**
	 * Associates this object with a plugin.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public DPEntityListener(DeathPenalty plugin) {
		this.plugin = plugin;
		config = plugin.getConfig();
	}
	
	/**
	 * Recognizes player deaths.
	 * 
	 * @param event
	 *            event information passed by Bukkit
	 */
	public void onEntityDeath(EntityDeathEvent event) {
		if (event != null && plugin.iConomyFound) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				String playerName = player.getName();
				
				if (plugin.getWorldGuardFound()) {
					if (config.getNoPenaltyInWorldGuardRegions()) {
						LocalPlayer localPlayer = plugin.getWorldGuardPlugin().wrapPlayer(player);
						Vector v = localPlayer.getPosition();
						 
						RegionManager regionManager = plugin.getWorldGuardPlugin().getRegionManager(player.getWorld());
						List<String> list = regionManager.getApplicableRegionsIDs(v);
						
						if (!list.isEmpty()) {
							return;
						}
					}
				}
				
				Account account = iConomy.getAccount(playerName);
				Holdings balance = account.getHoldings();
				String msg = "";
				
				if (balance.hasEnough(config.getTargetMinMoney())) {
					// player has enough money
					double before = balance.balance();
					double percentage = config.getPenaltyMoneyInPercent();
					
					if (before != 0.0 && percentage != 0.0 && percentage <= 100.0) {
						//make him lose a certain percentage of his money
						double after = before - (before * percentage / 100.00);
						
						balance.set(after);
					} else {
						//make him lose a fixed amount of his money
						double penalityMoney = config.getPenaltyMoney();
						balance.subtract(penalityMoney);
						
						if (!config.getBalanceCanBeNegative()) {
							if (balance.isNegative()) {
								balance.set(0.0);
							}
						}
						
					}
					
					if (config.getFloorAfterSubtraction()) {
						balance.set(Math.floor(balance.balance()));
					}
					
					double dif = before - balance.balance();
					msg = ChatColor.RED + replaceAllTags(dif);
				} else {
					// player does not have enough money
					msg = ChatColor.GOLD + config.getMsgNotEnoughMoney();
				}
				
				// finally, show the message
				if (config.getShowMsgOnDeath()) {
					player.sendMessage(ChatColor.RED + msg);
				}
			}
		}
	}
	
	private String replaceAllTags(double dif) {		
		// determine currency name
		String five = iConomy.format(5.0);
		String currency = five.substring(five.indexOf(" ") + 1);
		
		// replace tags one by one
		String tmp = config.getMsgLostMoney();
		tmp = DPConfig.replaceTags(tmp, "Money", "" + dif);
		tmp = DPConfig.replaceTags(tmp, "Percentage", "" + config.getPenaltyMoneyInPercent());
		tmp = DPConfig.replaceTags(tmp, "Currency", currency);
		
		return tmp;
	}
}
