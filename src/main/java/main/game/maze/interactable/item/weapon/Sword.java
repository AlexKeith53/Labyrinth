package main.game.maze.interactable.item.weapon;

import main.game.Config;
import main.game.maze.interactable.item.weapon.weaponType.MeleeWeapon;
import main.game.maze.mechanics.stats.ItemStats;

public class Sword extends Weapon{
	private static final int DELAY = 600;
	private static final int DAMAGE = 20;
	private static final int RANGE = 10;
	private static final String IMAGE_SRC = Config.IMAGES_FOLDER_ITEMS + "weapon_sword.png";
	private static final String DESCRIPTION = "Sticks and stones may break your bones, but sword will behead you.";
	private static final ItemStats STATS = new ItemStats(3,0,0,0);
	public static final String NAME = "sword";
	
	public Sword(){
		super(NAME, IMAGE_SRC, DESCRIPTION, STATS);
		weaponRange = RANGE;
		weaponDamage = DAMAGE;
		weaponDelay = DELAY;
		weaponType = new MeleeWeapon();
	}
}
