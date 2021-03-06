package client.UI;

import client.GameImageMiddleMan;

public abstract class InGameComponent
{
	protected GameImageMiddleMan graphics;
    protected int x, y, width, height;
    
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getX() { return x; }
	public int getY() { return y; }

	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) { this.height = height; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }

    
    public boolean isMouseOver(int mouseX, int mouseY)
    {
    	return (!(mouseX < x
        		|| mouseY < y
        		|| mouseX > x + width
        		|| mouseY > y + height));
    }
}
