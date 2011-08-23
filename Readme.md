DeathPenalty plugin v1.07		
by Pandemoneus		
https://github.com/Pandemoneus

Requirements:
----------------
- iConomy 5.0 OR BOSEconomy 6.2
- Permissions 3.x (optional)
- WorldGuard 5.2.3 (optional)

How to install:
----------------
1. Copy 'DeathPenalty.jar' into your 'plugins/' folder.		
2. Start your server to create a config file.		
3. Edit the config file in 'plugins/DeathPenalty/config.yml'.

How to uninstall:
-----------------
1. Delete 'DeathPenalty.jar'.		
2. Delete the folder 'plugins/DeathPenalty'.

Editable options:
-----------------
Money: [amount] - money to lose on death		
TargetMinMoney: [amount] - money a player must have at least to lose money at all		
LosePercentage: [amount] - percentage of the money the player should lose when he dies -- NOTE: this will be used instead of "Money" if not 0.0		
BalanceCanBeNegative: [true, false] - determines whether the player's money can be negative after subtracting		
FloorAfterSubtraction: [true, false] - determines whether the player's money should be floored (round down) after subtracting (iConomy only)		
NoPenaltyInWorldGuardRegions: [true, false] - determines whether players receive penalty in guarded regions		
ShowMsgOnDeath: [true, false] - determines whether a message is shown on the player's death		
NotEnoughMoney: [message] - shows this message when a player dies but didn't have enough money		
LostMoney: [message] - shows this message when a player dies and when he had enough money (Useable tags: '<'Money'>' = displays the money lost, '<'Currency'>' = displays the name of the currency, '<'Percentage'>' = displays the percentage as given in LosePercentage, '<'Victim'>' = displays the victim's name, '<'Killer'>' = displays the killer's name (when the player was killed by another player, otherwise it will be blank) | do not forget to remove the '' !)		
GiveMoneyToKiller: [true, false] - determines whether the player who killed the victim should receive the money lost by the victim

Commands:
-----------------
deathpenalty (alias: dp) - shows the ingame help		
deathpenalty reload (alias: dp reload) - reloads the plugin ingame		
deathpenalty info (alias: dp info) - shows the currently loaded config


Permission nodes:
-----------------
deathpenalty.losemoney //players with this permission lose money on death		
deathpenalty.help //makes the help command available		
deathpenalty.config.reload //makes the reload command available		
deathpenalty.config.info //makes the info command available

PermissionBukkit nodes:
-----------------------
`deathpenalty.admin:`		
    description: Quick node for administrators, disables money loss and enables configuration nodes		
`deathpenalty.player:`		
    description: Quick node for players, enables money loss and disables configuration nodes and help	
`deathpenalty.*:`		
    description: Gives access to everything that the plug-in offers		
`deathpenalty.config.*:`		
    description: Gives access to configuration reloading and displaying it in-game		
`deathpenalty.losemoney:`		
    description: Players with this permission lose money on death		
    default: not op		
`deathpenalty.help:`		
    description: Allows you to display the commands in-game		
    default: op		
`deathpenalty.config.info:`		
    description: Displays the currently loaded configuration		
    default: op		
`deathpenalty.config.reload:`		
    description: Reloads the configuration in-game		
    default: op		
