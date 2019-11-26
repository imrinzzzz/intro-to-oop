# Project 1: Final FiCT

## Introduction

In this project, we were implementing a program that simulates the gameplay of a turn-based twoteam battle game, Final FiCT. The skeleton java files were provided. Our task was to understand the
gameplay, and implement the methods in the provided skeleton java files which are currently left blank
for us to fill in the missing code.

## Final FiCT

Final FiCT is a simple turn-based battle game between two teams: TeamA and TeamB. Similar examples
include the battle scenes of Final Fantasy I, II, III, IV, V, VI, VII, VIII, IX, and X, Summoners War, and
Fate/Grand Order. However, in this programming project, you will only implement a simple,
deterministic mechanism of the game.

### Team Alignment

Players of each team are aligned in two rows: _**Front**_ and _**Back**_, where the front rows of both teams face
each other. The number of players in each row should be specifiable by users. For example, if there are 5
players in each row, then the arena could be depicted as:

![team-arrangement](https://raw.githubusercontent.com/imrinzzzz/intro-to-oop/master/proj1/team-arrangement.png)

The game must be playable as long as each team has more than one player. In a team, each player has a
position number (1 – 10 in the above figure). The position numbers start from top to bottom, and front
row to the back row. These position numbers will be used to determine the order of players when
attacking and receiving beneficial effects (i.e. healing and reviving, see next section). Typically, the front
row players will be eliminated first before moving to the next row. Exceptions to this rule include when a
player is taunting, which makes all the players in the opposite team attack just himself.

*Tip: Hence this is probably why positioning tanks (and other high HP players) in the front row is a good
idea!*

### The players

A player can be one of the following type: `Healer, Tank, Samurai, BlackMage, Phoenix, and
Cherry`. Each player can perform two actions: `attack()` and `useSpecialAbility()`. Each
player type has a different set of MAX HP (health points), ATK (attack power), # of Special Turns and
special ability. Listed below is the information of each player type:

|Type|Max HP and ATK| Special Ability |
|:---:|:---:|:---:|
| Healer | Max HP: 4790 <br> ATK: 238 <br> # of Special Turns: 4 | **Heal** <br> Increase HP of one alive ally with the lowest percentage HP by 25% of his MAX HP. If multiple allies have equal lowest percentage HP, heal the first one according to the position order. The HP of the healed ally cannot exceed his MAX HP. |
| Tank | Max HP: 5340 <br> ATK: 255 <br> # of Special Turns: 4 | **Taunt** <br> Make the players of the opposite team attack (including double-slashing and cursing) himself for one turn1. If there are multiple taunting player, the first taunting player according to the position order gets attacked first. |
| Samurai | Max HP: 4005 <br> ATK: 368 <br> # of Special Turns: 3 | **Double-Slash** <br> Attack the same target twice (even if it is dead after the first attack). |
| Black Mage | Max HP: 4175 <br> ATK: 303 <br> # of Special Turns: 4 | **Curse** <br> Curse an alive player with the lowest HP in the opposite team in the frontest row. A cursed player does not receive any beneficial effects from the healing abilities for one internal turn **of the curser**. That is the curse status remains until just before it is the **curser’s** turn again (even if the curser is already dead). If a cursed player gets cursed again, the most recent curse takes over the previous curse (hence, the internal clock restarts upon receiving the new curse).  |
| Phoenix | Max HP: 4175 <br> ATK: 209 <br> # of Special Turns: 8 | **Revive** <br> Revive a dead ally and increase the HP by 30% of his Max HP. If there are multiple dead allies, revive the first one according to the position order. If all the allies are still alive, do nothing. Except for his partial 30% HP, the revived player resumes the battle with a clean slate (i.e. with internal turns and statuses reset). Revived player can resume the battle right away if his turn comes.|
| Cherry | Max HP: 3560 <br> ATK: 198 <br> # of Special Turns: 4 | **Fortune-Cookies** <br> Lure each of the alive players in the opposite team to eat a sleeping-drug covered fortune cookie, causing them to fall asleep and yield their next turns. That is, the current team gets to take actions for the next turn. While a player is sleeping, its internal clock stops counting (i.e. number of Special Turns freezes). Eating more than one fortune-cookie in one turn yields the same effect as eating just one. |

In each team, all the positions must be filled. Furthermore, the number of players with the same type
must not exceed `MAXEACHTYPE` (defined in Arena.java).

### Gameplay

The battle is simulated in iterations (or rounds). For each round, each of the alive players (ordered by
position numbers) in Team A takes an action. By default, an action is an attack, where the HP of the alive
target with lowest HP on the frontmost row of the opposite team is deducted by the attacker’s ATK. If a
player’s HP is reduced to 0, the player is dead, in which case the HP remains 0. Once a player is dead, his
curse and taunting statuses (if any) are reset. A dead player cannot perform any action even when his
turn comes.

At every “number of Special Turns,” the player must perform his special ability. For example, the
Samurai’s # of Special Turns is 3. Hence, he would perform normal attacks in the first two turns, and
double-slash on the third turn.

Once all the players in Team A finish performing actions, Team B then proceeds with the same manner.
A round ends when all the alive players finish performing their actions.
The battle is concluded after a round ends AND either one of the following conditions becomes true:

1. All players of a team are dead.
2. The number of simulation rounds exceeds MAXROUNDS (defined in Arena.java).

After the battle terminates, the winning team is the team with the greater number of alive players. If
there are equal numbers of alive players, the team with higher sum of HP wins. Otherwise, Team A wins.
