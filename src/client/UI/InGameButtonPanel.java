package client.UI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InGameButtonPanel extends InGamePanel
{
    private List<InGameButton> buttons;
    private int btnHeight, btnWidth, nbrCols;
    
    public InGameButtonPanel(int x, int y, int btnWidth,
    		int btnHeight, int nbrButtons, int nbrCols)
    {
    	this.x = x;
    	this.y = y;
    	this.btnWidth = btnWidth;
    	this.btnHeight = btnHeight;
    	this.nbrCols = nbrCols;
    	width = btnWidth*nbrButtons;
    	height = btnHeight*(nbrButtons/nbrCols + (nbrButtons % nbrCols == 0 ? 0 : 1));
    	buttons = new ArrayList<InGameButton>(nbrButtons);
    }
    
    public List<InGameButton> getButtons() { return buttons; }
    public InGameButton getButton(int idx) { return buttons.get(idx); }
    public int getNbrButtons() { return buttons.size(); }
    
    public int addButton(String text)
    {
    	int btnid = buttons.size();
    	int row = btnid / nbrCols;
    	int col = btnid % nbrCols;
    	buttons.add(new InGameButton(x + col*btnWidth, y + row*btnHeight,
				btnWidth, btnHeight, text));
    	return btnid;
    }
}
