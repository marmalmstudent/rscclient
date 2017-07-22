package player;

import java.util.List;

public class Player
{
	public static final int MAX_INVENTORY_SLOTS = 35;
	public static final int MAX_BANK_SLOTS = 256;
	public static final int MAX_OFFER_SLOTS = 8;
	
	private ItemContainer inventory, bank, newBank, duelMyOffer,
	duelOpponentOffer, duelMyConfirm, duelOpponentConfirm;
	
	public Player()
	{
		inventory = new ItemContainer(MAX_INVENTORY_SLOTS, false);
		bank = new ItemContainer(MAX_BANK_SLOTS, true);
		newBank = new ItemContainer(MAX_BANK_SLOTS, true);
		duelMyOffer = new ItemContainer(MAX_BANK_SLOTS, false);
		duelOpponentOffer = new ItemContainer(MAX_BANK_SLOTS, false);
		duelMyConfirm = new ItemContainer(MAX_BANK_SLOTS, true);
		duelOpponentConfirm = new ItemContainer(MAX_BANK_SLOTS, true);
	}
	
	public List<Item> getInventoryItems() { return inventory.getItems(); }
	
	public List<Item> getBankItems() { return bank.getItems(); }
	
	public List<Item> getNewBankItems() { return newBank.getItems(); }
	
	public List<Item> getDuelMyItems() { return duelMyOffer.getItems(); }
	
	public List<Item> getDuelOpponentItems() { return duelOpponentOffer.getItems(); }
	
	public List<Item> getDuelConfirmMyItems() { return duelMyConfirm.getItems(); }
	
	public List<Item> getDuelConfirmOpponentItems() { return duelOpponentConfirm.getItems(); }
}
