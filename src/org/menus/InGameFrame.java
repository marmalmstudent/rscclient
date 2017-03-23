package org.menus;

import java.awt.Rectangle;

public class InGameFrame extends InGameComponent
{
	protected final int titleBarColor = 0x0000c0;
	protected final int titleBarAlpha = 0xff;
	protected final int titleTextColor = 0xffffff;
	protected final int marginBGColor = 0x0000c0;//0x989898;
	protected final int marginBGAlpha = 0xff;//0xa0;
	
    protected final int titlebarHeight = 12;
    protected final int topMarginHeight = 1;
    protected final int bottomMarginHeight = 1;
    protected final int leftMarginWidth = 1;
    protected final int rightMarginWidth = 1;
    
    protected final int closeButtonWidth = 88;
    protected final String closeBtnText = "Close window";
    protected String title;
    protected final int textLeftOffset = 1;
    protected final int textRightOffset = -2;
    protected final int textYOffset = 10;
    protected InGameButton closeButton;
    
    public InGameFrame(String title)
    {
    	this.title = title;
    }
    
    public void setBounds(Rectangle childBounds)
    {
    	x = childBounds.x - leftMarginWidth;
    	y = childBounds.y - titlebarHeight - topMarginHeight;
    	width = childBounds.width + leftMarginWidth + rightMarginWidth;
    	height = childBounds.height + titlebarHeight
    			+ topMarginHeight + bottomMarginHeight;
    	closeButton = new InGameButton(
    			x + getTitleBarWidth() - closeButtonWidth,
    			y, closeButtonWidth, getTitleBarHeight(),
    			closeBtnText);
    }
    
    public InGameButton getCloseButton() { return closeButton; }
    public int getCloseButtonColor(int mouseX, int mouseY)
    {
    	if (closeButton.isMouseOverButton(mouseX, mouseY))
    		return closeButton.getMouseOverColor();
    	return closeButton.getMouseNotOverColor();
    }
    
    public int getTitleBarColor() { return titleBarColor; }
    public int getTitleBarAlpha() { return titleBarAlpha; }
    public int getMarginBGColor() { return marginBGColor; }
    public int getMarginBGAlpha() { return marginBGAlpha; }

	public int getHorizontalMarginWidth() { return width; }
	public int getVerticalMarginHeight()
	{
		return height - titlebarHeight - topMarginHeight - bottomMarginHeight;
	}

	public int getTitleBarHeight() { return titlebarHeight; }
	public int getTitleBarWidth() { return getHorizontalMarginWidth(); }
	public int getTitleBarX() { return x; }
	public int getTitleBarY() { return y; }
	
	public int getTopMarginHeight() { return topMarginHeight; }
	public int getTopMarginWidth() { return getHorizontalMarginWidth(); }
	public int getTopMarginX() { return x; }
	public int getTopMarginY() { return getTitleBarY() + getTitleBarHeight(); }
	
	public int getBottomMarginHeight() { return bottomMarginHeight; }
	public int getBottomMarginWidth() { return getHorizontalMarginWidth(); }
	public int getBottomMarginX() { return x; }
	public int getBottomMarginY() { return y + height - bottomMarginHeight; }

	public int getLeftMarginHeight() { return getVerticalMarginHeight(); }
	public int getLeftMarginWidth() { return leftMarginWidth; }
	public int getLeftMarginX() { return x; }
	public int getLeftMarginY() { return getTopMarginY() + getTopMarginHeight(); }

	public int getRightMarginHeight() { return getVerticalMarginHeight(); }
	public int getRightMarginWidth() { return rightMarginWidth; }
	public int getRightMarginX() { return x + width - rightMarginWidth; }
	public int getRightMarginY() { return getTopMarginY() + getTopMarginHeight(); }

    public int getContentHeight()
    {
    	return height - titlebarHeight - topMarginHeight - bottomMarginHeight;
    }
    public int getContentWidth()
    {
    	return width - leftMarginWidth - rightMarginWidth;
    }
    public int getContentX() { return getLeftMarginX() + getLeftMarginWidth(); }
    public int getContentY() { return getTopMarginY() + getTopMarginHeight(); }
	
	
	public int getTitleTextColor() { return titleTextColor; }
	public String getTitleText() { return title; }
    public int getTextLeftOffset() { return textLeftOffset; }
    public int getTextRightOffset() { return textRightOffset; }
    public int getTextYOffset() { return textYOffset; }
}
