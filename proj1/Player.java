//Thanirin Trironnarith 6088122 1A

public class Player {

	public enum PlayerType {Healer, Tank, Samurai, BlackMage, Phoenix, Cherry}

	private PlayerType type;    //Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix
	private double maxHP;        //Max HP of this player
	private double currentHP;    //Current HP of this player
	private double atk;            //Attack power of this player
	private int numSP;            //Number of Special turn
	private int curNumSP;        //Number of current SP

	///////////////////////////////////////Added code/////////////////////////////////////////////
	private boolean isSleeping;
	private boolean isCursed;
	private boolean isAlive;
	private boolean isTaunting;

	private int position;
	private Arena.Team team;
	private Arena.Row row;
	/////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
	 * as specified in the given table. It also reset the internal turn count of this player.
	 * @param _type
	 */
	public Player(PlayerType _type) {
		switch (_type) {
			case Healer:
				maxHP = 4790;
				atk = 238;
				numSP = 4;
				this.type = PlayerType.Healer;
				break;
			case Tank:
				maxHP = 5340;
				atk = 255;
				numSP = 4;
				this.type = PlayerType.Tank;
				break;
			case Samurai:
				maxHP = 4005;
				atk = 368;
				numSP = 3;
				this.type = PlayerType.Samurai;
				break;
			case BlackMage:
				maxHP = 4175;
				atk = 303;
				numSP = 4;
				this.type = PlayerType.BlackMage;
				break;
			case Phoenix:
				maxHP = 4175;
				atk = 209;
				numSP = 8;
				this.type = PlayerType.Phoenix;
				break;
			case Cherry:
				maxHP = 3560;
				atk = 198;
				numSP = 4;
				this.type = PlayerType.Cherry;
				break;
		}
		currentHP = maxHP;
		curNumSP = 0;
		isAlive = true;
		isCursed = false;
		isSleeping = false;
		isTaunting = false;

	}

	/**
	 * Returns the current HP of this player
	 * @return
	 */
	public double getCurrentHP() {
		return currentHP;
	}

	/**
	 * Returns type of this player
	 * @return
	 */
	public Player.PlayerType getType() {
		return type;
	}

	/**
	 * Returns max HP of this player.
	 * @return
	 */
	public double getMaxHP() {
		return maxHP;
	}

	/**
	 * Returns whether this player is sleeping.
	 * @return
	 */
	public boolean isSleeping() {
		return isSleeping;
	}

	/**
	 * Returns whether this player is being cursed.
	 * @return
	 */
	public boolean isCursed() {
		return isCursed;
	}

	/**
	 * Returns whether this player is alive (i.e. current HP > 0).
	 * @return
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Returns whether this player is taunting the other team.
	 * @return
	 */
	public boolean isTaunting() {
		return isTaunting;
	}

	/**
	 * Method used when a player is attacked
	 * Target is the attacked player
	 *
	 *
	 * @param target
	 */
	public void attack(Player target) {
		target.currentHP = target.currentHP - atk;
		if (target.currentHP <= 0) {
			target.currentHP = 0;
			target.isTaunting = false;
			target.isSleeping = false;
			target.isCursed = false;
			target.isAlive = false;
			target.curNumSP = 0;
		}
	}

