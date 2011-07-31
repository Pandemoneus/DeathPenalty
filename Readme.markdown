DeathPenalty plugin v1.052<br>
by Pandemoneus<br>
https://github.com/Pandemoneus

Requirements:
----------------
- iConomy 5.0 OR BOSEconomy 6.2
- Permissions 3.x (optional)
- WorldGuard 5.2.3 (optional)

How to install:
----------------
1. Copy 'DeathPenalty.jar' into your 'plugins/' folder.<br>
2. Start your server to create a config file.<br>
3. Edit the config file in 'plugins/DeathPenalty/config.yml'.

How to uninstall:
-----------------
1. Delete 'DeathPenalty.jar'.<br>
2. Delete the folder 'plugins/DeathPenalty'.

Editable options:
-----------------
Money: [amount] - money to lose on death<br>
TargetMinMoney: [amount] - money a player must have at least to lose money at all<br>
LosePercentage: [amount] - percentage of the money the player should lose when he dies -- NOTE: this will be used instead of "Money" if not 0.0<br>
BalanceCanBeNegative: [true, false] - determines whether the player's money can be negative after subtracting<br>
FloorAfterSubtraction: [true, false] - determines whether the player's money should be floored (round down) after subtracting (iConomy only)<br>
NoPenaltyInWorldGuardRegions: [true, false] - determines whether players receive penalty in guarded regions<br>
ShowMsgOnDeath: [true, false] - determines whether a message is shown on the player's death<br>
NotEnoughMoney: [message] - shows this message when a player dies but didn't have enough money<br>
LostMoney: [message] - shows this message when a player dies and when he had enough money (Useable tags: '<'Money'>' = displays the money lost, '<'Currency'>' = displays the name of the currency, '<'Percentage'>' = displays the percentage as given in LosePercentage, | do not forget to remove the '' !)<br>
GiveMoneyToKiller: [true, false] - determines whether the player who killed the victim should receive the money lost by the victim

Commands:
-----------------
deathpenalty (alias: dp) - shows the ingame help<br>
deathpenalty reload (alias: dp reload) - reloads the plugin ingame<br>
deathpenalty info (alias: dp info) - shows the currently loaded config


Permission nodes:
-----------------
deathpenalty.losemoney //players with this permission lose money on death<br>
deathpenalty.help //makes the help command available<br>
deathpenalty.config.reload //makes the reload command available<br>
deathpenalty.config.info //makes the info command available