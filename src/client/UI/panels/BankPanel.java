package client.UI.panels;

import java.awt.Rectangle;

import client.GameImageMiddleMan;
import client.mudclient;
import client.UI.InGameButton;
import client.UI.InGameButtonPanel;
import client.UI.InGameFrame;
import client.UI.InGameGrid;
import client.UI.InGamePanel;
import entityhandling.EntityHandler;
import model.Item;

public class BankPanel extends InGamePanel
{
    private InGameGrid bankGrid;
    private int topInfoBoxHeight, bottomInfoBoxHeight;
    private final int bankCountTextColor = 0x00ff00;
    private final int invCountTextColor = 0x00ffff;
    private final String[] tabButtonText = {
    		"<page 1>", "<page 2>", "<page 3>", "<page 4>"
    }; 
    private final String[] withDepButtonText = {
    		"One", "10", "100", "1k", "10k", "All"
    };
    private InGameButtonPanel tabBtnPanel, depBtnPanel, withBtnPanel;
    
	public BankPanel(int xCenter, int yCenter, GameImageMiddleMan g)
	{
		graphics = g;
		frame = new InGameFrame("Bank", g);
		bankGrid = new InGameGrid(6, 8, g);
	    topInfoBoxHeight = 17;
	    bottomInfoBoxHeight = 47;
	    setHeight(getTopInfoBoxHeight() + bankGrid.getHeight() + getBottomInfoBoxHeight());
	    setWidth(bankGrid.getWidth());
	    setX(xCenter - width / 2);
	    setY(yCenter - height / 2);
	    frame.setBounds(new Rectangle(x, y, width, height));
	    bankGrid.setLocation(getX(), getTopInfoBoxY() + getTopInfoBoxHeight());
	    int buttonWidth = 65;
	    int buttonHeight = frame.getTitleBarHeight();
	    int nButtons = tabButtonText.length;
	    tabBtnPanel = new InGameButtonPanel(
	    		frame.getX() + 50,
	    		frame.getY(), buttonWidth, buttonHeight,
	    		tabButtonText.length, tabButtonText.length);
	    for (int i = 0; i < nButtons; ++i)
	    	tabBtnPanel.addButton(tabButtonText[i]);
	    
	    buttonWidth = 30;
	    buttonHeight = 11;
	    nButtons = withDepButtonText.length;
	    int yOffset = 5;
	    withBtnPanel = new InGameButtonPanel(
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - nButtons*buttonWidth,
	    		getBottomInfoBoxY() + yOffset, buttonWidth, buttonHeight,
	    		withDepButtonText.length, withDepButtonText.length);
	    for (int i = 0; i < nButtons; ++i)
	    	withBtnPanel.addButton(withDepButtonText[i]);
	    
	    yOffset += 25;
	    depBtnPanel = new InGameButtonPanel(
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - nButtons*buttonWidth,
	    		getBottomInfoBoxY() + yOffset, buttonWidth, buttonHeight,
	    		withDepButtonText.length, withDepButtonText.length);
	    for (int i = 0; i < nButtons; ++i)
	    	depBtnPanel.addButton(withDepButtonText[i]);
	}
	
	public int getWithAmt(int mouseX, int mouseY, int itemCount)
	{
		int count = 1;
		for (int i = 0; i < withBtnPanel.getNbrButtons()-1; ++i)
		{
	        if (withBtnPanel.getButton(i).isMouseOverButton(mouseX, mouseY))
	        	return itemCount >= count ? count : 0;
	        count *= 10;
		}
		if (withBtnPanel.getButton(withBtnPanel.getNbrButtons()-1).isMouseOverButton(mouseX, mouseY))
        	return itemCount;
        return 0;
	}
	
	public int getDepAmt(int mouseX, int mouseY, int itemCount)
	{
		int count = 1;
		for (int i = 0; i < depBtnPanel.getNbrButtons()-1; ++i)
		{
	        if (depBtnPanel.getButton(i).isMouseOverButton(mouseX, mouseY))
	        	return itemCount >= count ? count : 0;
	        count *= 10;
		}
		if (depBtnPanel.getButton(depBtnPanel.getNbrButtons()-1).isMouseOverButton(mouseX, mouseY))
        	return itemCount;
        return 0;
	}
	
	public int getTabMouseover(int mouseX, int mouseY, int nTabs)
	{
		if (!tabBtnPanel.isMouseOver(mouseX, mouseY))
			return 0;
		if (nTabs > 1)
			for (int i = 0; i < nTabs; ++i)
				if (tabBtnPanel.getButton(i).isMouseOverButton(mouseX, mouseY))
					return i+1;
		return 0;
	}

	public InGameGrid getBankGrid() { return bankGrid; }
	
	public int getTopInfoBoxHeight() { return topInfoBoxHeight; }
	public int getTopInfoBoxWidth() { return getWidth(); }
	public int getTopInfoBoxX() { return getX(); }
	public int getTopInfoBoxY() { return getY(); }
	
	public int getBottomInfoBoxHeight() { return bottomInfoBoxHeight; }
	public int getBottomInfoBoxWidth() { return bankGrid.getWidth(); }
	public int getBottomInfoBoxX() { return getX(); }
	public int getBottomInfoBoxY() { return bankGrid.getY() + bankGrid.getHeight(); }
	public InGameButtonPanel getTabButtonPanel() { return tabBtnPanel; }
	public InGameButtonPanel getDepButtonPanel() { return depBtnPanel; }
	public InGameButtonPanel getWithButtonPanel() { return withBtnPanel; }
	
