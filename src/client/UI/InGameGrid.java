package client.UI;

import java.util.List;

import client.GameImageMiddleMan;
import client.GameWindow.MouseVariables;
import client.mudclient;
import player.Item;

public class InGameGrid extends InGamePanel
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
    
    private static MouseVariables mv = MouseVariables.get();
	
	public InGameGrid(int rows, int cols, GameImageMiddleMan g)
	{
		graphics = g;
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
	
	public boolean isMouseOverGrid()
	{
    	return (!(mv.getX() < x
        		|| mv.getY() < y
        		|| mv.getX() > x + width
        		|| mv.getY() > y + height));
	}

	public void drawStorableGrid(
			List<Item> depositedItems, int depItemStart, int depItemCount,
			int depositedTextColor, int selectedDepIdx,
			List<Item> withdrawnItems, int withItemCount, int withdrawnTextColor)
	{
		int itemIdx = depItemStart;
		for (int row = 0; row < nRows; row++)
		{
			for (int col = 0; col < nCols; col++)
			{
				int slotX = x + col*ITEM_SLOT_WIDTH;
				int slotY = y + row*ITEM_SLOT_HEIGHT;
				drawItemBox(depositedItems.get(itemIdx), slotX, slotY,
						selectedDepIdx == itemIdx,
						itemIdx < depItemCount && depositedItems.get(itemIdx).getID() != -1);
				if (itemIdx < depItemCount && depositedItems.get(itemIdx).getID() != -1)
				{
					drawDepositedText(slotX, slotY, depositedItems.get(itemIdx).getAmount(),
							depositedTextColor);
					drawWithdrawnText(slotX, slotY,
							mudclient.itemCount(depositedItems.get(itemIdx), withdrawnItems, withItemCount),
							withdrawnTextColor);
				}
				++itemIdx;
			}
		}
	}

	public void drawStaticGrid(List<Item> items, int itemCount, int textColor)
	{
		for (int j = 0; j < getSlots(); j++)
		{
			Item item = items.get(j);
			int col = x + (j % nCols) * ITEM_SLOT_WIDTH;
			int row = y + (j / nCols) * ITEM_SLOT_HEIGHT;

			drawItemBox(item, col, row,
					j < itemCount && item.isWearing(),
					j < itemCount && item.getID() != -1);
			if (j < itemCount && item.getID() != -1
					&& item.isStackable())
				drawDepositedText(col, row, item.getAmount(), textColor);
		}
	}

	public void drawDepositedText(int col, int row, long amount, int color)
	{
		graphics.drawString(mudclient.getAbbreviatedValue(amount),
				col + 1, row + 10, 1, color);
	}

	private void drawWithdrawnText(int slotX, int slotY, int amount, int color)
	{
		graphics.drawBoxTextRight(mudclient.getAbbreviatedValue(amount),
				slotX + ITEM_SLOT_WIDTH - 1,
				slotY + ITEM_SLOT_HEIGHT - 5, 1, color);
	}

	public void drawItemBox(Item item,
			int slotX, int slotY, boolean selected, boolean drawSprite)
	{
		if (selected)
			graphics.drawBoxAlpha(slotX+1, slotY+1,
					ITEM_SLOT_WIDTH-1,
					ITEM_SLOT_HEIGHT-1,
					getGridBGSelectColor(),
					getGridBGAlpha());
		else
			graphics.drawBoxAlpha(slotX+1, slotY+1,
					ITEM_SLOT_WIDTH-1,
					ITEM_SLOT_HEIGHT-1,
					getGridBGNotSelectColor(),
					getGridBGAlpha());
		graphics.drawBoxEdge(slotX, slotY,
				ITEM_SLOT_WIDTH+1,
				ITEM_SLOT_HEIGHT+1, getGridLineColor());
		if (drawSprite)
			graphics.spriteClip4(slotX+1, slotY+1,
					InGameGrid.ITEM_SLOT_WIDTH-1,
					InGameGrid.ITEM_SLOT_HEIGHT-1,
					graphics.getSprite(mudclient.SPRITE_ITEM_START + item.getIcon()),
					item.getColor(), 0, 0, false);
	}
}
