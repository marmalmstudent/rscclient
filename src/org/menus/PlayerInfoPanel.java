package org.menus;

import java.awt.Rectangle;

public class PlayerInfoPanel extends InGamePanel
{
    private int tabHeight, scrollBoxHeight, scrollBoxTitleHeight,
    contentWidth, contentHeight;
    
    public PlayerInfoPanel(int xCenter, int yCenter)
    {
		frame = new InGameFrame("Info");
        tabHeight = 24;
        scrollBoxTitleHeight = 16;
        scrollBoxHeight = contentHeight - tabHeight - scrollBoxTitleHeight;

	    setHeight(275);
	    setWidth(197);
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter - width / 2 - 3);
	    setY(yCenter - height / 2 - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
    }
    
    public int getTabHeight() { return tabHeight; }
    public int getScrollBoxHeight() { return scrollBoxHeight; }
    public int getScrollBoxTitleHeight() { return scrollBoxTitleHeight; }
}
