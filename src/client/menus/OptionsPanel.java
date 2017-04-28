package client.menus;

import java.awt.Rectangle;

public class OptionsPanel extends InGamePanel
{
	private int headerHeight;
	private InGameButtonPanel gameOptions, clientAssists, privacySettings;
	private InGameButton logoutButton;
	
	private String[][] gameOptionsText = {
			{"Manual", "Auto"},
			{"One", "Two"},
			{"off", "on"}
	};
	private String[][] clientAssistsText = {
			{"off", "on"},
			{"off", "on"},
			{"off", "on"},
			{"off", "on"},
			{"off", "on"}
	};
	private String[][] privacySettingsText = {
			{"<off>", "<on>"},
			{"<off>", "<on>"},
			{"<off>", "<on>"},
			{"<off>", "<on>"}
	};
	public OptionsPanel(int xCenter, int yCenter)
	{
		frame = new InGameFrame("Options");
        setWidth(197);
        setHeight(276);
        setX(xCenter*2 - width - 3);
        setY(yCenter*2 - height - 35);
	    headerHeight = 15;

	    int buttonWidth = width;
	    int buttonHeight = 15;
	    int nButtons = gameOptionsText.length;
	    gameOptions = new InGameButtonPanel(
	    		x, y + headerHeight,
	    		buttonWidth, buttonHeight, nButtons, 1);
	    for (int i = 0; i < nButtons; ++i)
	    	gameOptions.addButton(gameOptionsText[i][0]);
	    
	    nButtons = clientAssistsText.length;
	    clientAssists = new InGameButtonPanel(
	    		x, y + 2*headerHeight + gameOptions.getHeight(),
	    		buttonWidth, buttonHeight, nButtons, 1);
	    for (int i = 0; i < nButtons; ++i)
	    	clientAssists.addButton(clientAssistsText[i][0]);
	    
	    nButtons = privacySettingsText.length;
	    privacySettings = new InGameButtonPanel(
	    		x, y + 4*headerHeight + gameOptions.getHeight() + clientAssists.getHeight(),
	    		buttonWidth, buttonHeight, nButtons, 1);
	    for (int i = 0; i < nButtons; ++i)
	    	privacySettings.addButton(privacySettingsText[i][0]);
	    logoutButton = new InGameButton(x,
	    		privacySettings.getY() + privacySettings.getHeight() + headerHeight,
	    		width, buttonHeight, "Click here to logout");
	    frame.setBounds(new Rectangle(x, y, width, height));
	}
	
	public void setGameOptionState(int index, boolean isSet)
	{
		String newText;
		if (isSet)
			newText = gameOptionsText[index][1];
		else
			newText = gameOptionsText[index][0];
		gameOptions.getButtons()[index].setButtonText(newText);
	}
	
	public void setClientAssistState(int index, boolean isSet)
	{
		String newText;
		if (isSet)
			newText = clientAssistsText[index][1];
		else
			newText = clientAssistsText[index][0];
		clientAssists.getButtons()[index].setButtonText(newText);
	}
	
	public void setPrivacySettingsState(int index, boolean isSet)
	{
		String newText;
		if (isSet)
			newText = privacySettingsText[index][1];
		else
			newText = privacySettingsText[index][0];
		privacySettings.getButtons()[index].setButtonText(newText);
	}
	
	public InGameButtonPanel getGameOptions() { return gameOptions; }
	public InGameButtonPanel getClientAssists() { return clientAssists; }
	public InGameButtonPanel getPrivacySettings() { return privacySettings; }
	public InGameButton getLogoutButton() { return logoutButton; }
	public int getHeaderHeight() { return headerHeight; }
}
