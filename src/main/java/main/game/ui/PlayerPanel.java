package main.game.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import main.game.Config;
import main.game.maze.Maze;
import main.game.maze.interactable.Option;
import main.game.maze.interactable.creature.player.Player;
import main.game.maze.interactable.item.Item;
import main.game.maze.interactable.item.Stackable;
import main.game.util.Size;
import main.game.util.Util;

public class PlayerPanel extends JPanel {
	private static final long serialVersionUID = 8598148008009578889L;
	private static final Color COLOR_BACKGROUND = Color.BLACK;
	private static final Color COLOR_INVENTORY_SLOT_STROKE = Color.GRAY;
	private static final Color COLOR_INVENTORY_SLOT_CONTENT_EQUIPPED = Color.LIGHT_GRAY;
	private static final int PADDING_INVENTORY_INTERNAL = 5;
	private static final int PADDING_INVENTORY_EXTERNAL = 20;
	private static final int SWITCHTAB_HEIGHT = 30;
	public static final int WIDTH = MiniMap.WIDTH;
	public static final int HEIGHT = Board.HEIGHT + KeyBag.HEIGHT - MiniMap.HEIGHT;

	private Maze maze;
	private int imageSize;
	
	public PlayerPanel(Maze maze) {
		this.maze = maze;
		calculateInventorySlotSize();
		addMouseListener(new MouceClickListener());
	}
	
	private void calculateInventorySlotSize() {
		imageSize = (WIDTH - 2 * PADDING_INVENTORY_EXTERNAL - 
				(Config.INVENTORY_COUNT_HORIZONTAL - 1) * PADDING_INVENTORY_INTERNAL) / Config.INVENTORY_COUNT_HORIZONTAL;
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		setBackground(COLOR_BACKGROUND);
		drawInventorySlots(g);
		drawItems(g);
	}

	private void drawItems(Graphics g) {
		List<Item> items = maze.getPlayer().getItems();
		Iterator<Item> it = items.iterator();
		for (int i = 0; i < Config.INVENTORY_COUNT_VERTICAL; i++){
			for (int j = 0; j < Config.INVENTORY_COUNT_HORIZONTAL; j++){
				if (it.hasNext()){
					Item item = it.next();
					Point imageCorner = getItemCorner(j,i);
					Image image = item.getImage();
					g.drawImage(image, imageCorner.x, imageCorner.y, imageSize, imageSize, null);
					if (item instanceof Stackable){
						Stackable sItem = (Stackable) item;
						String text = Integer.toString(sItem.getQuantity());
					    Util.drawTextSmall(g, imageCorner, text);						
					}
				} else {
					return;
				}
			}
		}
		
	}

	private void drawInventorySlots(Graphics g) {
		for (int i = 0; i < Config.INVENTORY_COUNT_HORIZONTAL; i++){
			for (int j = 0; j < Config.INVENTORY_COUNT_VERTICAL; j++){
				Point imageCorner = getItemCorner(i,j);
				g.setColor(COLOR_INVENTORY_SLOT_STROKE);
				int inventorySlot = i+j*Config.INVENTORY_COUNT_HORIZONTAL;
				if (maze.getPlayer().getItems().size() > inventorySlot){
					if (maze.getPlayer().hasEquipped(maze.getPlayer().getItems().get(inventorySlot))){
						g.setColor(COLOR_INVENTORY_SLOT_CONTENT_EQUIPPED);
					}
				}
				g.fillOval(imageCorner.x, imageCorner.y, imageSize, imageSize);
				g.setColor(COLOR_BACKGROUND);
				g.fillOval(imageCorner.x+4, imageCorner.y+4, imageSize-8, imageSize-8);
			}
		}
		
	}

	private Point getItemCorner(int i, int j) {
		return new Point(PADDING_INVENTORY_EXTERNAL + i*(imageSize + PADDING_INVENTORY_INTERNAL), 
				PADDING_INVENTORY_EXTERNAL + SWITCHTAB_HEIGHT + j*(imageSize + PADDING_INVENTORY_INTERNAL));
	}
	
	private class MouceClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent me){
			for (int i = 0; i < Config.INVENTORY_COUNT_HORIZONTAL; i++){
				for (int j = 0; j < Config.INVENTORY_COUNT_VERTICAL; j++){
					Point corner = getItemCorner(i,j);
					if (me.getPoint().x >= corner.x && me.getPoint().x < corner.x + imageSize){
						if (me.getPoint().y >= corner.y && me.getPoint().y < corner.y + imageSize){
							int inventorySlot = i+j*Config.INVENTORY_COUNT_HORIZONTAL;
							if (me.getButton() == MouseEvent.BUTTON1){
								doLeftClick(inventorySlot);
							} else if (me.getButton() == MouseEvent.BUTTON3){
								doRightClick(me, inventorySlot);
							}
						}
					}
				}
			}
		}

		private void doRightClick(MouseEvent me, int inventorySlot) {
			if (maze.getPlayer().getItems().size() > inventorySlot){
				final RightClickMenu menu = new RightClickMenu(inventorySlot);
				
				menu.setVisible(true);	//to initalize menu and generate size
				menu.setVisible(false);
				Point point = me.getPoint();
				if (point.x > WIDTH - menu.getWidth()){
					point.setLocation(WIDTH - menu.getWidth(), point.y);
				}
				if (point.y > HEIGHT - menu.getHeight()){
					point.setLocation(point.x, HEIGHT - menu.getHeight());
				}
				menu.addMouseListener(new MouseAdapter() {
					Size dim = new Size(menu.getSize());
					@Override
					public void mouseExited(MouseEvent e) {
						if (!Util.areasOverlap(new Point(0,0), dim, e.getPoint(), new Size(1,1), 0)){
							menu.setVisible(false);
						}
					}
				});
			    menu.show(me.getComponent(), point.x, point.y);			 
			}
		}

		private void doLeftClick(int inventorySlot) {
			if (maze.getPlayer().getItems().size() > inventorySlot){
				Player player = maze.getPlayer();
				Item item = player.getItems().get(inventorySlot);
				Option[] options = item.getOptions(player);
				if (options.length > 0){
					item.doAction(options[0], maze.getPlayer());
				} else {
					throw new IllegalStateException("every item should return atleast 1 option!: " + item.getName());
				}			 
			}
		}
	}
	
	private class RightClickMenu extends JPopupMenu {
		private static final long serialVersionUID = 6176663489076570027L;
		private Item item;
		private RightClickMenu(int inventorySlot){
			Player player = maze.getPlayer();
			item = player.getItems().get(inventorySlot);
			Option[] options = item.getOptions(player);
			setFocusable(false);
			for (Option option: options){
				final Option optionFinal = option;
				JMenuItem menuItem = new JMenuItem(optionFinal.toString() + ": " + item.getName());
				menuItem.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						item.doAction(optionFinal, maze.getPlayer());
					}
				});
		        add(menuItem);
			}
		}
	}

}
