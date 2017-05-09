package client.UI.panels;

import java.awt.Rectangle;

import client.GameImageMiddleMan;
import client.mudclient;
import client.UI.InGameFrame;
import client.UI.InGameGrid;
import client.UI.InGamePanel;

public class InventoryPanel extends InGamePanel
{
    private final int invCountTextColor = 0xffff00;
    private InGameGrid invGrid;
    
    public InventoryPanel(int xCenter, int yCenter, GameImageMiddleMan g)
    {
    	graphics = g;
		frame = new InGameFrame("Inventory", g);
		invGrid = new InGameGrid(6, 5, g);
	    setHeight(invGrid.getHeight());
	    setWidth(invGrid.getWidth());
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter*2 - width - 3);
	    setY(yCenter*2 - height - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
	    invGrid.setLocation(getX(), getY());
    }
	public int getInvCountTextColor() { return invCountTextColor; }
	
	public InGameGrid getInvGrid() { return invGrid; }

}
