package client.UI.panels;

import java.awt.Point;
import java.awt.Rectangle;

import client.GameImageMiddleMan;
import client.UI.InGameButton;
import client.UI.InGameFrame;
import client.UI.InGameTabPanel;

public class MagicPanel extends InGameTabPanel
{
    public int infoBoxHeight;
    private InGameButton magicTab, prayerTab;
    
	public MagicPanel(Point center, GameImageMiddleMan g)
	{
		graphics = g;
		frame = new InGameFrame("Spells", g);
        tabHeight = 24;
        infoBoxHeight = 70;
        setWidth(198+30);
        setHeight(276);
        setX(center.x*2 - width - 3);
        setY(center.y*2 - height - 35);
    	scrollBoxHeight = height - tabHeight - infoBoxHeight;
	    frame.setBounds(new Rectangle(x, y, width, height));
	    magicTab = new InGameButton(x, y, width/2, tabHeight, "Magic");
	    prayerTab = new InGameButton(x + width/2, y, width/2, tabHeight, "Prayers");
	}
    public InGameButton getMagicButton() { return magicTab; }
    public InGameButton getPrayerButton() { return prayerTab; }
}
