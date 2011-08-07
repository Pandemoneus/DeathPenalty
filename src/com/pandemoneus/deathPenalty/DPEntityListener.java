package com.pandemoneus.deathPenalty;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.iConomy.*;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;
import com.nijiko.permissions.PermissionHandler;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;

import cosine.boseconomy.BOSEconomy;

/**
 * Entity Listener for the DeathPenalty plugin. Triggers on entity deaths.
 * 
 * @author Pandemoneus
 * 
 */
public final class DPEntityListener extends EntityListener {
	private DeathPenalty plugin;
	private static String pluginName;
	private DPConfig config;

	private boolean oneTimeCheck = false;
	private boolean iConomyFound = false;
	private boolean bosEconomyFound = false;
	private boolean worldGuardFound = false;
	private boolean permissionsFound = false;
	
	// HashMaps used to determine who killed a player and when
	private HashMap<Player, Long> recentlyAttackedPlayers = new HashMap<Player, Long>();
	private HashMap<Player, Boolean> lastAttackByOtherPlayer = new HashMap<Player, Boolean>();
	private HashMap<Player, Player> lastAttacker = new HashMap<Player, Player>();
	
	// maximum delay between a recorded player damage and this player's death
	private final static long MAX_DELAY = 3000;
	
	/**
	 * Associates this object with a plugin.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public DPEntityListener(DeathPenalty plugin) {
		this.plugin = plugin;
		pluginName = DeathPenalty.getPluginName();
		config = plugin.getConfig();
	}
	
	/**
	 * Recognizes player deaths.
	 * 
	 * @param event
	 *            event information passed by Bukkit
	 */
	public void onEntityDeath(EntityDeathEvent event) {
		if (!oneTimeCheck) {
			iConomyFound = plugin.getIConomyFound();
			bosEconomyFound = plugin.getBOSEconomyFound();
			worldGuardFound = plugin.getWorldGuardFound();
			permissionsFound = plugin.getPermissionsFound();
			
			oneTimeCheck = true;
		}
		
		if (event != null && (iConomyFound || bosEconomyFound)) {
			if (event.getEntity() instanceof Player) {
				final Player victim = (Player) event.getEntity();
				Player killer = null;
				
				if (recentlyAttackedPlayers.get(victim) - System.currentTimeMillis() <= MAX_DELAY && lastAttackByOtherPlayer.get(victim) != null && lastAttackByOtherPlayer.get(victim)) {
					killer = lastAttacker.get(victim);
				}
					
				if (permissionsFound) {
					final PermissionHandler ph = plugin.getPermissionsHandler();
					
					if (!ph.has(victim, pluginName.toLowerCase() + ".losemoney")) {
						// cancel if the player does not have the permission to lose money on death
						return;
					}
				}
				
				if (worldGuardFound) {
					if (config.getNoPenaltyInWorldGuardRegions()) {
						final LocalPlayer localPlayer = plugin.getWorldGuardPlugin().wrapPlayer(victim);
						final Vector v = localPlayer.getPosition();
						 
						final RegionManager regionManager = plugin.getWorldGuardPlugin().getRegionManager(victim.getWorld());
						final List<String> list = regionManager.getApplicableRegionsIDs(v);
						
						if (!list.isEmpty()) {
							// cancel all further actions if the player is in a WorldGuard region
							return;
						}
					}
				}
				
				if (iConomyFound) {
					useIConomy(victim, killer);
				} else if (bosEconomyFound) {
					useBOSEconomy(victim, killer);
				}
			}
		}		
	}
	
	/**
	 * Determines the last damage source of the victim.
	 * 
	 * Since onEntityDeath does not provide a getDamager() or getKiller() method, we have to
	 * determine it this way.
	 * 
	 * @param event event information passed by Bukkit
	 */
	public void onEntityDamage(EntityDamageEvent event) {
		if (event != null && !event.isCancelled() && (event instanceof EntityDamageByEntityEvent)) {
			final EntityDamageByEntityEvent temp = (EntityDamageByEntityEvent) event;
			
			if (!(temp.getEntity() instanceof Player)) {
				return;
			}
			
			final Player victim = (Player) temp.getEntity();
			
			recentlyAttackedPlayers.put(victim, System.currentTimeMillis());
			
			if (temp.getDamager() instanceof Player) {
				final Player killer = (Player) temp.getDamager();
				
				lastAttackByOtherPlayer.put(victim, true);
				lastAttacker.put(victim, killer);
			} else {
				lastAttackByOtherPlayer.put(victim, false);
			}
 		}
	}
	
