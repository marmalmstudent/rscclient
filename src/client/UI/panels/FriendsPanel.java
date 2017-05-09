package client.UI.panels;

import java.awt.Rectangle;

import client.UI.InGameButton;
import client.UI.InGameFrame;
import client.UI.InGameTabPanel;

public class FriendsPanel extends InGameTabPanel
{
    private InGameButton friendsTab, ignoreTab, addFriendButton, addIgnoreButton;
    private int headerHeight;
    private final int addFriendIgnoreBtnHeight = 16;
    public static final int MAX_FRIENDS = 400;
    public static final int MAX_IGNORE = 200;
    
	public FriendsPanel(int xCenter, int yCenter)
	{
		frame = new InGameFrame("Friends");
		scrollBoxHeight = height - tabHeight - 2*headerHeight;
        tabHeight = 24;
        scrollBoxTitleHeight = 16;
        setWidth(198);
        setHeight(276);
        setX(xCenter*2 - width - 3);
        setY(yCenter*2 - height - 35);
    	scrollBoxHeight = height - tabHeight - scrollBoxTitleHeight;
	    frame.setBounds(new Rectangle(x, y, width, height));
	    friendsTab = new InGameButton(x, y, width/2, tabHeight, "Friends");
	    ignoreTab = new InGameButton(x + width/2, y, width/2, tabHeight, "Ignore");
	    addFriendButton = new InGameButton(x, y + height - addFriendIgnoreBtnHeight,
	    		width, addFriendIgnoreBtnHeight, "Click here to add a friend");
	    addIgnoreButton = new InGameButton(x, y + height - addFriendIgnoreBtnHeight,
	    		width, addFriendIgnoreBtnHeight, "Click here to add a name");
	}
    public InGameButton getFriendsButton() { return friendsTab; }
    public InGameButton getIgnoreButton() { return ignoreTab; }
    public InGameButton getFriendsAddButton() { return addFriendButton; }
    public InGameButton getIgnoreAddButton() { return addIgnoreButton; }
}
