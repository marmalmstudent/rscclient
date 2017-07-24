package client.UI;

import client.GameImageMiddleMan;
import client.GameWindow.MouseVariables;

public abstract class InGameComponent
{
	protected GameImageMiddleMan graphics;
    protected int x, y, width, height;
    
    private static MouseVariables mv = MouseVariables.get();
    
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getX() { return x; }
	public int getY() { return y; }

	public void setWidth(int width) { this.width = width; }
	public void setHeight(int height) { this.height = height; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }

    
    public boolean isMouseOver()
    {
    	return (!(mv.getX() < x
        		|| mv.getY() < y
        		|| mv.getX() > x + width
        		|| mv.getY() > y + height));
    }
}
