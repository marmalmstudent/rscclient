package client.menus;

public class InGameButtonPanel extends InGamePanel
{
    private InGameButton[] buttons;
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
    	buttons = new InGameButton[nbrButtons];
    }
    
    public InGameButton[] getButtons() { return buttons; }
    public InGameButton getButton(int idx) { return buttons[idx]; }
    public int getNbrButtons() { return buttons.length; }
    
    public int addButton(String text)
    {
    	int row, col;
    	for (int i = 0; i < buttons.length; ++i)
    	{
    		if (buttons[i] != null)
    			continue;
    		row = i / nbrCols;
    		col = i % nbrCols;
    		buttons[i] = new InGameButton(
    				(col != 0) ? (buttons[row*nbrCols+(col-1)].getX() + buttons[row*nbrCols+(col-1)].getWidth()) : x,
    				(row != 0) ? (buttons[(row-1)*nbrCols+col].getY() + buttons[(row-1)*nbrCols+col].getHeight()) : y,
    				btnWidth, btnHeight, text);
    		return i;
    	}
    	// button array is full;
    	return -1;
    }
}
