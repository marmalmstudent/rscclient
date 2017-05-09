package client.UI;

public class InGameGridPanel extends InGamePanel
{
    public static final int ITEM_SLOT_WIDTH = 49;
    public static final int ITEM_SLOT_HEIGHT = 34;
    protected int nRows, nCols, gridX, gridY;
    protected final int gridBGSelectColor = 0xff0000;
    protected final int gridBGNotSelectColor = 0xd0d0d0;
    protected final int gridBGAlpha = 0xa0;
    protected final int gridLineColor = 0x000000;
    protected final int gridLineAlpha = 0xff;
        
    public int getRows() { return nRows; }
    public int getCols() { return nCols; }
    public int getSlots() { return nRows*nCols; }
    public void setRows(int rows) { nRows = rows; }
    public void setCols(int cols) { nCols = cols; }
    public int getGridBGNotSelectColor() { return gridBGNotSelectColor; }
    public int getGridBGSelectColor() { return gridBGSelectColor; }
    public int getGridBGAlpha() { return gridBGAlpha; }
    public int getGridLineColor() { return gridLineColor; }
    public int getGridLineAlpha() { return gridLineAlpha; }
	
	public InGameGridPanel(int rows, int cols)
	{
		nRows = rows;
		nCols = cols;
		height = nRows*ITEM_SLOT_HEIGHT+1;
		width = nCols*ITEM_SLOT_WIDTH+1;
	}
	
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean isMouseOverGrid(int mouseX, int mouseY)
	{
    	return (!(mouseX < x
        		|| mouseY < y
        		|| mouseX > x + width
        		|| mouseY > y + height));
	}
}
