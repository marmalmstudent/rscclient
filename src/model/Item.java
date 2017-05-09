package model;

import entityhandling.EntityHandler;

public class Item
{
	public String name, description, command;
	public int id, amount, color;
	public int icon;
	public boolean stackable, wieldable;

	public Item(int id)
	{
		this(id, 0);
	}
	
	public Item(int id, int amount)
	{
		this.id = id;
		this.amount = amount;
		if (id < 0 || id >= EntityHandler.itemCount())
		{
			icon = -1;
			color = -1;
			name = "";
			description = "";
			command = "";
		}
		else
		{
			icon = EntityHandler.getItemDef(id).getSprite();
			color = EntityHandler.getItemDef(id).getPictureMask();
			name = EntityHandler.getItemDef(id).getName();
			description = EntityHandler.getItemDef(id).getDescription();
			command = EntityHandler.getItemDef(id).getCommand();
		}
	}
}
