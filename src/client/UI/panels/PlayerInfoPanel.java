package client.UI.panels;

import java.awt.Rectangle;

import client.UI.InGameButton;
import client.UI.InGameButtonPanel;
import client.UI.InGameFrame;
import client.UI.InGameTabPanel;

public class PlayerInfoPanel extends InGameTabPanel
{
    private InGameButton statTab, questTab;
    private InGameButtonPanel statsBtnPanel, equipmentBtnPanel;
    private final String skillText[] = {
    		"Attack", "Fletching",
    		"Defense", "Fishing",
    		"Strength", "Firemaking",
    		"Hits", "Crafting",
    		"Ranged", "Smithing",
    		"Prayer", "Mining",
    		"Magic", "Herblaw",
    		"Cooking", "Agility",
    		"Woodcut", "Thieving"
    };
    private final String equipmentStatusText[] = {
    		"Armour", "Magic",
    		"WeaponAim", "Prayer",
    		"WeaponPower", "Range"
    };
    private int headerHeight;
    private final int nbrSkillCols = 2;
    
    public PlayerInfoPanel(int xCenter, int yCenter)
    {
		frame = new InGameFrame("Info");
        tabHeight = 24;
        scrollBoxTitleHeight = 16;

	    setWidth(198+30);
	    setHeight(276+6);
	    // TODO: revise these when the bottom menu is done.
	    setX(xCenter*2 - width - 3);
	    setY(yCenter*2 - height - 35);
	    frame.setBounds(new Rectangle(x, y, width, height));
	    headerHeight = 26;
	    statTab = new InGameButton(x, y, width/2, tabHeight, "Stats");
	    questTab = new InGameButton(x + width/2, y, width/2, tabHeight, "Quests");

	    int buttonWidth = width/2;
	    int buttonHeight = 13;
	    int nButtons = skillText.length;
	    statsBtnPanel = new InGameButtonPanel(
	    		x, y + tabHeight + headerHeight,
	    		buttonWidth, buttonHeight, nButtons, 2);
	    for (int i = 0; i < nButtons; ++i)
	    	statsBtnPanel.addButton(skillText[i]);
	    nButtons = equipmentStatusText.length;
	    equipmentBtnPanel = new InGameButtonPanel(
	    		x, y + tabHeight + 2*headerHeight + statsBtnPanel.getHeight(),
	    		buttonWidth, buttonHeight, nButtons, 2);
	    for (int i = 0; i < nButtons; ++i)
	    	equipmentBtnPanel.addButton(equipmentStatusText[i]);
        scrollBoxHeight = height - tabHeight - scrollBoxTitleHeight;
    }
	
	public int getCorrectedSkillIndex(int i)
	{
		int rows = i / nbrSkillCols;
		int cols = i % nbrSkillCols;
		System.out.printf("%d, %d, %d\n", i, rows, cols);
		int colFactor = skillText.length/nbrSkillCols
				+ (skillText.length % nbrSkillCols == 0 ? 0 : 1);
		return cols*colFactor + rows;
	}
    
    public int getHeaderHeight() { return headerHeight; }
    public InGameButton getStatButton() { return statTab; }
    public InGameButton getQuestButton() { return questTab; }

	public InGameButtonPanel getStatButtonPanel() { return statsBtnPanel; }
	public InGameButtonPanel getEquipmentButtonPanel() { return equipmentBtnPanel; }
}
