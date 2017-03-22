package org.menus;

public class InGameButtonPanel extends InGamePanel
{
    private InGameButton[] buttons;
    private int btnHeight, btnWidth;
    
    public InGameButtonPanel(int x, int y, int btnWidth,
    		int btnHeight, int nbrButtons)
    {
    	this.x = x;
    	this.y = y;
    	this.btnWidth = btnWidth;
    	this.btnHeight = btnHeight;
    	width = btnWidth*nbrButtons;
    	height = btnHeight;
    	buttons = new InGameButton[nbrButtons];
    }
    
    public InGameButton[] getButtons() { return buttons; }
    public InGameButton getButton(int idx) { return buttons[idx]; }
    public int getNbrButtons() { return buttons.length; }
    
    public int addButton(String text)
    {
    	for (int i = 0; i < buttons.length; ++i)
    	{
    		if (buttons[i] != null)
    			continue;
    		buttons[i] = new InGameButton(
    				(i != 0) ? (buttons[i-1].getX() + buttons[i-1].getWidth()) : x,
    				y, btnWidth, btnHeight, text);
    		return i;
    	}
    	// button array is full;
    	return -1;
    }
}
