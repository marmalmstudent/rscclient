package player;

public class Player
{
	public static final int MAX_INVENTORY_SLOTS = 30;
	public static final int MAX_BANK_SLOTS = 256;
	
	private ItemContainer inventory, bank;
	
	public Player()
	{
		inventory = new ItemContainer(MAX_INVENTORY_SLOTS);
		bank = new ItemContainer(MAX_BANK_SLOTS);
	}
}
