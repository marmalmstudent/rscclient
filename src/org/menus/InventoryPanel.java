package org.menus;

import java.awt.Rectangle;

import org.mudclient;

public class InventoryPanel extends InGameGridPanel
{
    private int nCols, nRows, nSlots;
    
    public InventoryPanel(int xCenter, int yCenter)
    {
		frame = new InGameFrame("Inventory");
        nCols = 5;
        nRows = 6;
        nSlots = nCols*nRows;
	    setHeight(getGridHeight());
	    setWidth(getGridWidth());
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter - width / 2 - 3);
	    setY(yCenter - height / 2 - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
    }
    
    public int getCols() { return nCols; }
    public int getRows() { return nRows; }
    public int getSlots() { return nSlots; }
	
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
