package org.menus;

import java.awt.Rectangle;

public class InGameButton {
	protected final int notMouseoverColor = 0xffffff;
	protected final int mouseoverColor = 0xff0000;
	protected final int selectedColor = 0xffff00;
	protected String buttonText;
	protected Rectangle bounds;
	public InGameButton(int x, int y, int width, int height, String text)
	{
		bounds = new Rectangle(x, y, width, height);
		buttonText = text;
	}
	public int getX() { return bounds.x; }
	public int getY() { return bounds.y; }
	public int getWidth() { return bounds.width; }
	public int getHeight() { return bounds.height; }
	public String getButtonText() { return buttonText; }
	public int getMouseNotOverColor() { return notMouseoverColor; }
	public int getMouseOverColor() { return mouseoverColor; }
	public int getButtonSelectedColor() { return selectedColor; }
	
	public boolean isMouseOverButton(int mouseX, int mouseY)
	{
		return (mouseX >= bounds.x
				&& mouseY >= bounds.y
				&& mouseX < bounds.x + bounds.width
				&& mouseY < bounds.y + bounds.height);
	}
}
