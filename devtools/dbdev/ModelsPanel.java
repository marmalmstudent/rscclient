package dbdev;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ModelsPanel extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JLabel placeholder1Lbl, placeholder2Lbl;
	public JTextField placeholder1Txt, placeholder2Txt;
	public static final String SIGN = "models_";
	private CacheDev cd;

	public ModelsPanel(CacheDev cd)
	{
		this.cd = cd;
		setLayout(new GridLayout(7, 2));
		
		placeholder1Lbl = new JLabel("Placeholder 1");
		placeholder1Lbl.setName(SIGN+"placeholder1lbl");
		placeholder2Lbl = new JLabel("Placeholder 2");
		placeholder2Lbl.setName(SIGN+"placeholder2lbl");
		
		placeholder1Txt = new JTextField("");
		placeholder1Txt.setName(SIGN+"placeholder1txt");
		placeholder1Txt.addActionListener(cd);
		placeholder2Txt = new JTextField("");
		placeholder2Txt.setName(SIGN+"placeholder2txt");
		placeholder2Txt.addActionListener(cd);

		add(placeholder1Lbl);
		add(placeholder1Txt);
		add(placeholder2Lbl);
		add(placeholder2Txt);
	}
	
	public void fileSelected(File f)
	{
		;
	}
	
	public void handleEvent(String bName)
	{
		if (bName.equals(placeholder1Txt.getName()))
		{
			try {
				Integer.parseInt(placeholder1Txt.getText());
				placeholder2Txt.requestFocus();
			} catch (NumberFormatException nfe) { placeholder1Txt.setText(""); }
		}
		else if (bName.equals(placeholder2Txt.getName()))
		{
			try {
				Integer.parseInt(placeholder2Txt.getText());
				placeholder1Txt.requestFocus();
			} catch (NumberFormatException nfe) { placeholder2Txt.setText(""); }
		}
	}
}
