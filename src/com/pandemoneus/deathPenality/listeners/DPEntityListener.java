package com.pandemoneus.deathPenality.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.iConomy.*;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;
import com.pandemoneus.deathPenality.DeathPenality;
import com.pandemoneus.deathPenality.config.DPConfig;

/**
 * Entity Listener for the DeathPenality plugin. Triggers on entity deaths.
 * 
 * @author Pandemoneus
 * 
 */
public final class DPEntityListener extends EntityListener {
	private DeathPenality plugin;
	
	/**
	 * Associates this object with a plugin.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public DPEntityListener(DeathPenality plugin) {
		this.plugin = plugin;
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
				DPConfig config = plugin.getConfig();
				Account account = iConomy.getAccount(playerName);
				Holdings balance = account.getHoldings();
				String msg = "";
				
				if (balance.hasEnough(config.getTargetMinMoney())) {
					// player has enough money
					double before = balance.balance();
					double percentage = config.getPenalityMoneyInPercent();
					
					if (before != 0.0 && percentage != 0.0 && percentage <= 100.0) {
						//make him lose a certain percentage of his money
						double after = before - (before * percentage / 100.00);
						
						if (config.getFloorAfterSubtraction()) {
							after = Math.floor(after);
						}
						
						balance.set(after);
					} else {
						//make him lose a fixed amount of his money
						double penalityMoney = config.getPenalityMoney();
						balance.subtract(penalityMoney);
						
						if (!config.getBalanceCanBeNegative()) {
							if (balance.isNegative()) {
								balance.set(0.0);
							}
						}
						
					}
					
					double dif = before - balance.balance();
					msg = ChatColor.RED + DPConfig.replaceTag(config.getMsgLostMoney(), "Money", "" + dif);
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
}
