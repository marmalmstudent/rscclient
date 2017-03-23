package org.menus;

import java.awt.Rectangle;

import org.mudclient;

public class InventoryPanel extends InGameGridPanel
{
    private final int invCountTextColor = 0xffff00;
    
    public InventoryPanel(int xCenter, int yCenter)
    {
		frame = new InGameFrame("Inventory");
        nCols = 5;
        nRows = 6;
	    setHeight(getGridHeight());
	    setWidth(getGridWidth());
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter*2 - width - 3);
	    setY(yCenter*2 - height - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
		gridY =  getY();
		gridX = getX();
    }
	public int getInvCountTextColor() { return invCountTextColor; }
	public int getInvGridX() { return getX(); }
	public int getInvGridY() { return getY(); }
	
	public boolean isMouseOverInvGrid(int mouseX, int mouseY)
	{
		return (mouseX >= getInvGridX()
        		&& mouseY >= getInvGridY()
        		&& mouseX < getInvGridX() + getGridWidth() 
        		&& mouseY < getInvGridY() + getGridHeight());
	}
}
