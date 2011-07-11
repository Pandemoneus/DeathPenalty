DeathPenality plugin v1.00<br>
by Pandemoneus<br>
https://github.com/Pandemoneus

Requirements:
----------------
- iConomy 5.0
- Permissions 3.x (optional)

How to install:
----------------
1. Copy 'DeathPenality.jar' into your 'plugins/' folder.<br>
2. Start your server to create a config file.<br>
3. Edit the config file in 'plugins/DeathPenality/config.yml'.

How to uninstall:
-----------------
1. Delete 'DeathPenality.jar'.<br>
2. Delete the folder 'plugins/DeathPenality'.

Editable options:
-----------------
Money: [amount] - money to lose on death<br>
TargetMinMoney: [amount] - money a player must have at least to lose money at all<br>
BalanceCanBeNegative: [true, false] - determines whether the player's money can be negative after subtracting<br>
ShowMsgOnDeath: [true, false] - determines whether a message is shown on the player's death<br>
NotEnoughMoney: [message] - shows this message when a player dies but didn't have enough money<br>
LostMoney: [message] - shows this message when a player dies and when he had enough money -- USE <Money> TO INSERT THE AMOUNT LOST

Commands:
-----------------
deathpenality (alias: dp) - shows the ingame help<br>
deathpenality reload (alias: dp reload) - reloads the plugin ingame<br>
deathpenality info (alias: dp info) - shows the currently loaded config


Permission nodes:
-----------------
deathpenality.help //makes the help command available<br>
deathpenality.config.reload //makes the reload command available<br>
deathpenality.config.info //makes the info command available