package client.UI.panels;

import java.awt.Rectangle;

import client.GameImage;
import client.GameImageMiddleMan;
import client.UI.InGameButton;
import client.UI.InGameFrame;
import client.UI.InGameGridPanel;
import client.UI.InGamePanel;
import model.Sprite;

public class TradeConfirmPanel extends InGamePanel
{
	private InGameGridPanel invGrid, myOfferGrid, otherOfferGrid;
	private int middleMarginWidth;
	private int plrTextBoxHeight, opntTextBoxHeight, itemInfoBarHeight;
    private InGameButton acceptBtn, declineBtn;
    
	public TradeConfirmPanel(int xCenter, int yCenter, GameImageMiddleMan g)
	{
		graphics = g;
		frame = new InGameFrame("", g);
		invGrid = new InGameGridPanel(6, 5);
		myOfferGrid = new InGameGridPanel(3, 4);
		otherOfferGrid = new InGameGridPanel(3, 4);
        middleMarginWidth = 11;
        plrTextBoxHeight = 18;
        opntTextBoxHeight = 22;
        itemInfoBarHeight = 20;
        setHeight(plrTextBoxHeight + myOfferGrid.getHeight() + otherOfferGrid.getHeight()
        		+ opntTextBoxHeight + itemInfoBarHeight);
        setWidth(invGrid.getWidth()
        		+ middleMarginWidth + myOfferGrid.getWidth());
        setX(xCenter - width/2);
        setY(yCenter - height/2);
	    frame.setBounds(new Rectangle(x, y, width, height));
	}
	
	public void addDeclineButton(Sprite sprite, int index)
	{
	    Rectangle declnBtn = new Rectangle(
	    		x + 3*width/4 - (sprite.getWidth() + 2*sprite.getXShift())/2,
	    		y + height - 24,
	    		sprite.getWidth() + 2*sprite.getXShift(),
	    		sprite.getHeight() + 2*sprite.getYShift());
	    declineBtn = new InGameButton(declnBtn.x, declnBtn.y,
	    		declnBtn.width, declnBtn.height, "");	
	    declineBtn.setSprite(sprite, index);
	}
	
	public void addAcceptButton(Sprite sprite, int index)
	{
	    Rectangle accptBtn = new Rectangle(
	    		x + width/4 - (sprite.getWidth() + 2*sprite.getXShift())/2,
	    		y + height - 24,
	    		sprite.getWidth() + 2*sprite.getXShift(),
	    		sprite.getHeight() + 2*sprite.getYShift());
	    acceptBtn = new InGameButton(accptBtn.x, accptBtn.y,
	    		accptBtn.width, accptBtn.height, "");
	    acceptBtn.setSprite(sprite, index);
	}

	/*
	public int getPlrTextBoxX() { return plrTextBox.x; }
	public int getPlrTextBoxY() { return plrTextBox.y; }
	public int getPlrTextBoxWidth() { return plrTextBox.width; }
	*/
	public int getPlrTextBoxHeight() { return plrTextBoxHeight; }
	public InGameButton getAcceptButton() { return acceptBtn; }
	public InGameButton getDeclineButton() { return declineBtn; }
}