	private String replaceAllTags(double dif) {		
		// determine currency name
		String currency = "";
		if (iConomyFound) {
			String five = iConomy.format(5.0);
			currency = five.substring(five.indexOf(" ") + 1);
		} else if (bosEconomyFound) {
			currency = plugin.getBOSEconomyPlugin().getMoneyNamePlural();
		}
		
		// replace tags one by one
		String tmp = config.getMsgLostMoney();
		tmp = DPConfig.replaceTags(tmp, "Money", "" + dif);
		tmp = DPConfig.replaceTags(tmp, "Percentage", "" + config.getPenaltyMoneyInPercent());
		tmp = DPConfig.replaceTags(tmp, "Currency", currency);
		
		return tmp;
	}
	
	private void useIConomy(Player victim, Player killer) {
		String playerName = victim.getName();
		
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
				double penaltyMoney = config.getPenaltyMoney();
				balance.subtract(penaltyMoney);
				
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
			
			if (config.getGiveMoneyToKiller() && killer != null && killer instanceof Player) {
				if (!killer.equals(victim)) {
					// determine currency name
					String currency = "";
					String five = iConomy.format(5.0);
					currency = five.substring(five.indexOf(" ") + 1);
						
					iConomy.getAccount(killer.getName()).getHoldings().add(dif);
					killer.sendMessage(ChatColor.GREEN + "You killed " + playerName + " and received " + dif + " " + currency);
				}
			}
			
			msg = ChatColor.RED + replaceAllTags(dif);
		} else {
			// player does not have enough money
			msg = ChatColor.GOLD + config.getMsgNotEnoughMoney();
		}
	
	
		// finally, show the message
		if (config.getShowMsgOnDeath()) {
			victim.sendMessage(ChatColor.RED + msg);
		}
	}
	
	private void useBOSEconomy(Player victim, Player killer) {
		String playerName = victim.getName();
		
		BOSEconomy bos = plugin.getBOSEconomyPlugin();
		int playerMoney = bos.getPlayerMoney(playerName);
		String msg = "";
		
		if (playerMoney >= config.getTargetMinMoney()) {
			// player has enough money
			double before = playerMoney;
			double percentage = config.getPenaltyMoneyInPercent();
			
			if (before != 0.0 && percentage != 0.0 && percentage <= 100.0) {
				//make him lose a certain percentage of his money
				double after = before - (before * percentage / 100.00);
				
				playerMoney = (int) after;
				
				bos.setPlayerMoney(playerName, playerMoney, false);
			} else {
				//make him lose a fixed amount of his money
				double penaltyMoney = config.getPenaltyMoney();
				playerMoney -= (int) penaltyMoney;
				
				if (!config.getBalanceCanBeNegative()) {
					if (playerMoney < 0) {
						playerMoney = 0;
					}
				}
				
				bos.setPlayerMoney(playerName, playerMoney, false);				
			}
			
			double dif = before - bos.getPlayerMoney(playerName);
			
			if (config.getGiveMoneyToKiller() && killer != null && killer instanceof Player) {
				if (!killer.equals(victim)) {
					bos.addPlayerMoney(killer.getName(), (int) dif, false);
					killer.sendMessage(ChatColor.GREEN + "You killed " + playerName + " and received " + dif + " " + bos.getMoneyNamePlural());
				}
			}
			
			msg = ChatColor.RED + replaceAllTags(dif);
		} else {
			// player does not have enough money
			msg = ChatColor.GOLD + config.getMsgNotEnoughMoney();
		}
	
	
		// finally, show the message
		if (config.getShowMsgOnDeath()) {
			victim.sendMessage(ChatColor.RED + msg);
		}
		
	}
}
