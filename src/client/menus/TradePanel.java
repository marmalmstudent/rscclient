package client.menus;

import java.awt.Rectangle;

import client.GameImage;
import client.mudclient;
import client.model.Sprite;

public class TradePanel extends InGameGridPanel
{
    private final int invCountTextColor = 0xffff00;
	private int nTradeCols, nTradeRows, middleMarginWidth;
	private int plrTextBoxHeight, opntTextBoxHeight, itemInfoBarHeight;
    
    private Rectangle plrTextBox, plrOfferBox, opntTextBox, opntOfferBox,
    itemInfoBar, mdlMarginBar, accptDclnBox;
    private InGameButton acceptBtn, declineBtn;
    
	public TradePanel(int xCenter, int yCenter)
	{
		frame = new InGameFrame("Trading width: ");
		nCols = 5;
		nRows = 6;
		nTradeCols = 4;
		nTradeRows = 3;
		middleMarginWidth = 11;
		plrTextBoxHeight = 18;
		opntTextBoxHeight = 22;
		itemInfoBarHeight = 20;
		setWidth(nTradeCols*ITEM_SLOT_WIDTH+1+middleMarginWidth+getGridWidth());
		setHeight(plrTextBoxHeight + 2*(nTradeRows*ITEM_SLOT_HEIGHT+1)
				+ opntTextBoxHeight + itemInfoBarHeight);
		setX(xCenter - width/2);
		setY(yCenter - height/2);
	    frame.setBounds(new Rectangle(x, y, width, height));
	    
	    plrTextBox = new Rectangle(x, y, width, plrTextBoxHeight);
	    plrOfferBox = new Rectangle(x, plrTextBox.y + plrTextBox.height,
	    		nTradeCols*ITEM_SLOT_WIDTH+1, nTradeRows*ITEM_SLOT_HEIGHT+1);
	    opntTextBox = new Rectangle(x, plrOfferBox.y + plrOfferBox.height,
				nTradeCols*ITEM_SLOT_WIDTH+1, opntTextBoxHeight);
	    opntOfferBox = new Rectangle(x, opntTextBox.y+opntTextBox.height,
				nTradeCols*ITEM_SLOT_WIDTH+1, nTradeRows*ITEM_SLOT_HEIGHT+1);
	    gridX = plrOfferBox.x+plrOfferBox.width+middleMarginWidth;
	    gridY = plrTextBox.y+plrTextBox.height;
	    mdlMarginBar = new Rectangle(plrOfferBox.x+plrOfferBox.width,
				plrTextBox.y+plrTextBox.height,
				middleMarginWidth,
				plrOfferBox.height+opntTextBox.height+opntOfferBox.height);
	    accptDclnBox = new Rectangle(
				getGridX(),
				getGridY()+getGridHeight(),
				getGridWidth(),
				plrOfferBox.height+opntTextBox.height+opntOfferBox.height-getGridHeight());
	    itemInfoBar = new Rectangle(x, opntOfferBox.y + opntOfferBox.height,
	    		width, itemInfoBarHeight);
	}
	
	public void addDeclineButton(Sprite sprite, int index)
	{
	    Rectangle declnBtn = new Rectangle(
	    		accptDclnBox.x - 1 + accptDclnBox.width - (sprite.getWidth() + 2*sprite.getXShift()),
	    		accptDclnBox.y + 1, sprite.getWidth() + 2*sprite.getXShift(),
	    		sprite.getHeight() + 2*sprite.getYShift());
	    declineBtn = new InGameButton(declnBtn.x, declnBtn.y,
	    		declnBtn.width, declnBtn.height, "");	
	    declineBtn.setSprite(sprite, index);
	}
	
