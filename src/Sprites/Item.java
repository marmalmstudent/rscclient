package Sprites;

import model.Sprite;

public class Item {

	public String name;
	public Sprite icon;
	public String[] command;
	public boolean stackable, wieldable, tradeable;
	public int basePrice, color;
	public Sprite[] sprite;

	public String[] getCommand() { return command; }
	public Sprite getIcon() { return icon; }
	public int getBasePrice() { return basePrice; }
	public boolean isStackable() { return stackable; }
	public boolean isWieldable() { return wieldable; }
	public int getPictureMask() { return color; }
	public Sprite[] getSprites() { return sprite; }
	
	public Item()
	{
		this("null", null, new String[]{""}, false, false,
				false, 0, 0xffffff, null);
	}
	
	public Item(String name, String[] command)
	{
		this(name, null, command, false, false,
				false, 0, 0xffffff, null);
	}
	
	public Item(String name, Sprite icon, String[] command,
			boolean stackable, boolean wieldable, boolean tradeable,
			int basePrice, int color, Sprite[] sprite)
	{
		this.name = name;
		this.icon = icon;
		this.command = command;
		this.stackable = stackable;
		this.wieldable = wieldable;
		this.tradeable = tradeable;
		this.basePrice = basePrice;
		this.color = color;
		this.sprite = sprite;
	}
}
