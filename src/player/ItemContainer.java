package player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import exceptions.ItemAmountOutOfBoundsException;
import exceptions.ItemContainerFullException;
import exceptions.ItemNotFoundException;

public class ItemContainer
{
	private final int maxItems;
	private List<Item> items;
	
	public ItemContainer(int maxItems)
	{
		this.maxItems = maxItems;
		items = new ArrayList<Item>(maxItems);
	}
	
	/**
	 * Determines if this container is full.
	 * 
	 * @return {@code true} if this container is full, {@code false} if there are
	 * 		empty slots available.
	 */
	public boolean isFull()
	{
		return emptySlots() <= 0;
	}
	
	/**
	 * Calculated the number of available slots in this container.
	 * 
	 * @return The number of available slots in this container.
	 */
	public int emptySlots()
	{
		return maxItems - items.size();
	}
	
	public int occupiedSlots()
	{
		return items.size();
	}
	
	/**
	 * Attempts to add {@code item} to this container. If an item with the same id
	 * as that of {@code item} then {@code item}'s amount will be added to the existing
	 * item's.
	 * 
	 * @param item The item to add to this container.
	 * 
	 * @throws ItemContainerFullException If this container does not have room for
	 * 		{@code item}.
	 */
	public void addItem(Item item) throws ItemContainerFullException
	{
		if (!item.isStackable())
		{
			if (isFull())
				throw new ItemContainerFullException();
			items.add(item);
		}
		
		Item nextAvailable = findItem(item.getID());
		if (nextAvailable != null)
			nextAvailable.addAmount(item.getAmount());
		else
			items.add(item);
	}
	
	/**
	 * Attempts to delete {@code item} from this container. If {@code item}'s amount
	 * is less than that of the item in this container, {@code item}'s amount will
	 * be removed from the item in this container. If {@code item}'s amount is equal
	 * to that of the item in this container then the item will be completely removed.
	 * 
	 * @param item The item to delete from this container.
	 * 
	 * @throws ItemNotFoundException If this container does not have an item with
	 * 		the same id as {@code item}.
	 */
	public void delItem(Item item) throws ItemNotFoundException
	{
		Item foundItem = findItem(item.getID());
		if (foundItem == null)
			throw new ItemNotFoundException();
		
		foundItem.delAmount(item.getAmount());
		if (foundItem.getAmount() == 0)
			items.remove(foundItem);
	}
	
	/**
	 * Attempts to find the first occurrence of an item with the id {@code id}
	 * among the items in this container.
	 * 
	 * @param id The id of the item to look for.
	 * @return The item if it is found, or {@code null} if not.
	 */
	private Item findItem(int id)
	{
		for (Iterator<Item> itr = items.iterator(); itr.hasNext();)
		{
			Item tmp = itr.next();
			if (tmp.getID() == id)
				return tmp;
		}
		return null;
	}
}
