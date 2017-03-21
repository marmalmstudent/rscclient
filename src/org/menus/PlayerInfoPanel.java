package org.menus;

public class PlayerInfoPanel extends GamePanel
{
    private int tabHeight, scrollBoxHeight, scrollBoxTitleHeight,
    contentWidth, contentHeight;
    
    public PlayerInfoPanel(int xCenter, int yCenter)
    {
        tabHeight = 24;
        scrollBoxTitleHeight = 16;
	    contentWidth = 197;
	    contentHeight = 275;
        scrollBoxHeight = contentHeight - tabHeight - scrollBoxTitleHeight;
	    height = getTitleBarHeight() + getTopMarginHeight()
	    + getContentHeight() + getBottomMarginHeight();
	    width = getLeftMarginWidth() + getContentWidth() + getRightMarginWidth();
	    // TODO: revise these when the bottom menu is done.
        x = xCenter*2 - width - 3;//xCenter - width/2;
        y = yCenter*2 - height - 35;//yCenter - height/2;
	    closeButton = new int[]{
	    		getTitleBarX() + getTitleBarWidth() - 88,
	    		getTitleBarY(),
	    		getTitleBarX() + getTitleBarWidth(),
	    		getTitleBarY() + getTitleBarHeight()};
    }
    
    public int getTabHeight() { return tabHeight; }
    public int getScrollBoxHeight() { return scrollBoxHeight; }
    public int getScrollBoxTitleHeight() { return scrollBoxTitleHeight; }
    
    public int getContentHeight() { return contentHeight; }
    public int getContentWidth() { return contentWidth; }
    public int getContentX() { return getLeftMarginX() + getLeftMarginWidth(); }
    public int getContentY() { return getTopMarginY() + getTopMarginHeight(); }
}
