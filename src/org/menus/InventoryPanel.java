package org.menus;

import org.mudclient;

public class InventoryPanel extends GamePanel
{
    private int nCols, nRows, nSlots;
    
    public InventoryPanel(int xCenter, int yCenter)
    {
        nCols = 5;
        nRows = 6;
        nSlots = nCols*nRows;
	    height = getTitleBarHeight() + getTopMarginHeight()
	    + getInvGridHeight()
	    + getBottomMarginHeight();
	    width = getLeftMarginWidth() + getInvGridWidth() + getRightMarginWidth();
	    // TODO: revise these when the bottom menu is done.
        x = xCenter*2 - width - 3;//xCenter - width/2;
        y = yCenter*2 - height - 35;//yCenter - height/2;
	    closeButton = new int[]{
	    		getTitleBarX() + getTitleBarWidth() - 88,
	    		getTitleBarY(),
	    		getTitleBarX() + getTitleBarWidth(),
	    		getTitleBarY() + getTitleBarHeight()};
    }
    
    public int getCols() { return nCols; }
    public int getRows() { return nRows; }
    public int getSlots() { return nSlots; }
	
	public int getInvGridHeight() { return nRows*mudclient.itemSlotHeight; }
	public int getInvGridWidth() { return nCols*mudclient.itemSlotWidth; }
	public int getInvGridX() { return getLeftMarginX() + getLeftMarginWidth(); }
	public int getInvGridY() { return getTopMarginY() + getTopMarginHeight(); }
}
