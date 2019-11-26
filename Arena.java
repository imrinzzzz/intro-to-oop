//Thanirin Trironnarith 6088122 1A

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Arena {

	public enum Row {Front, Back}		//enum for specifying the front or back row

	public enum Team {A, B}				//enum for specifying team A or B

	private Player[][] teamA = null;    //two dimensional array representing the players of Team A
	private Player[][] teamB = null;    //two dimensional array representing the players of Team B
	private int numRowPlayers = 0;      //number of players in each row

	public static final int MAXROUNDS = 100;    //Max number of turn
	public static final int MAXEACHTYPE = 3;    //Max number of players of each type, in each team.
	private final Path logFile = Paths.get("battle_log.txt");

	private int numRounds = 0;    //keep track of the number of rounds so far

	/**
	 * Constructor.
	 * @param _numRowPlayers is the number of players in each row.
	 */
	public Arena(int _numRowPlayers) {
		teamA = new Player[2][_numRowPlayers];
		teamB = new Player[2][_numRowPlayers];
		this.numRowPlayers = _numRowPlayers;

		try {
			Files.deleteIfExists(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Returns true if "player" is a member of "team", false otherwise.
	 * Assumption: team can be either Team.A or Team.B
	 * @param player
	 * @param team
	 * @return
	 */
	public boolean isMemberOf(Player player, Team team) {
		switch (team) {
			case A:
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < numRowPlayers; j++) {
						if (teamA[i][j].equals(player)) {
							return true;
						}
					}
				}
				break;
			case B:
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < numRowPlayers; j++) {
						if (teamB[i][j].equals(player)) {
							return true;
						}
					}
				}
				break;
		}
		return false;
	}


	/**
	 * This methods receives a player configuration (i.e., team, type, row, and position),
	 * creates a new player instance, and places him at the specified position.
	 * @param team is either Team.A or Team.B
	 * @param pType is one of the Player.Type  {Healer, Tank, Samurai, BlackMage, Phoenix}
	 * @param row	either Row.Front or Row.Back
	 * @param position is the position of the player in the row. Note that position starts from 1, 2, 3....
	 */
	public void addPlayer(Team team, Player.PlayerType pType, Row row, int position) {
		Player player = new Player(pType);
		player.setPos(team, row, position);
		switch (team) {
			case A:
				switch (row) {
					case Front:
						teamA[0][position - 1] = player;
						break;
					case Back:
						teamA[1][position - 1] = player;
						break;
				}
				break;
			case B:
				switch (row) {
					case Front:
						teamB[0][position - 1] = player;
						break;
					case Back:
						teamB[1][position - 1] = player;
						break;
				}
				break;
		}
	}


	/**
	 * Validate the players in both Team A and B. Returns true if all of the following conditions hold:
	 *
	 * 1. All the positions are filled. That is, there each team must have exactly numRow*numRowPlayers players.
	 * 2. There can be at most MAXEACHTYPE players of each type in each team. For example, if MAXEACHTYPE = 3
	 * then each team can have at most 3 Healers, 3 Tanks, 3 Samurais, 3 BlackMages, and 3 Phoenixes.
	 *
	 * Returns true if all the conditions above are satisfied, false otherwise.
	 * @return
	 */
	public boolean validatePlayers() {

		int countTeamA[] = new int[6];
		int countTeamB[] = new int[6];

		for(int i=0; i<6; i++) {
			countTeamA[i] = 0;
			countTeamB[i] = 0;
		}

		for(int i=0; i<2; i++) {
			for (int j = 0; j < numRowPlayers; j++) {
				if (teamA[i][j] == null) return false;
				switch (teamA[i][j].getType().ordinal()) {

					case 0:
						countTeamA[0]++;
						break;
					case 1:
						countTeamA[1]++;
						break;
					case 2:
						countTeamA[2]++;
						break;
					case 3:
						countTeamA[3]++;
						break;
					case 4:
						countTeamA[4]++;
						break;
					case 5:
						countTeamA[5]++;
						break;
				}
			}
		}

		for(int i=0; i<2; i++) {
			for (int j = 0; j < numRowPlayers; j++) {
				if (teamB[i][j] == null) return false;
				switch (teamB[i][j].getType().ordinal()) {

					case 0:
						countTeamB[0]++;
						break;
					case 1:
						countTeamB[1]++;
						break;
					case 2:
						countTeamB[2]++;
						break;
					case 3:
						countTeamB[3]++;
						break;
					case 4:
						countTeamB[4]++;
						break;
					case 5:
						countTeamB[5]++;
						break;
				}
			}
		}

		for(int i=0; i<6; i++) {
			if (countTeamA[i] > MAXEACHTYPE || countTeamB[i] > MAXEACHTYPE) {
				return false;
			}
		}

		return true;
	}


	/**
	 * Returns the sum of HP of all the players in the given "team"
	 * @param team
	 * @return
	 */
	public static double getSumHP(Player[][] team) {
        double sumHP = 0;

		for(int i=0; i<2; i++) {
            for (int j = 0; j <team[i].length; j++) {
                sumHP += team[i][j].getCurrentHP();
            }
        }
        return sumHP;
	}


	/**
	 * Return the team (either teamA or teamB) whose number of alive players is higher than the other.
	 *
	 * If the two teams have an equal number of alive players, then the team whose sum of HP of all the
	 * players is higher is returned.
	 *
	 * If the sums of HP of all the players of both teams are equal, return teamA.
	 * @return
	 */
	public Player[][] getWinningTeam() {
        if (countAlive(teamB) > countAlive(teamA)) {
            return teamB;
        } else if (countAlive(teamA) > countAlive(teamB)) {
            return teamA;
        } else if (countAlive(teamB) == countAlive(teamA)) {
            if (getSumHP(teamA) > getSumHP(teamB)) {
                return teamA;
            } else if (getSumHP(teamB) > getSumHP(teamA)) {
                return teamB;
            }
        }
        return teamA;
	}


	/**
	 * This method simulates the battle between teamA and teamB. The method should have a loop that signifies
	 * a round of the battle. In each round, each player in teamA invokes the method takeAction(). The players'
	 * turns are ordered by its position in the team. Once all the players in teamA have invoked takeAction(),
	 * not it is teamB's turn to do the same.
	 *
	 * The battle terminates if one of the following two conditions is met:
	 *
	 * 1. All the players in a team has been eliminated.
	 * 2. The number of rounds exceeds MAXROUNDS
	 *
	 * After the battle terminates, report the winning team, which is determined by getWinningTeam().
	 */
	public void startBattle() {

		while (this.numRounds <= MAXROUNDS) {

			this.numRounds++;

			//TeamA's turn
			System.out.println("Round " + this.numRounds);
			for(int i=0;i<2;i++) {
				for(int j=0;j<this.numRowPlayers;j++) {
					if(areTheyAlive(teamB))
						teamA[i][j].takeAction(this);
					else break;
				}
			}
			//TeamB's turn
			for(int i=0;i<2;i++) {
				for(int j=0;j<this.numRowPlayers;j++) {
					if(areTheyAlive(teamA))
						teamB[i][j].takeAction(this);
					else break;
				}
			}
			System.out.println();
			displayArea(this, true);
			logAfterEachRound();
			
			if (!areTheyAlive(teamA) || !areTheyAlive(teamB)) {
				System.out.println("The winning team is team " + teamNameString(getWinningTeam()));
				break;
			}
		}
        
	}


	/**
	 * This method displays the current area state, and is already implemented for you.
	 * In startBattle(), you should call this method once before the battle starts, and
	 * after each round ends.
	 *
	 * @param arena
	 * @param verbose
	 */
    public static void displayArea(Arena arena, boolean verbose) {
		StringBuilder str = new StringBuilder();
		if (verbose) {
			str.append(String.format("%43s   %40s", "Team A", "") + "\t\t" + String.format("%-38s%-40s", "", "Team B") + "\n");
			str.append(String.format("%43s", "BACK ROW") + String.format("%43s", "FRONT ROW") + "  |  " + String.format("%-43s", "FRONT ROW") + "\t" + String.format("%-43s", "BACK ROW") + "\n");
			for (int i = 0; i < arena.numRowPlayers; i++) {
				str.append(String.format("%43s", arena.teamA[1][i]) + String.format("%43s", arena.teamA[0][i]) + "  |  " + String.format("%-43s", arena.teamB[0][i]) + String.format("%-43s", arena.teamB[1][i]) + "\n");
			}
		}

		str.append("@ Total HP of Team A = " + getSumHP(arena.teamA) + ". @ Total HP of Team B = " + getSumHP(arena.teamB) + "\n\n");
		System.out.print(str.toString());


	}


	/**
	 * This method writes a log (as round number, sum of HP of teamA, and sum of HP of teamB) into the log file.
	 * You are not to modify this method, however, this method must be call by startBattle() after each round.
	 *
	 * The output file will be tested against the auto-grader, so make sure the output look something like:
	 *
	 * 1	47415.0	49923.0
	 * 2	44977.0	46990.0
	 * 3	42092.0	43525.0
	 * 4	44408.0	43210.0
	 *
	 * Where the numbers of the first, second, and third columns specify round numbers, sum of HP of teamA, and sum of HP of teamB respectively.
	 */
	private void logAfterEachRound() {
		try {
			Files.write(logFile, Arrays.asList(new String[]{numRounds + "\t" + getSumHP(teamA) + "\t" + getSumHP(teamB)}), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	////////////////////////////////My code///////////////////////////////////////

	/**
	*get team either A or B and return the whole team (Player[][] type) back
	*/
	public Player[][] findTeam(Team team) {
		switch (team) {
			case A:
				return teamA;
			case B:
				return teamB;
		}
		return null;
	}

	/**
	 * work similarly to findTeam but return reverse team
	 * e.g. pass team A and return Player[][] teamB
	 *
	 * @param team
	 * @return Player[][]
	 */
	public Player[][] findReverseTeam(Team team) {
		switch (team) {
			case A:
				return teamB;
			case B:
				return teamA;
		}
		return null;
	}

	/**
	 * Find target player to attack or curse
	 * Priority search : taunting player (tank)
	 *
	 * Search front row only if there are front row players left
	 * Find lowest HP player
	 *
	 * If all front row died, then search in the second row
	 * Find the lowest HP player
	 *
	 * @param team
	 * @return target
	 */
	public Player findTarger(Player[][] team) {

		Player target = null;
		
		for(int i=0; i<2; i++) {
			for(int j=0; j<numRowPlayers; j++) {
				if(team[i][j].isTaunting() && team[i][j].isAlive()) {
					return target = team[i][j];
				}
			}
		}
		
		if (isFrontAlive(team)) {
			for (int i = 0; i < numRowPlayers; i++) {
				if (team[0][i].isAlive()) {
					target = team[0][i];
					for (int j = i; j < numRowPlayers; j++) {
						if (team[0][j].getCurrentHP() < target.getCurrentHP() && team[0][j].isAlive()) {
							target = team[0][j];
						}
					}
					break;
				}
			}
		} else {
			for (int i = 0; i < numRowPlayers; i++) {
				if (team[1][i].isAlive()) {
					target = team[1][i];
					for (int j = i; j < numRowPlayers; j++) {
						if (team[1][j].getCurrentHP() < target.getCurrentHP() && team[1][j].isAlive()) {
							target = team[1][j];
						}
					}
					break;
				}
			}
		}
		return target;
	}

	/**
	 * check if there are players left in the front row
	 *
	 * @param team
	 * @return
	 */
	public boolean isFrontAlive(Player[][] team) {
		for (int i = 0; i < numRowPlayers; i++) {
			if (team[0][i].isAlive()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if there are players left at all
	 *
	 * @param team
	 * @return
	 */
	public boolean areTheyAlive(Player[][] team) {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < this.numRowPlayers; j++) {
				if (team[i][j].isAlive()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Find lowest HP player
	 * Is used in Healer's Special Ability to find the ally's lowest HP
	 *
	 *
	 * @param team
	 * @return minHealth
	 */
	public Player hasLowestHealth(Player[][] team) {

        double minHP = 0;
        Player minHealth = null;

        if (areTheyAlive(team)) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < this.numRowPlayers; j++) {
                    if (team[i][j].isAlive()) {
                        minHP = team[i][j].getCurrentHP();
                        minHealth = team[i][j];
                        break;
                    }
                }
                if (minHP != 0) {
                    break;
                }
            }
            
            for(int j=0; j<2; j++) {
                for(int i=0; i<this.numRowPlayers; i++) {
                    if (minHP > team[j][i].getCurrentHP() && team[j][i].isAlive() && team[j][i].getCurrentHP()!=team[j][i].getMaxHP()) {
                        minHP = team[j][i].getCurrentHP();
						minHealth = team[j][i];
                    }
                }
            }
            return minHealth;
        }

        return null;
	}

	/**
	 * Find the first dead player
	 * Is used in Phoenix's Special Ability
	 *
	 *
	 * @param team
	 * @return
	 */
    public Player isDeadest(Player[][] team) {
	    for(int i=0; i<2; i++) {
	        for(int j=0; j<this.numRowPlayers; j++) {
                if (!team[i][j].isAlive()) {
                    return team[i][j];
                }
            }
        }
        return null;
    }

	/**
	 * Count the alive players
	 * Used in isWinningTeam to compare which team has more alive players
	 *
	 *
	 * @param team
	 * @return
	 */
	public int countAlive(Player[][] team) {
        int count = 0;
	    for(int i=0; i<2; i++) {
	        for(int j=0; j<this.numRowPlayers; j++) {
                if (team[i][j].isAlive()) {
                    count++;
                }
            }
        }
        return count;
    }

	/**
	 * Return String of team's name
	 * Basically pass the team
	 * And it will return that team's name in String back
	 *
	 *
	 * @param team
	 * @return
	 */
	public String teamNameString(Player[][] team) {
        if (isMemberOf(team[0][0], Team.A)) {
            return "Team A";
        } else return "Team B";
    }

	/**
	 * To cancel the curse done on anyone once the black mage turn comes
	 *
	 *
	 * @param team
	 */
	public void liftCurse(Player[][] team) {
		boolean checkout = false;
		for(int i=0; i<2; i++) {
			for(int j=0; j<numRowPlayers; j++) {
				if (team[i][j].isCursed()) {
					team[i][j].setCursed(false);
					checkout = true;
					break;
				}
			}
			if (checkout) {
				break;
			}
		}
	}

	/**
	 * Return numRowPlayers
	 * Is used in Player class
	 *
	 *
	 * @return
	 */
	public int getNumRowPlayers() {
		return numRowPlayers;
	}
}