	/**
	 * How each type use special ability
	 *
	 *
	 * @param arena
	 * @param theirTeam
	 */
	public void useSpecialAbility(Arena arena, Arena.Team theirTeam) {

		Player target = null;
		Player[][] teamThem = arena.findTeam(theirTeam);
		Player[][] teamUs = arena.findReverseTeam(theirTeam);

		switch (type) {
			case Healer:
				target = arena.hasLowestHealth(teamUs);
				if(target!=null && !target.isCursed) {
					target.currentHP = target.currentHP + (target.maxHP * 0.25);
					System.out.println(this.checkPrint() + " heals " + target.checkPrint());
					
					if (target.currentHP > target.maxHP) {
						target.currentHP = target.maxHP;
					}	
				} else if (target.isCursed) {
					System.out.println(this.checkPrint() + " didn't heal cursed " + target.checkPrint());
				}
				break;
			case Tank:
				isTaunting = true;
				System.out.println(this.checkPrint() + " is taunting.");
				break;
			case Samurai:
				target = arena.findTarger(teamThem);
				attack(target);
				attack(target);
				System.out.println(this.checkPrint() + " double slashes " + target.checkPrint());
				break;
			case BlackMage:
				target = arena.findTarger(teamThem);
				target.isCursed = true;
				System.out.println(this.checkPrint() + " curses " + target.checkPrint());
				break;
			case Phoenix:
				if (arena.isDeadest(teamUs) != null) {
					target = arena.isDeadest(teamUs);
					target.isAlive = true;
					target.currentHP = target.maxHP * 0.3;
					System.out.println(this.checkPrint() + " revives " + target.checkPrint());
				}
				break;
			case Cherry:
				for(int i=0; i<2; i++) {
					for(int j=0; j<arena.getNumRowPlayers(); j++) {
						if (teamThem[i][j].isAlive()) {
							teamThem[i][j].isSleeping = true;
							System.out.println(this.checkPrint() + " feeds fucking fortune cookie to " + teamThem[i][j].checkPrint());
						}
					}
				}
				break;
		}
	}

	/**
	 * This method is called by Arena when it is this player's turn to take an action.
	 * By default, the player simply just "attack(target)". However, once this player has
	 * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
	 * where each player type performs his own special move.
	 * @param arena
	 */
	public void takeAction(Arena arena) {

		/**
		 * Check if the player is taunting
		 * Lift curse
		 */
		if(this.type==PlayerType.Tank) this.isTaunting=false;
		if (this.type == PlayerType.BlackMage) {
			if (arena.isMemberOf(this, Arena.Team.A)) {
				arena.liftCurse(arena.findTeam(Arena.Team.B));
			}
			else arena.liftCurse(arena.findTeam(Arena.Team.A));
		}

		/**
		 * Find target and 1) Attack or 2) useSpecialAbility
		 */
		if (this.isAlive && !this.isSleeping) {
			Player target;
			Player[][] targetTeam;

			curNumSP++;

			if (arena.isMemberOf(this, Arena.Team.A)) {
				if (this.curNumSP == this.numSP) {
					useSpecialAbility(arena, Arena.Team.B);
					this.curNumSP = 0;
				} else {
					targetTeam = arena.findTeam(Arena.Team.B);
					target = arena.findTarger(targetTeam);
					attack(target);
					System.out.println(this.checkPrint() + " attacks " + target.checkPrint());
				}
			} else {
				if (this.curNumSP == this.numSP) {
					useSpecialAbility(arena, Arena.Team.A);
					this.curNumSP = 0;
				} else {
					targetTeam = arena.findTeam(Arena.Team.A);
					target = arena.findTarger(targetTeam);
					attack(target);
					System.out.println(this.checkPrint() + " attacks " + target.checkPrint());
				}
			}
		}
		/**
		 * Wake the sleeping beauty
		 */
		else if (this.isSleeping) {
			this.isSleeping = false;
		}
	}

	///////////////////////////////////////Added code/////////////////////////////////////////////
	/**
	 * Setter for setting curse status
	 * Used in Arena class
	 *
	 *
	 * @param cursed
	 */
	public void setCursed(boolean cursed) {
		isCursed = cursed;
	}

	public void setPos(Arena.Team team, Arena.Row row, int position) {
		this.team = team;
		this.row = row;
		this.position = position;
	}

	public String checkPrint() {
		return this.team+"["+this.row+"]["+this.position+"] {"+this.type+"}";
	}
	//////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method overrides the default Object's toString() and is already implemented for you.
	 */
	@Override
	public String toString ()
	{
		return "[" + this.type.toString() + " HP:" + this.currentHP + "/" + this.maxHP + " ATK:" + this.atk + "]["
				+ ((this.isCursed()) ? "C" : "")
				+ ((this.isTaunting()) ? "T" : "")
				+ ((this.isSleeping()) ? "S" : "")
				+ "]";
	}


	}

