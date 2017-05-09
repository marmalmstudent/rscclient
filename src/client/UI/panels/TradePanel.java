package client.UI.panels;

import java.awt.Rectangle;

import client.GameImage;
import client.GameImageMiddleMan;
import client.mudclient;
import client.UI.InGameButton;
import client.UI.InGameFrame;
import client.UI.InGameGridPanel;
import client.UI.InGamePanel;
import model.Sprite;

public class TradePanel extends InGamePanel
{
	private InGameGridPanel invGrid, myOfferGrid, otherOfferGrid;
	private final int invCountTextColor = 0xffff00;
	private int nTradeCols, nTradeRows, middleMarginWidth;
	private int plrTextBoxHeight, opntTextBoxHeight, itemInfoBarHeight;

	private Rectangle plrTextBox, opntTextBox,
	itemInfoBar, mdlMarginBar, accptDclnBox;
	private InGameButton acceptBtn, declineBtn;

	public TradePanel(int xCenter, int yCenter, GameImageMiddleMan g)
	{
		graphics = g;
		frame = new InGameFrame("Trading width: ", g);
		invGrid = new InGameGridPanel(6, 5);
		myOfferGrid = new InGameGridPanel(3, 4);
		otherOfferGrid = new InGameGridPanel(3, 4);

		middleMarginWidth = 11;
		plrTextBoxHeight = 18;
		opntTextBoxHeight = 22;
		itemInfoBarHeight = 20;
		setWidth(myOfferGrid.getWidth() + middleMarginWidth + invGrid.getWidth());
		setHeight(plrTextBoxHeight + myOfferGrid.getHeight() + otherOfferGrid.getHeight()
		+ opntTextBoxHeight + itemInfoBarHeight);
		setX(xCenter - width/2);
		setY(yCenter - height/2);
		frame.setBounds(new Rectangle(x, y, width, height));

		plrTextBox = new Rectangle(x, y, width, plrTextBoxHeight);
		myOfferGrid.setLocation(x, plrTextBox.y + plrTextBox.height);
		opntTextBox = new Rectangle(x, myOfferGrid.getY() + myOfferGrid.getHeight(),
				myOfferGrid.getWidth(), opntTextBoxHeight);

		invGrid.setLocation(myOfferGrid.getX() + myOfferGrid.getWidth() + middleMarginWidth,
				plrTextBox.y+plrTextBox.height);

		otherOfferGrid.setLocation(x, opntTextBox.y + opntTextBox.height);
		mdlMarginBar = new Rectangle(myOfferGrid.getX() + myOfferGrid.getWidth(),
				plrTextBox.y+plrTextBox.height,
				middleMarginWidth,
				myOfferGrid.getHeight() + opntTextBox.height + otherOfferGrid.getHeight());
		accptDclnBox = new Rectangle(
				invGrid.getX(),
				invGrid.getY() + invGrid.getHeight(),
				invGrid.getWidth(),
				myOfferGrid.getHeight() + opntTextBox.height + otherOfferGrid.getHeight() - invGrid.getHeight());
		itemInfoBar = new Rectangle(x, otherOfferGrid.getY() + otherOfferGrid.getHeight(),
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

	public InGameButton getAcceptButton() { return acceptBtn; }
	public InGameButton getDeclineButton() { return declineBtn; }
	public int getInvCountTextColor() { return invCountTextColor; }
	
	public InGameGridPanel getOfferGrid() { return myOfferGrid; }
	public InGameGridPanel getOtherOfferGrid() { return otherOfferGrid; }
	public InGameGridPanel getInvGrid() { return invGrid; }

	public int getPlrTextBoxX() { return plrTextBox.x; }
	public int getPlrTextBoxY() { return plrTextBox.y; }
	public int getPlrTextBoxWidth() { return plrTextBox.width; }
	public int getPlrTextBoxHeight() { return plrTextBox.height; }

	public int getOpntTextBoxX() { return opntTextBox.x; }
	public int getOpntTextBoxY() { return opntTextBox.y; }
	public int getOpntTextBoxWidth() { return opntTextBox.width; }
	public int getOpntTextBoxHeight() { return opntTextBox.height; }

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
