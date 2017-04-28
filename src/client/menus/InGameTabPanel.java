package client.menus;

public abstract class InGameTabPanel extends InGamePanel
{
    protected int tabHeight, scrollBoxHeight, scrollBoxTitleHeight;
    protected final int activeTabColor = 0xdcdcdc;
    protected final int inactiveTabColor = 0x909090;

    public int getTabHeight() { return tabHeight; }
    public int getScrollBoxHeight() { return scrollBoxHeight; }
    public int getScrollBoxTitleHeight() { return scrollBoxTitleHeight; }
    public int getActiveTabColor() { return activeTabColor; }
    public int getInactiveTabColor() { return inactiveTabColor; }
}