    public int getBankCountTextColor() { return bankCountTextColor; }
    public int getInvCountTextColor() { return invCountTextColor; }
    
	public void drawBankInfo()
	{
		graphics.drawBoxAlpha(
				getTopInfoBoxX(), getTopInfoBoxY(),
				getTopInfoBoxWidth(), getTopInfoBoxHeight(),
				getBGColor(), getBGAlpha());
		graphics.drawString("Number in bank in green",
				getTopInfoBoxX(),
				getTopInfoBoxY() + 12, 1, 0x00ff00);
		graphics.drawString("Number held in blue",
				getTopInfoBoxX() + getTopInfoBoxWidth() - 111,
				getTopInfoBoxY() + 12, 1, 0x00ffff);
	}
	
	public void drawBankDepWithPanel(Item[] bankItems, Item[] invItems,
			int selectedBankItemIdx, int mouseX, int mouseY)
	{
		graphics.drawBoxAlpha(
				getBottomInfoBoxX(), getBottomInfoBoxY(),
				getBottomInfoBoxWidth(), getBottomInfoBoxHeight(),
				getBGColor(), getBGAlpha());
		graphics.drawLineX(
				getBottomInfoBoxX(),
				getBottomInfoBoxY() + getBottomInfoBoxHeight()/2,
				getBottomInfoBoxWidth(), 0);
		if (selectedBankItemIdx == -1)
		{
			graphics.drawText("Select an object to withdraw or deposit",
					getBottomInfoBoxX() + getBottomInfoBoxWidth()/2,
					getBottomInfoBoxY() + 15, 3, 0xffff00);
			return;
		}
		int selectedBankItemId;
		if (selectedBankItemIdx < 0)
			selectedBankItemId = -1;
		else
			selectedBankItemId = bankItems[selectedBankItemIdx].id;
		if (selectedBankItemId != -1) {
			int selectedBankItemCount = bankItems[selectedBankItemIdx].amount;
			if (selectedBankItemCount > 0) {
				graphics.drawString("Withdraw " + EntityHandler.getItemDef(selectedBankItemId).getName(),
						getBottomInfoBoxX() + 2, getBottomInfoBoxY() + 15, 1, 0xffffff);
				drawBankWithText(getWithButtonPanel(), selectedBankItemCount,
						mouseX, mouseY);
			}
			if (mudclient.itemCount(bankItems[selectedBankItemIdx], invItems) > 0) {
				graphics.drawString("Deposit " + EntityHandler.getItemDef(selectedBankItemId).getName(),
						getBottomInfoBoxX() + 2, getBottomInfoBoxY() + 40, 1, 0xffffff);
				drawBankDepText(getDepButtonPanel(), bankItems, invItems,
						selectedBankItemIdx, mouseX, mouseY);
			}
		}
	}

	public void drawBankWithText(InGameButtonPanel btnPan,
			int selectedBankItemCount, int mouseX, int mouseY)
	{
		int yOffset = 10;

		InGameButton button;
		int count = 1;
		for (int i = 0; i < btnPan.getNbrButtons()-1; ++i)
		{
			button = btnPan.getButton(i); 
			if (selectedBankItemCount >= count)
			{
				if (button.isMouseOverButton(mouseX, mouseY))
					graphics.drawString(
							button.getButtonText(), button.getX() + 2,
							button.getY() + yOffset, 1,
							button.getMouseOverColor());
				else

					graphics.drawString(
							button.getButtonText(), button.getX() + 2,
							button.getY() + yOffset, 1,
							button.getMouseNotOverColor());
			}

			count *= 10;
		}
		button = btnPan.getButton(
				btnPan.getNbrButtons()-1);
		if (button.isMouseOverButton(mouseX, mouseY))
			graphics.drawString(
					button.getButtonText(), button.getX() + 2,
					button.getY() + yOffset, 1,
					button.getMouseOverColor());
		else
			graphics.drawString(
					button.getButtonText(), button.getX() + 2,
					button.getY() + yOffset, 1,
					button.getMouseNotOverColor());
	}

	public void drawBankDepText(InGameButtonPanel btnPan,
			Item[] bankItems, Item[] invItems,
			int selectedBankItemIdx, int mouseX, int mouseY)
	{
		int yOffset = 10;

		InGameButton button;
		int count = 1;
		for (int i = 0; i < btnPan.getNbrButtons()-1; ++i)
		{
			button = btnPan.getButton(i);
			if (mudclient.itemCount(bankItems[selectedBankItemIdx], invItems) >= count)
			{
				if (button.isMouseOverButton(mouseX, mouseY))
					graphics.drawString(
							button.getButtonText(), button.getX() + 2,
							button.getY() + yOffset, 1,
							button.getMouseOverColor());
				else

					graphics.drawString(
							button.getButtonText(), button.getX() + 2,
							button.getY() + yOffset, 1,
							button.getMouseNotOverColor());
			}
			count *= 10;
		}
		button = btnPan.getButton(
				btnPan.getNbrButtons()-1);
		if (button.isMouseOverButton(mouseX, mouseY))
			graphics.drawString(
					button.getButtonText(), button.getX() + 2,
					button.getY() + yOffset, 1,
					button.getMouseOverColor());
		else
			graphics.drawString(
					button.getButtonText(), button.getX() + 2,
					button.getY() + yOffset, 1,
					button.getMouseNotOverColor());
	}
}
