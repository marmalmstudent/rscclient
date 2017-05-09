package client.UI.panels;

import java.awt.Rectangle;

import client.UI.InGameButton;
import client.UI.InGameFrame;
import client.UI.InGameTabPanel;

public class MagicPanel extends InGameTabPanel
{
    public int infoBoxHeight;
    private InGameButton magicTab, prayerTab;
    
	public MagicPanel(int xCenter, int yCenter)
	{
		frame = new InGameFrame("Spells");
        tabHeight = 24;
        infoBoxHeight = 70;
        setWidth(198+30);
        setHeight(276);
        setX(xCenter*2 - width - 3);
        setY(yCenter*2 - height - 35);
    	scrollBoxHeight = height - tabHeight - infoBoxHeight;
	    frame.setBounds(new Rectangle(x, y, width, height));
	    magicTab = new InGameButton(x, y, width/2, tabHeight, "Magic");
	    prayerTab = new InGameButton(x + width/2, y, width/2, tabHeight, "Prayers");
	}
    public InGameButton getMagicButton() { return magicTab; }
    public InGameButton getPrayerButton() { return prayerTab; }
}
