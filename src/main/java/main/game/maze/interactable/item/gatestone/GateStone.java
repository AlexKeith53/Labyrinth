package main.game.maze.interactable.item.gatestone;

import java.awt.Dimension;
import main.game.maze.interactable.Option;
import main.game.maze.interactable.creature.player.Player;
import main.game.maze.interactable.item.Item;

public abstract class GateStone extends Item{
	private static final int SIZE_WIDTH = 16;
	private static final int SIZE_HEIGHT = 16;
	private static final Dimension imageSize = new Dimension(SIZE_WIDTH,SIZE_HEIGHT);
	private final Player player;
	
	public GateStone(String imageSource, Player player, String name) {
		super(name, imageSource, imageSize);
		this.player = player;
	}

	public abstract void teleportTo();
	public abstract void dropGateStone();	
	
	public boolean exists(){
		return getPosition() != null;
	}

	public Player getPlayer() {
		return player;
	}
	
	@Override
	public void doInteract(Player player) {
		doAction(Option.PICKUP, player);
	}
	
	@Override
	public Option[] getOptions(Player player) {
		return new Option[]{Option.PICKUP};
	}
	
	@Override
	public void resetPosition(){
		setPosition(null);
	}
	
	@Override
	public void pickUp(Player player){
		resetPosition();
	}
	
	@Override
	public void doAction(Option option, Player player) {
		switch (option){
		case PICKUP: tryPickUp(player); break;
		case DROP: player.drop(this); break;
		default: return;
		}
	}
}
