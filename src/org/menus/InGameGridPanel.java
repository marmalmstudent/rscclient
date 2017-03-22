package org.menus;

import org.mudclient;

public abstract class InGameGridPanel extends InGamePanel
{
    public static final int ITEM_SLOT_WIDTH = 49;
    public static final int ITEM_SLOT_HEIGHT = 34;
    protected int nRows, nCols;
    protected final int gridBGSelectColor = 0xff0000;
    protected final int gridBGNotSelectColor = 0xd0d0d0;
    protected final int gridBGAlpha = 0xa0;
    protected final int gridLineColor = 0x000000;
    protected final int gridLineAlpha = 0xff;
        
    public int getRows() { return nRows; }
    public int getCols() { return nCols; }
    public void setRows(int rows) { nRows = rows; }
    public void setCols(int cols) { nCols = cols; }
    public int getGridBGNotSelectColor() { return gridBGNotSelectColor; }
    public int getGridBGSelectColor() { return gridBGSelectColor; }
    public int getGridBGAlpha() { return gridBGAlpha; }
    public int getGridLineColor() { return gridLineColor; }
    public int getGridLineAlpha() { return gridLineAlpha; }
    
	public int getGridHeight() { return nRows*ITEM_SLOT_HEIGHT; }
	public int getGridWidth() { return nCols*ITEM_SLOT_WIDTH; }
}
