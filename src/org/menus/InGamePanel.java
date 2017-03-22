package org.menus;

public abstract class InGamePanel extends InGameComponent
{
	protected final int bgColor = 0x989898;
	protected final int bgAlpha = 0xa0;
    protected InGameFrame frame;
	
	public int getBGColor() { return bgColor; }
	public int getBGAlpha() { return bgAlpha; }
	public InGameFrame getFrame() { return frame; }
}
