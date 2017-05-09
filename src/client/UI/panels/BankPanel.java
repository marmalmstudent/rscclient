package client.UI.panels;

import java.awt.Rectangle;

import client.GameImageMiddleMan;
import client.UI.InGameButtonPanel;
import client.UI.InGameFrame;
import client.UI.InGameGridPanel;

public class BankPanel extends InGameGridPanel
{
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
	    nRows = 6;
	    nCols = 8;
	    topInfoBoxHeight = 17;
	    bottomInfoBoxHeight = 47;
	    setHeight(getTopInfoBoxHeight() + getGridHeight() + getBottomInfoBoxHeight());
	    setWidth(getGridWidth());
	    setX(xCenter - width / 2);
	    setY(yCenter - height / 2);
	    frame.setBounds(new Rectangle(x, y, width, height));
		gridY =  getTopInfoBoxY() + getTopInfoBoxHeight();
		gridX = getX();
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
	
	
	public int getTopInfoBoxHeight() { return topInfoBoxHeight; }
	public int getTopInfoBoxWidth() { return getWidth(); }
	public int getTopInfoBoxX() { return getX(); }
	public int getTopInfoBoxY() { return getY(); }
	
	public int getBottomInfoBoxHeight() { return bottomInfoBoxHeight; }
	public int getBottomInfoBoxWidth() { return getGridWidth(); }
	public int getBottomInfoBoxX() { return getX(); }
	public int getBottomInfoBoxY() { return getGridY() + getGridHeight(); }
	public InGameButtonPanel getTabButtonPanel() { return tabBtnPanel; }
	public InGameButtonPanel getDepButtonPanel() { return depBtnPanel; }
	public InGameButtonPanel getWithButtonPanel() { return withBtnPanel; }
	
    public int getBankCountTextColor() { return bankCountTextColor; }
    public int getInvCountTextColor() { return invCountTextColor; }
}
