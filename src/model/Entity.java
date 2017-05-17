package model;

public class Entity
{
	public Sprite[] sprites;
	private String name;
	private int width, height, gender;
	private boolean hasAttack, hasFlip;
	
	public Entity(Sprite[] sprites, String name,
			int gender, boolean hasAttack, boolean hasFlip)
	{
		this.sprites = sprites;
		this.name = name;
		this.gender = gender;
		this.hasAttack = hasAttack;
		this.hasFlip = hasFlip;
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getGender() { return gender; }
	public boolean hasAttack() { return hasAttack; }
	public boolean hasFlip() { return hasFlip; }
}
