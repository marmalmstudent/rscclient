package client.UI;

import model.Sprite;

public class InGameButton extends InGameComponent {
	protected final int notMouseoverColor = 0xffffff;
	protected final int mouseoverColor = 0xff0000;
	protected final int selectedColor = 0xffff00;
	protected String buttonText;
	protected Sprite sprite;
	protected int spriteIdx;
	
	public InGameButton(int x, int y, int width, int height, String text)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		buttonText = text;
	}
	
	public String getButtonText() { return buttonText; }
	public int getMouseNotOverColor() { return notMouseoverColor; }
	public int getMouseOverColor() { return mouseoverColor; }
	public int getButtonSelectedColor() { return selectedColor; }
	
	public Sprite getSprite() { return sprite; }
	public int getSpriteIdx() { return spriteIdx; }
	public void setSprite(Sprite sprite, int index)
	{
		this.sprite = sprite;
		spriteIdx = index;
	}
	
	public boolean isMouseOverButton(int mouseX, int mouseY)
	{
		return (mouseX >= x
				&& mouseY >= y
				&& mouseX < x + width
				&& mouseY < y + height);
	}
	
	public void setButtonText(String text) {
		buttonText = text;
	}
}
