package org.menus;

import org.entityhandling.EntityHandler;
import org.mudclient;

public class BankWindow extends GamePanel
{
    private int nRows, nCols;
    private int topInfoBoxHeight, bottomInfoBoxHeight;

    int topInfoBoxY = y + titleBarHeight;
    int bottomInfoBoxY = topInfoBoxY + topInfoBoxHeight
    		+ nRows*mudclient.itemSlotHeight;
    int leftMarginBoxY = topInfoBoxY + topInfoBoxHeight;
    int leftMarginBoxHeight = nRows*mudclient.itemSlotHeight;
    int rightMarginBoxHeight = nRows*mudclient.itemSlotHeight;
    int rightMarginBoxX = x + leftMarginBoxWidth
    		+ nCols*mudclient.itemSlotWidth;
    int rightMarginBoxY = topInfoBoxY + topInfoBoxHeight;
    int[] buttonWithOne, buttonWith10, buttonWith100, buttonWith1k,
    buttonWith10k, buttonWithAll;
    int[] buttonDepOne, buttonDep10, buttonDep100, buttonDep1k,
    buttonDep10k, buttonDepAll;
    
	public BankWindow(int xCenter, int yCenter)
	{
	    nRows = 6; // TODO: make this depend on frame size
	    nCols = 8; // TODO: make this depend on frame size
	    topInfoBoxHeight = 17;
	    bottomInfoBoxHeight = 47;
	    width = leftMarginBoxWidth + nCols*mudclient.itemSlotWidth
	    		+ rightMarginBoxWidth;
	    height = titleBarHeight + topInfoBoxHeight
	    		+ nRows*mudclient.itemSlotHeight + bottomInfoBoxHeight;
	    x = xCenter - width / 2;
	    y = yCenter - height / 2;
	    

	    topInfoBoxY = y + titleBarHeight;
	    bottomInfoBoxY = topInfoBoxY + topInfoBoxHeight
	    		+ nRows*mudclient.itemSlotHeight;
	    leftMarginBoxY = topInfoBoxY + topInfoBoxHeight;
	    leftMarginBoxHeight = nRows*mudclient.itemSlotHeight;
	    rightMarginBoxHeight = nRows*mudclient.itemSlotHeight;
	    rightMarginBoxX = x + leftMarginBoxWidth
	    		+ nCols*mudclient.itemSlotWidth;
	    rightMarginBoxY = topInfoBoxY + topInfoBoxHeight;
	    buttonWithOne = new int[]{
	    		x + width - 180 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - 150 - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonWith10 = new int[]{
	    		x + width - 150 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - 120 - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonWith100 = new int[]{
	    		x + width - 120 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - 95 - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonWith1k = new int[]{
	    		x + width - 95 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - 65 - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonWith10k = new int[]{
	    		x + width - 65 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - 32 - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonWithAll = new int[]{
	    		x + width - 30 - rightMarginBoxWidth, bottomInfoBoxY + 5,
	    		x + width - rightMarginBoxWidth, bottomInfoBoxY + 16};
	    buttonDepOne = new int[]{
	    		x + width - 180 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - 150 - rightMarginBoxWidth, bottomInfoBoxY + 41};
	    buttonDep10 = new int[]{
	    		x + width - 150 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - 120 - rightMarginBoxWidth, bottomInfoBoxY + 41};
	    buttonDep100 = new int[]{
	    		x + width - 120 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - 95 - rightMarginBoxWidth, bottomInfoBoxY + 41};
	    buttonDep1k = new int[]{
	    		x + width - 95 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - 65 - rightMarginBoxWidth, bottomInfoBoxY + 41};
	    buttonDep10k = new int[]{
	    		x + width - 65 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - 32 - rightMarginBoxWidth, bottomInfoBoxY + 41};
	    buttonDepAll = new int[]{
	    		x + width - 30 - rightMarginBoxWidth, bottomInfoBoxY + 30,
	    		x + width - rightMarginBoxWidth, bottomInfoBoxY + 41};
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
	
	public int getRows()
	{
		return nRows;
	}
	
	public int getCols()
	{
		return nCols;
	}
	
	public int getTopInfoBoxHeight()
	{
		return topInfoBoxHeight;
	}
	
	public int getBottomInfoBoxHeight()
	{
		return bottomInfoBoxHeight;
	}
}
