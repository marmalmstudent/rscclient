package org.menus;

public abstract class GamePanel
{
	protected final int titleBarColor = 0x0000c0;
	protected final int titleBarAlpha = 0xff;
	protected final int bgColor = 0x989898;
	protected final int bgAlpha = 0xa0;
	
    protected final int titleBarHeight = 12;
    protected final int topMarginBoxHeight = 8;
    protected final int bottomMarginBoxHeight = 8;
    protected final int leftMarginBoxWidth = 8;
    protected final int rightMarginBoxWidth = 8;
    
    protected int[] closeButton;
    
    protected int x, y, width, height;

    public int getTitleBarColor() { return titleBarColor; }
    public int getTitleBarAlpha() { return titleBarAlpha; }
    public int getBGColor() { return bgColor; }
    public int getBGAlpha() { return bgAlpha; }

	public int getHorizontalMarginWidth() { return width; }
	public int getVerticalMarginHeight()
	{
		return height - titleBarHeight - topMarginBoxHeight - bottomMarginBoxHeight;
	}

	public int getTitleBarHeight() { return titleBarHeight; }
	public int getTitleBarWidth() { return getHorizontalMarginWidth(); }
	public int getTitleBarX() { return x; }
	public int getTitleBarY() { return y; }
	
	public int getTopMarginHeight() { return topMarginBoxHeight; }
	public int getTopMarginWidth() { return getHorizontalMarginWidth(); }
	public int getTopMarginX() { return x; }
	public int getTopMarginY() { return getTitleBarY() + getTitleBarHeight(); }
	
	public int getBottomMarginHeight() { return bottomMarginBoxHeight; }
	public int getBottomMarginWidth() { return getHorizontalMarginWidth(); }
	public int getBottomMarginX() { return x; }
	public int getBottomMarginY() { return y + height - bottomMarginBoxHeight; }

	public int getLeftMarginHeight() { return getVerticalMarginHeight(); }
	public int getLeftMarginWidth() { return leftMarginBoxWidth; }
	public int getLeftMarginX() { return x; }
	public int getLeftMarginY() { return getTopMarginY() + getTopMarginHeight(); }

	public int getRightMarginHeight() { return getVerticalMarginHeight(); }
	public int getRightMarginWidth() { return rightMarginBoxWidth; }
	public int getRightMarginX() { return x + width - rightMarginBoxWidth; }
	public int getRightMarginY() { return getTopMarginY() + getTopMarginHeight(); }

	public int getCloseButtonHeight() { return closeButton[3] - closeButton[1]; }
	public int getCloseButtonWidth() { return closeButton[2] - closeButton[0]; }
	public int getCloseButtonX() { return closeButton[0]; }
	public int getCloseButtonY() { return closeButton[1]; }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getX() { return x; }
	public int getY() { return y; }
}
