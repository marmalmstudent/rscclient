package player;

import java.util.List;

import client.Mob;

public class Player
{
	public static final int MAX_INVENTORY_SLOTS = 35;
	public static final int MAX_BANK_SLOTS = 256;
	public static final int MAX_OFFER_SLOTS = 8;
	
	public Mob me;
	
	public Player(Mob me)
	{
		this.me = me;
	}
	
	public void initContainers()
	{
		inventory = new ItemContainer(MAX_INVENTORY_SLOTS, false);
		bank = new ItemContainer(MAX_BANK_SLOTS, true);
		newBank = new ItemContainer(MAX_BANK_SLOTS, true);
		
		duelMyOffer = new ItemContainer(MAX_OFFER_SLOTS, false);
		duelOpponentOffer = new ItemContainer(MAX_OFFER_SLOTS, false);
		duelMyConfirm = new ItemContainer(MAX_OFFER_SLOTS, true);
		duelOpponentConfirm = new ItemContainer(MAX_OFFER_SLOTS, true);
		
		tradeMyOffer = new ItemContainer(MAX_OFFER_SLOTS, false);
		tradeOpponentOffer = new ItemContainer(MAX_OFFER_SLOTS, false);
		tradeMyConfirm = new ItemContainer(MAX_OFFER_SLOTS, true);
		tradeOpponentConfirm = new ItemContainer(MAX_OFFER_SLOTS, true);
	}
	
	public List<Item> getInventoryItems() { return inventory.getItems(); }
	public List<Item> getBankItems() { return bank.getItems(); }
	public List<Item> getNewBankItems() { return newBank.getItems(); }
	
	public List<Item> getDuelMyItems() { return duelMyOffer.getItems(); }
	public List<Item> getDuelOpponentItems() { return duelOpponentOffer.getItems(); }
	public List<Item> getDuelConfirmMyItems() { return duelMyConfirm.getItems(); }
	public List<Item> getDuelConfirmOpponentItems() { return duelOpponentConfirm.getItems(); }
	
	public List<Item> getTradeMyItems() { return tradeMyOffer.getItems(); }
	public List<Item> getTradeOtherItems() { return tradeOpponentOffer.getItems(); }
	public List<Item> getTradeConfirmMyItems() { return tradeMyConfirm.getItems(); }
	public List<Item> getTradeConfirmOtherItems() { return tradeOpponentConfirm.getItems(); }

	
	private ItemContainer inventory, bank, newBank;
	private ItemContainer duelMyOffer, duelOpponentOffer, duelMyConfirm, duelOpponentConfirm;
	private ItemContainer tradeMyOffer, tradeOpponentOffer, tradeMyConfirm, tradeOpponentConfirm;
}
