package client.menus;

public abstract class InGamePanel extends InGameComponent
{
	protected final int bgColor = 0xc0c0c0;
	protected final int bgAlpha = 0xa0;
	protected final int lineColor = 0x000000;
    protected InGameFrame frame;
	
	public int getBGColor() { return bgColor; }
	public int getBGAlpha() { return bgAlpha; }
	public int getLineColor() { return lineColor; }
	public InGameFrame getFrame() { return frame; }
}
