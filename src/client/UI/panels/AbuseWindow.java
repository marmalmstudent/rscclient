package client.UI.panels;
import client.DataOperations;
import client.GameImageMiddleMan;
import client.mudclient;
import client.GameWindow.MouseVariables;

public class AbuseWindow
{
	private int x, y, width, height, halfWidth, halfHeight;
	private int fontSize, selectedTypeInline;
	private final String[] info = {
			"This form is for reporting players who are breaking our rules",
			"Using it sends a snapshot of the last 60 secs of activity to us",
			"If you misuse this form, you will be banned.",
			""
	};
	private final String[] reportInfo = {
			"First indicate which of our 12 rules is being broken. For a detailed",
			"explanation of each rule please read the manual on our website."
	};
	private final String[] rules = {
			"1: Offensive language",
			"2: Item scamming",
			"3: Password scamming",
			"4: Bug abuse",
			"5: TestServer Staff impersonation",
			"6: Account sharing/trading",
			"7: Macroing",
			"8: Mutiple logging in",
			"9: Encouraging others to break rules",
			"10: Misuse of customer support",
			"11: Advertising / website",
			"12: Real world item trading",
			""
	};
	private final String[] close = {
			"Click here to cancel"
	};
	
	private static MouseVariables mv = MouseVariables.get();
    
	public AbuseWindow(int xCenter, int yCenter)
	{
		fontSize = 14;
		width = 400;
		halfWidth = width/2;
		height = (info.length + reportInfo.length
				+ rules.length + close.length)*getRowSeparation()
				+ 2*getRowSeparation()/5;
		//height = 290;
		halfHeight = height/2;
		x = xCenter - halfWidth;
		y = yCenter - halfHeight;
		selectedTypeInline = 10;  // space between window edge and selected rule box
		
	}
	
	public int getXCenter()
	{
		return x + halfWidth;
	}
	
	public int getYCenter()
	{
		return y + halfHeight;
	}
	
	public String[] getInfo()
	{
		return info;
	}
	
	public String[] getReportInfo()
	{
		return reportInfo;
	}
	
	public String[] getRules()
	{
		return rules;
	}
	
	public String[] getCloseText()
	{
		return close;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getXUpperBnd()
	{
		return x + width;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getYUpperBnd()
	{
		return y + height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getBoxX()
	{
		return x + selectedTypeInline;
	}
	
	public int getBoxXUpperBnd()
	{
		return getBoxX() + getBoxWidth();
	}
	
	public int getBoxWidth()
	{
		return 2*(halfWidth - selectedTypeInline);
	}
	
	public int getRowSeparation()
	{
		return fontSize+1;
	}
	
	public int getFirstLineY()
	{
		return y + getRowSeparation(); 
	}
	
	public int getRowYOffset()
	{
		return -4*getRowSeparation()/5;
	}
	
	public int getFirstRuleY()
	{
		return getFirstLineY()
				+ (info.length + reportInfo.length)*getRowSeparation();
	}
	
	public int getCloseY()
	{
		return getFirstRuleY() + rules.length*getRowSeparation();
	}
	
	public int getFontSize()
	{
		return fontSize;
	}
	
	/**
	 * What rule is being mouseoveered.
	 * @return The rule that is mouseovered. First rule will be 0, second 1 etc.
	 */
	public int getSelectedType()
	{
		int mouseX = mv.getX();
		int mouseY = mv.getY();
		int i = mouseX - x + selectedTypeInline;
		int j = mouseY - (getFirstRuleY() + getRowYOffset());
		if (j < 0 || j >= (rules.length-1)*getRowSeparation()
				|| mouseX < getBoxX() || mouseX > getBoxXUpperBnd())
			return -1;
		return j/getRowSeparation();
	}
	
	public boolean insideWindow()
	{
		int mouseX = mv.getX();
		int mouseY = mv.getY();
		return !(mouseX < getX() || mouseX > getXUpperBnd()
				|| mouseY < getY() || mouseY > getYUpperBnd());
	}
	
	public boolean insideCloseBtn()
	{
		int mouseX = mv.getX();
		int mouseY = mv.getY();
		return !(mouseY < getCloseY() - 4*getRowSeparation()/5
				|| mouseY > getCloseY() + getRowSeparation()/5
				|| mouseX < getBoxX()
				|| mouseX > getBoxXUpperBnd());
	}
}