	public void addAcceptButton(Sprite sprite, int index)
	{
	    Rectangle accptBtn = new Rectangle(accptDclnBox.x + 1,
	    		accptDclnBox.y + 1, sprite.getWidth() + 2*sprite.getXShift(),
	    		sprite.getHeight() + 2*sprite.getYShift());
	    acceptBtn = new InGameButton(accptBtn.x, accptBtn.y,
	    		accptBtn.width, accptBtn.height, "");	
	    acceptBtn.setSprite(sprite, index);
	}
	
	public boolean isMouseOverOfferGrid(int mouseX, int mouseY)
	{
		return (mouseX > getPlrOfferGridX()
				&& mouseY > getPlrOfferGridY()
				&& mouseX < getPlrOfferGridX() + getOfferGridWidth()
				&& mouseY < getPlrOfferGridY() + getOfferGridHeight());
	}
	
	public InGameButton getAcceptButton() { return acceptBtn; }
	public InGameButton getDeclineButton() { return declineBtn; }
	public int getInvCountTextColor() { return invCountTextColor; }
	public int getOfferGridRows() { return nTradeRows; }
	public int getOfferGridCols() { return nTradeCols; }
	public int getOfferGridSlots() { return nTradeRows*nTradeCols; }
	public int getOfferGridHeight() { return getOfferGridRows()*ITEM_SLOT_HEIGHT+1; }
	public int getOfferGridWidth() { return getOfferGridCols()*ITEM_SLOT_WIDTH+1; }
	
	public int getInvGridX() { return getMdlMarginBarX() + getMdlMarginBarWidth(); }
	public int getInvGridY() { return getPlrTextBoxY() + getPlrTextBoxHeight(); }
	public int getPlrOfferGridX() { return getX(); }
	public int getPlrOfferGridY() { return getPlrTextBoxY() + getPlrTextBoxHeight(); }
	public int getOpntOfferGridX() { return getX(); }
	public int getOpntOfferGridY() { return getOpntTextBoxY() + getOpntTextBoxHeight(); }

	public int getPlrTextBoxX() { return plrTextBox.x; }
	public int getPlrTextBoxY() { return plrTextBox.y; }
	public int getPlrTextBoxWidth() { return plrTextBox.width; }
	public int getPlrTextBoxHeight() { return plrTextBox.height; }

	public int getPlrOfferBoxX() { return plrOfferBox.x; }
	public int getPlrOfferBoxY() { return plrOfferBox.y; }
	public int getPlrOfferBoxWidth() { return plrOfferBox.width; }
	public int getPlrOfferBoxHeight() { return plrOfferBox.height; }

	public int getOpntTextBoxX() { return opntTextBox.x; }
	public int getOpntTextBoxY() { return opntTextBox.y; }
	public int getOpntTextBoxWidth() { return opntTextBox.width; }
	public int getOpntTextBoxHeight() { return opntTextBox.height; }

	public int getOpntOfferBoxX() { return opntOfferBox.x; }
	public int getOpntOfferBoxY() { return opntOfferBox.y; }
	public int getOpntOfferBoxWidth() { return opntOfferBox.width; }
	public int getOpntOfferBoxHeight() { return opntOfferBox.height; }

	public int getMdlMarginBarX() { return mdlMarginBar.x; }
	public int getMdlMarginBarY() { return mdlMarginBar.y; }
	public int getMdlMarginBarWidth() { return mdlMarginBar.width; }
	public int getMdlMarginBarHeight() { return mdlMarginBar.height; }

	public int getAccptDclnBoxX() { return accptDclnBox.x; }
	public int getAccptDclnBoxY() { return accptDclnBox.y; }
	public int getAccptDclnBoxWidth() { return accptDclnBox.width; }
	public int getAccptDclnBoxHeight() { return accptDclnBox.height; }
	
	public int getItemInfoBarX() { return itemInfoBar.x; }
	public int getItemInfoBarY() { return itemInfoBar.y; }
	public int getItemInfoBarWidth() { return itemInfoBar.width; }
	public int getItemInfoBarHeight() { return itemInfoBar.height; }
}
