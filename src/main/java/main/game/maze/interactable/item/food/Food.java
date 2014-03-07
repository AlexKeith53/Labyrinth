package main.game.maze.interactable.item.food;

import java.awt.Dimension;

import main.game.maze.interactable.Option;
import main.game.maze.interactable.creature.player.Player;
import main.game.maze.interactable.item.NonStackable;
import main.game.maze.interactable.item.behaviours.ItemBehaviourFactory;
import main.game.maze.interactable.item.behaviours.ItemType;
import main.game.maze.interactable.item.behaviours.rightclickbehaviour.RightClickBehaviour;

public abstract class Food extends NonStackable {
	private static final int SIZE_WIDTH = 20;
	private static final int SIZE_HEIGHT = 20;
	private static final Dimension imageSize = new Dimension(SIZE_WIDTH, SIZE_HEIGHT);
	private static final ItemType itemType = ItemType.EDIBLE;
	private static final RightClickBehaviour rightClickBehaviour = ItemBehaviourFactory.getRightClickBehaviour(itemType);
	private final int healAmount;
	
	public Food(int healAmount, String name, String imageSrc) {
		super(name, imageSrc, imageSize);
		if (healAmount <= 0){
			throw new IllegalArgumentException("heal amount must be greater than zero");
		}
		this.healAmount = healAmount;
	}

	@Override
	public Option[] getOptions(Player player) {
		return rightClickBehaviour.getOptions(this, player);
	}

	@Override
	public void doInteract(Player player) {
		doAction(Option.PICKUP, player);
	}

	@Override
	public void resetPosition() {
		getPosition().getRoom().removeDroppedItem(this);
		setPosition(null);
	}

	@Override
	public void doAction(Option option, Player player) {
		switch (option){
		case PICKUP: tryPickUp(player); break;
		case DROP: player.drop(this); break;
		case EAT: player.eat(this); break;
		default: return;
		}
	}

	public int getHealAmount() {
		return healAmount;
	}
	
}
