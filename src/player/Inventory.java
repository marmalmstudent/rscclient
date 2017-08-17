package player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.GameImageMiddleMan;
import client.GameWindow.MouseVariables;
import client.mudclient.OpenMenu;
import client.mudclient.SelectedItem;
import client.mudclient.SelectedSpell;
import client.UI.InGameGrid;
import client.UI.menus.MenuRightClick;
import client.UI.panels.InventoryPanel;

public class Inventory
{
	public static final int MAX_INVENTORY_SLOTS = 35;
	
	
	public List<Item> items()
	{
		return inventory.getItems();
	}

	public final List<MenuRightClick> draw(boolean flag, final SelectedItem selItem,
			final SelectedSpell selSpell, int inventoryCount)
	{
		List<MenuRightClick> result = new ArrayList<MenuRightClick>();
		invPan.getFrame().drawComponent();
		invPan.getInvGrid().drawStaticGrid(items(),
				inventoryCount, invPan.getInvCountTextColor());
		if (!flag)
			return result;
		
		if (invPan.getInvGrid().isMouseOver())
			result = handleMouseover(selItem, selSpell);
		else if (invPan.getFrame().getCloseButton().isMouseOver())
		{
			if (mv.leftDown())
			{
				OpenMenu.get().close(OpenMenu.INVENTORY);
				mv.releaseButton();
			}
		}
		else if (invPan.getFrame().isMouseOver())
			if (mv.leftDown())
				mv.releaseButton();
		return result;
	}
	
	protected void initGraphics(Point center, GameImageMiddleMan graphics)
	{
		invPan = new InventoryPanel(center, graphics);
	}

	private List<MenuRightClick> handleMouseover(final SelectedItem selItem,
			final SelectedSpell selSpell)
	{
		int mouseX = mv.getX();
		int mouseY = mv.getY();
		InGameGrid invGrid = invPan.getInvGrid();
		int xInInv = mouseX - invGrid.getX();
		int yInInv = mouseY - invGrid.getY();
		int currentInventorySlot = xInInv / InGameGrid.ITEM_SLOT_WIDTH
				+ (yInInv / InGameGrid.ITEM_SLOT_HEIGHT) * invGrid.getCols();
		
		List<MenuRightClick> lmrc = new ArrayList<MenuRightClick>();
		if (currentInventorySlot < invGrid.getCols()*invGrid.getRows())
		{
			Item item = items().get(currentInventorySlot);
			if (item.getID() == Item.NONE)
				return lmrc;
			
			if (selSpell != null) {
				if (selSpell.getSpell().getSpellType() == 3)
				{
					MenuRightClick mrc = new MenuRightClick();
					mrc.text1 = String.format("Cast %s on",
							selSpell.getSpell().getName());
					mrc.text2 = "@lre@" + item.getName();
					mrc.id = 600;
					mrc.actionType = currentInventorySlot;
					mrc.actionVariable = selSpell.getID();
					lmrc.add(mrc);
					return lmrc;
				}
			} else {
				if (selItem != null) {
					MenuRightClick mrc = new MenuRightClick();
					mrc.text1 = String.format("Use %s with", selItem.getItem().getName());
					mrc.text2 = "@lre@" + item.getName();
					mrc.id = 610;
					mrc.actionType = currentInventorySlot;
					mrc.actionVariable = selItem.getIndex();
					lmrc.add(mrc);
					return lmrc;
				}
				if (item.isWearing()) {
					MenuRightClick mrc = new MenuRightClick();
					mrc.text1 = "Remove";
					mrc.text2 = "@lre@" + item.getName();
					mrc.id = 620;
					mrc.actionType = currentInventorySlot;
					lmrc.add(mrc);
				} else if (item.isWieldable()) {
					MenuRightClick mrc = new MenuRightClick();
					mrc.text1 = "Wear";
					mrc.text2 = "@lre@" + item.getName();
					mrc.id = 630;
					mrc.actionType = currentInventorySlot;
					lmrc.add(mrc);
				}
				if (!item.getCommand().equals("")) {
					MenuRightClick mrc = new MenuRightClick();
					mrc.text1 = item.getCommand();
					mrc.text2 = "@lre@" + item.getName();
					mrc.id = 640;
					mrc.actionType = currentInventorySlot;
					lmrc.add(mrc);
				}
				MenuRightClick mrc = new MenuRightClick();
				mrc.text1 = "Use";
				mrc.text2 = "@lre@" + item.getName();
				mrc.id = 650;
				mrc.actionType = currentInventorySlot;
				lmrc.add(mrc);
				
				mrc = new MenuRightClick();
				mrc.text1 = "Drop";
				mrc.text2 = "@lre@" + item.getName();
				mrc.id = 660;
				mrc.actionType = currentInventorySlot;
				lmrc.add(mrc);
				
				mrc = new MenuRightClick();
				mrc.text1 = "Examine";
				mrc.text2 = "@lre@" + item.getName() +
						(admin >= 2 ? " @or1@(" + item.getID() + ")" : "");
				mrc.id = 3600;
				mrc.actionType = item.getID();
				lmrc.add(mrc);
			}
		}
		return lmrc;
	}
	
	protected Inventory(int admin)
	{
		inventory = new ItemContainer(MAX_INVENTORY_SLOTS, false);
		this.admin = admin;
	}

	private ItemContainer inventory;
	private InventoryPanel invPan;
	private static MouseVariables mv = MouseVariables.get();
	private final int admin;
}
