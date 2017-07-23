package player;

import entityhandling.EntityHandler;
import exceptions.ItemAmountOutOfBoundsException;
import exceptions.ItemNotFoundException;

public class Item
{
	
	public int getID() { return id; }
	public long getAmount() { return amount; }
	
	public long getStackSize() { return stackSize; }
	public int getIcon() { return icon; }
	public int getColor() { return color; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public String getCommand() { return command; }
	public boolean isStackable() { return stackable; }
	public boolean isWieldable() { return wieldable; }

	public Item(int id, boolean forceStackable)
	{
		this(id, forceStackable, 0);
	}
	
	public Item(int id, boolean forceStackable, long amount)
	{
		this.id = id;
		this.amount = amount;
		
		stackSize = Long.MAX_VALUE;
		if (id > 0)
		{
			icon = EntityHandler.getItemDef(id).getSprite();
			color = EntityHandler.getItemDef(id).getPictureMask();
			name = EntityHandler.getItemDef(id).getName();
			description = EntityHandler.getItemDef(id).getDescription();
			command = EntityHandler.getItemDef(id).getCommand();
			stackable = forceStackable ? true : EntityHandler.getItemDef(id).isStackable();
			wieldable = EntityHandler.getItemDef(id).isWieldable();
		}
		else
		{
			icon = 0;
			color = 0;
			name = "";
			description = "";
			command = "";
			stackable = false;
			wieldable = false;
		}
	}
	
	@Override
	public Object clone()
	{
		return new Item(id, false, amount);
	}
	
	public void setAmount(long amt) throws ItemAmountOutOfBoundsException
	{
		if (amt < 0
				|| ((!stackable && amt > 1) || amt > stackSize))
			throw new ItemAmountOutOfBoundsException();
		amount = amt;
	}
	
	/**
	 * Adds {@code amt} to this item.
	 * 
	 * @param amt The amount to add.
	 * @throws ItemAmountOutOfBoundsException If {@code amt} can not be added to
	 * 		this item.
	 */
	public void addAmount(long amt) throws ItemAmountOutOfBoundsException
	{
		if (amt < 0)
			delAmount(-amt);
		if ((!stackable && amt > 1) || amount + amt > stackSize)
			throw new ItemAmountOutOfBoundsException();
		
		amount += amt;
	}
	
	/**
	 * Deletes {@code amt} from this item.
	 * 
	 * @param amt The amount do delete.
	 * @throws ItemAmountOutOfBoundsException if {@code amt} is negative
	 * 		or greater than this item's amount.
	 */
	public void delAmount(long amt) throws ItemAmountOutOfBoundsException
	{
		if (amt < 0)
			addAmount(-amt);
		if (amount - amt < 0)
			throw new ItemAmountOutOfBoundsException();
		
		amount -= amt;
	}
	
	/**
	 * Finds the maximum amount that can be added (if <code>amt > 0</code>)
	 * or removed (if <code>amt < 0</code>) from this container.
	 * 
	 * @param amt The amount to add (>0) or remove (<0), if possible.
	 * 
	 * @return The amount that can be added/removed
	 */
	public long maxModify(long amt)
	{
		if (amt < 0 && amount - amt < 0)
			return amount;
		if (amt > 0 && amount + amt > stackSize)
			return stackSize - amount;
		return amt;
	}
	
	private long amount;
	private final int id;
	
	private final long stackSize;
	private final int icon, color;
	private final String name, description, command;
	private final boolean stackable, wieldable;
}
