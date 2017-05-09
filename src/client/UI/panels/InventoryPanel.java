package client.UI.panels;

import java.awt.Rectangle;

import client.GameImageMiddleMan;
import client.UI.InGameFrame;
import client.UI.InGameGridPanel;
import client.UI.InGamePanel;

public class InventoryPanel extends InGamePanel
{
    private final int invCountTextColor = 0xffff00;
    private InGameGridPanel invGrid;
    
    public InventoryPanel(int xCenter, int yCenter, GameImageMiddleMan g)
    {
    	graphics = g;
		frame = new InGameFrame("Inventory", g);
		invGrid = new InGameGridPanel(6, 5);
	    setHeight(invGrid.getHeight());
	    setWidth(invGrid.getWidth());
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter*2 - width - 3);
	    setY(yCenter*2 - height - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
	    invGrid.setLocation(getX(), getY());
    }
	public int getInvCountTextColor() { return invCountTextColor; }
	
	public InGameGridPanel getInvGrid() { return invGrid; }
	
	public boolean isMouseOverInvGrid(int mouseX, int mouseY)
	{
		return invGrid.isMouseOver(mouseX, mouseY);
	}
}
