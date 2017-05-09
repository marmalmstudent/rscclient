package model;

import entityhandling.EntityHandler;
import entityhandling.defs.ItemDef;

public class Item
{
	public int id, amount;
	
	public int icon() { return EntityHandler.getItemDef(id).getSprite(); }
	public int color() { return EntityHandler.getItemDef(id).getPictureMask(); }
	public String name() { return EntityHandler.getItemDef(id).getName(); }
	public String description() { return EntityHandler.getItemDef(id).getDescription(); }
	public String command() { return EntityHandler.getItemDef(id).getCommand(); }
	public boolean stackable() { return EntityHandler.getItemDef(id).isStackable(); }
	public boolean wieldable() { return EntityHandler.getItemDef(id).isWieldable(); }

	public Item(int id)
	{
		this(id, 0);
	}
	
	public Item(int id, int amount)
	{
		this.id = id;
		this.amount = amount;
	}
}
