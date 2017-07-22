package player;

import java.util.List;

public class Player
{
	public static final int MAX_INVENTORY_SLOTS = 35;
	public static final int MAX_BANK_SLOTS = 256;
	
	private ItemContainer inventory, bank;
	
	public Player()
	{
		inventory = new ItemContainer(MAX_INVENTORY_SLOTS, false);
		bank = new ItemContainer(MAX_BANK_SLOTS, true);
	}
	
	public List<Item> getInventoryItems()
	{
		return inventory.getItems();
	}
	
	public List<Item> getBankItems()
	{
		return bank.getItems();
	}
}
