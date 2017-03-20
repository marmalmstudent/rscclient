package org.menus;

import org.entityhandling.EntityHandler;
import org.mudclient;

public class BankWindow extends GamePanel
{
    private int nRows, nCols;
    private int topInfoBoxHeight, bottomInfoBoxHeight;
    private int[] buttonWithOne, buttonWith10, buttonWith100, buttonWith1k,
    buttonWith10k, buttonWithAll;
    private int[] buttonDepOne, buttonDep10, buttonDep100, buttonDep1k,
    buttonDep10k, buttonDepAll;
    private int [][] tabButtons;
    private int[] tab1Button, tab2Button, tab3Button, tab4Button;
    
	public BankWindow(int xCenter, int yCenter)
	{
	    nRows = 6;
	    nCols = 8;
	    topInfoBoxHeight = 17;
	    bottomInfoBoxHeight = 47;
	    height = getTitleBarHeight() + getTopMarginHeight()
	    + getTopInfoBoxHeight() + getBankGridHeight()
	    + getBottomInfoBoxHeight() + getBottomMarginHeight();
	    width = getLeftMarginWidth() + getBankGridWidth() + getRightMarginWidth();
	    x = xCenter - width / 2;
	    y = yCenter - height / 2;
	    closeButton = new int[]{
	    		getTitleBarX() + getTitleBarWidth() - 88,
	    		getTitleBarY(),
	    		getTitleBarX() + getTitleBarWidth(),
	    		getTitleBarY() + getTitleBarHeight()};
	    int xJump = 65;
	    int xStart = x + 50;
	    tab1Button = new int[] {xStart, y,
	    		xStart + xJump, y + titleBarHeight};
	    tab2Button = new int[] {xStart + xJump, y,
	    		xStart + xJump*2, y + titleBarHeight};
	    tab3Button = new int[] {xStart + xJump*2, y,
	    		xStart + xJump*3, y + titleBarHeight};
	    tab4Button = new int[] {xStart + xJump*3, y,
	    		xStart + xJump*4, y + titleBarHeight};
	    tabButtons = new int[][] {tab1Button, tab2Button, tab3Button, tab4Button};
	    buttonWithOne = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 180,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 150,
	    		getBottomInfoBoxY() + 16};
	    buttonWith10 = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 150,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 120,
	    		getBottomInfoBoxY() + 16};
	    buttonWith100 = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 120,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 95,
	    		getBottomInfoBoxY() + 16};
	    buttonWith1k = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 95,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 65,
	    		getBottomInfoBoxY() + 16};
	    buttonWith10k = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 65,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 32,
	    		getBottomInfoBoxY() + 16};
	    buttonWithAll = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 30,
	    		getBottomInfoBoxY() + 5,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth(),
	    		getBottomInfoBoxY() + 16};
	    buttonDepOne = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 180,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 150,
	    		getBottomInfoBoxY() + 41};
	    buttonDep10 = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 150,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 120,
	    		getBottomInfoBoxY() + 41};
	    buttonDep100 = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 120,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 95,
	    		getBottomInfoBoxY() + 41};
	    buttonDep1k = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 95,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 65,
	    		getBottomInfoBoxY() + 41};
	    buttonDep10k = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 65,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 32,
	    		getBottomInfoBoxY() + 41};
	    buttonDepAll = new int[]{
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth() - 30,
	    		getBottomInfoBoxY() + 30,
	    		getBottomInfoBoxX() + getBottomInfoBoxWidth(),
	    		getBottomInfoBoxY() + 41};
	}
	
	public int getWithAmt(int mouseX, int mouseY, int itemCount)
	{
		int amount = 0;
        if (itemCount >= 1
        		&& mouseX >= buttonWithOne[0]
        		&& mouseY >= buttonWithOne[1]
        		&& mouseX < buttonWithOne[2]
        		&& mouseY <= buttonWithOne[3])
        {
        	amount = 1;
        }
        else if (itemCount >= 10
        		&& mouseX >= buttonWith10[0]
        		&& mouseY >= buttonWith10[1]
        		&& mouseX < buttonWith10[2]
        		&& mouseY <= buttonWith10[3])
        {
        	amount = 10;
        }
        else if (itemCount >= 100
        		&& mouseX >= buttonWith100[0]
        		&& mouseY >= buttonWith100[1]
        		&& mouseX < buttonWith100[2]
        		&& mouseY <= buttonWith100[3])
        {
        	amount = 100;
        }
        else if (itemCount >= 1000
        		&& mouseX >= buttonWith1k[0]
        		&& mouseY >= buttonWith1k[1]
        		&& mouseX < buttonWith1k[2]
        		&& mouseY <= buttonWith1k[3])
        {
        	amount = 1000;
        }
        else if (itemCount >= 10000
        		&& mouseX >= buttonWith10k[0]
        		&& mouseY >= buttonWith10k[1]
        		&& mouseX < buttonWith10k[2]
        		&& mouseY <= buttonWith10k[3])
        {
        	amount = 10000;
        }
        else if (mouseX >= buttonWithAll[0]
        		&& mouseY >= buttonWithAll[1]
        		&& mouseX < buttonWithAll[2]
        		&& mouseY <= buttonWithAll[3])
        {
        	amount = itemCount;
        }
        return amount;
	}
	
	public int getDepAmt(int mouseX, int mouseY, int itemCount)
	{
		int amount = 0;
        if (itemCount >= 1
        		&& mouseX >= buttonDepOne[0]
        		&& mouseY >= buttonDepOne[1]
        		&& mouseX < buttonDepOne[2]
        		&& mouseY <= buttonDepOne[3])
        {
        	amount = 1;
        }
        else if (itemCount >= 10
        		&& mouseX >= buttonDep10[0]
        		&& mouseY >= buttonDep10[1]
        		&& mouseX < buttonDep10[2]
        		&& mouseY <= buttonDep10[3])
        {
        	amount = 10;
        }
        else if (itemCount >= 100
        		&& mouseX >= buttonDep100[0]
        		&& mouseY >= buttonDep100[1]
        		&& mouseX < buttonDep100[2]
        		&& mouseY <= buttonDep100[3])
        {
        	amount = 100;
        }
        else if (itemCount >= 1000
        		&& mouseX >= buttonDep1k[0]
        		&& mouseY >= buttonDep1k[1]
        		&& mouseX < buttonDep1k[2]
        		&& mouseY <= buttonDep1k[3])
        {
        	amount = 1000;
        }
        else if (itemCount >= 10000
        		&& mouseX >= buttonDep10k[0]
        		&& mouseY >= buttonDep10k[1]
        		&& mouseX < buttonDep10k[2]
        		&& mouseY <= buttonDep10k[3])
        {
        	amount = 10000;
        }
        else if (mouseX >= buttonDepAll[0]
        		&& mouseY >= buttonDepAll[1]
        		&& mouseX < buttonDepAll[2]
        		&& mouseY <= buttonDepAll[3])
        {
        	amount = itemCount;
        }
        return amount;
	}
	
	public int getTabMouseover(int mouseX, int mouseY, int nTabs)
	{
		if (mouseX < tabButtons[0][0]
				|| mouseY < tabButtons[0][1]
				|| mouseX >= tabButtons[tabButtons.length-1][2]
				|| mouseY >= tabButtons[tabButtons.length-1][3])
			return 0;
		if (nTabs > 1)
			for (int i = 0; i < nTabs; ++i)
				if (mouseX >= tabButtons[i][0]
						&& mouseY >= tabButtons[i][1]
						&& mouseX < tabButtons[i][2]
						&& mouseY < tabButtons[i][3])
					return i+1;
		return 0;
	}
	
	public int getRows() { return nRows; }
	
	public int getCols() { return nCols; }
	
	public int getTopInfoBoxHeight() { return topInfoBoxHeight; }
	public int getTopInfoBoxWidth() {
		return getWidth() - getLeftMarginWidth() - getRightMarginWidth();
	}
	public int getTopInfoBoxX() { return getLeftMarginX() + getLeftMarginWidth(); }
	public int getTopInfoBoxY() { return getTopMarginY() + getTopMarginHeight(); }
	
	public int getBankGridHeight() { return nRows*mudclient.itemSlotHeight; }
	public int getBankGridWidth() { return nCols*mudclient.itemSlotWidth; }
	public int getBankGridX() { return getLeftMarginX() + getLeftMarginWidth(); }
	public int getBankGridY() { return getTopInfoBoxY() + getTopInfoBoxHeight(); }
	
	public int getBottomInfoBoxHeight() { return bottomInfoBoxHeight; }
	public int getBottomInfoBoxWidth() { return getBankGridWidth(); }
	public int getBottomInfoBoxX() { return getLeftMarginX() + getLeftMarginWidth(); }
	public int getBottomInfoBoxY() { return getBankGridY() + getBankGridHeight(); }
	
	public int getButtonWithOneX() { return buttonWithOne[0]; }
	public int getButtonWithOneY() { return buttonWithOne[1]; }
	public int getButtonWith10X() { return buttonWith10[0]; }
	public int getButtonWith10Y() { return buttonWith10[1]; }
	public int getButtonWith100X() { return buttonWith100[0]; }
	public int getButtonWith100Y() { return buttonWith100[1]; }
	public int getButtonWith1kX() { return buttonWith1k[0]; }
	public int getButtonWith1kY() { return buttonWith1k[1]; }
	public int getButtonWith10kX() { return buttonWith10k[0]; }
	public int getButtonWith10kY() { return buttonWith10k[1]; }
	public int getButtonWithAllX() { return buttonWithAll[0]; }
	public int getButtonWithAllY() { return buttonWithAll[1]; }

	public int getButtonDepOneX() { return buttonDepOne[0]; }
	public int getButtonDepOneY() { return buttonDepOne[1]; }
	public int getButtonDep10X() { return buttonDep10[0]; }
	public int getButtonDep10Y() { return buttonDep10[1]; }
	public int getButtonDep100X() { return buttonDep100[0]; }
	public int getButtonDep100Y() { return buttonDep100[1]; }
	public int getButtonDep1kX() { return buttonDep1k[0]; }
	public int getButtonDep1kY() { return buttonDep1k[1]; }
	public int getButtonDep10kX() { return buttonDep10k[0]; }
	public int getButtonDep10kY() { return buttonDep10k[1]; }
	public int getButtonDepAllX() { return buttonDepAll[0]; }
	public int getButtonDepAllY() { return buttonDepAll[1]; }
	
	public int getTab1ButtonX() { return tab1Button[0]; }
	public int getTab1ButtonY() { return tab1Button[1]; }
	public int getTab2ButtonX() { return tab2Button[0]; }
	public int getTab2ButtonY() { return tab2Button[1]; }
	public int getTab3ButtonX() { return tab3Button[0]; }
	public int getTab3ButtonY() { return tab3Button[1]; }
	public int getTab4ButtonX() { return tab4Button[0]; }
	public int getTab4ButtonY() { return tab4Button[1]; }
	public int[][] getTabButtons() { return tabButtons; }
	public int getTabButtonPanelHeight() {
		return tabButtons[tabButtons.length-1][3] - tabButtons[0][1];
	}
	public int getTabButtonPanelWidth() {
		return tabButtons[tabButtons.length-1][2] - tabButtons[0][0];
	}
	public int getTabButtonPanelX() { return tabButtons[0][0]; }
	public int getTabButtonPanelY() { return tabButtons[0][1]; }
}
