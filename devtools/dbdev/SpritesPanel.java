package dbdev;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class SpritesPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JLabel widthLbl, heightLbl, reqShiftLbl,
	xShiftLbl, yShiftLbl, totalWidthLbl, totalHeightLbl;
	public JTextField widthTxt, heightTxt, reqShiftTxt,
	xShiftTxt, yShiftTxt, totalWidthTxt, totalHeightTxt;

	protected File selectedFile;
	protected CacheDev cd;
	public static final String SIGN = "sprites_";
	
	public SpritesPanel(CacheDev cd)
	{
		this.cd = cd;
		setLayout(new GridLayout(7, 2));

		widthLbl = new JLabel("Width");
		widthLbl.setName(SIGN+"widthlbl");
		heightLbl = new JLabel("Height");
		heightLbl.setName(SIGN+"heightlbl");
		reqShiftLbl = new JLabel("Requires Shift");
		reqShiftLbl.setName(SIGN+"reqshiftlbl");
		xShiftLbl = new JLabel("X Shift");
		xShiftLbl.setName(SIGN+"xshiftlbl");
		yShiftLbl = new JLabel("Y Shift");
		yShiftLbl.setName(SIGN+"yshiftlbl");
		totalWidthLbl = new JLabel("Total Width");
		totalWidthLbl.setName(SIGN+"totalwidthlbl");
		totalHeightLbl = new JLabel("Total Height");
		totalHeightLbl.setName(SIGN+"totalheightlbl");
		
		widthTxt = new JTextField("");
		widthTxt.setName(SIGN+"widthtxt");
		widthTxt.addActionListener(cd);
		heightTxt = new JTextField("");
		heightTxt.setName(SIGN+"heighttxt");
		heightTxt.addActionListener(cd);
		reqShiftTxt = new JTextField("");
		reqShiftTxt.setName(SIGN+"reqShifttxt");
		reqShiftTxt.addActionListener(cd);
		xShiftTxt = new JTextField("");
		xShiftTxt.setName(SIGN+"xShifttxt");
		xShiftTxt.addActionListener(cd);
		yShiftTxt = new JTextField("");
		yShiftTxt.setName(SIGN+"yShifttxt");
		yShiftTxt.addActionListener(cd);
		totalWidthTxt = new JTextField("");
		totalWidthTxt.setName(SIGN+"totalwidthtxt");
		totalWidthTxt.addActionListener(cd);
		totalHeightTxt = new JTextField("");
		totalHeightTxt.setName(SIGN+"totalheighttxt");
		totalHeightTxt.addActionListener(cd);

		add(widthLbl);
		add(widthTxt);
		add(heightLbl);
		add(heightTxt);
		add(reqShiftLbl);
		add(reqShiftTxt);
		add(xShiftLbl);
		add(xShiftTxt);
		add(yShiftLbl);
		add(yShiftTxt);
		add(totalWidthLbl);
		add(totalWidthTxt);
		add(totalHeightLbl);
		add(totalHeightTxt);
	}
	
	public boolean validateEntry()
	{
		JTextField[] tf = {
				widthTxt, heightTxt, xShiftTxt,
				yShiftTxt, totalWidthTxt, totalHeightTxt
		};
		boolean valid = true;
		for (JTextField text : tf)
		{
			try
			{
				Integer.parseInt(text.getText());
			}
			catch (NumberFormatException nfe)
			{
				widthTxt.setText("");
				valid = false;
			}
		}
		if (!reqShiftTxt.getText().equalsIgnoreCase("true")
				&& !reqShiftTxt.getText().equalsIgnoreCase("false"))
		{
			reqShiftTxt.setText("");
			valid = false;
		}
		return valid;
	}
	
	public abstract void exportSprite();
	public abstract boolean importSprite();

	public boolean importSprite(SpriteHandle handle,
			String datDir, int transparentMask)
	{
		if (checkValidEntries())
		{
			cd.cdc.insertSprite(handle, datDir, selectedFile,
					Boolean.getBoolean(reqShiftTxt.getText().toLowerCase()),
					Integer.parseInt(xShiftTxt.getText()),
					Integer.parseInt(yShiftTxt.getText()),
					Integer.parseInt(totalWidthTxt.getText()),
					Integer.parseInt(totalHeightTxt.getText()),
					transparentMask);
			return true;
		}
		return false;
	}
	
	public boolean checkValidEntries()
	{
		return selectedFile != null && selectedFile.exists()
				&& isImage(selectedFile) && validateEntry();
	}
	
	public void importDatValues(File f, String datDir,
			SpriteHandle handle, int transparentMask)
	{
		File datFile = new File(datDir
				+ FileOperations.getFileName(f, ".png"));
		if (datFile.exists())
		{
			handle.newDat(datFile, transparentMask);
			Sprite sprite = handle.getSprite();
			handle.newPNG(f, sprite.getRequiresShift(),
					sprite.getXShift(), sprite.getYShift(),
					sprite.getTotalWidth(), sprite.getTotalHeight(),
					transparentMask);
		}
		else
			handle.newPNG(f, false, 0, 0, 0, 0, 0);
	}
	
	/**
	 * What happens when the user selects a file from the
	 * JFileChooser.
	 * @param f
	 */
	public abstract void fileSelected(File f);
	
	public void fileSelected(File f, String datDir,
			SpriteHandle handle, int transparentMask)
	{
		selectedFile = f;
		if (isImage(f))
		{
			importDatValues(f, datDir, handle, transparentMask);
			setSpriteText(handle.getSprite());
		}
	}
	
	public boolean isImage(File f)
	{
		return FileOperations.getFileExtension(f).equals(".png");
	}
	
	public void setSpriteText(Sprite sprite)
	{
		widthTxt.setText(Integer.toString(sprite.getWidth()));
		heightTxt.setText(Integer.toString(sprite.getHeight()));
		reqShiftTxt.setText(sprite.getRequiresShift() ? "true" : "false");
		xShiftTxt.setText(Integer.toString(sprite.getXShift()));
		yShiftTxt.setText(Integer.toString(sprite.getYShift()));
		totalWidthTxt.setText(Integer.toString(sprite.getTotalWidth()));
		totalHeightTxt.setText(Integer.toString(sprite.getTotalHeight()));
	}

	/**
	 * 
	 * @param bName
	 */
	public void handleEvent(String bName)
	{
		if (bName.equals(widthTxt.getName()))
		{
			try {
				Integer.parseInt(widthTxt.getText());
				heightTxt.requestFocus();
			} catch (NumberFormatException nfe) { widthTxt.setText(""); }
		}
		else if (bName.equals(heightTxt.getName()))
		{
			try {
				Integer.parseInt(heightTxt.getText());
				reqShiftTxt.requestFocus();
			} catch (NumberFormatException nfe) { heightTxt.setText(""); }
		}
		else if (bName.equals(reqShiftTxt.getName()))
		{
			try {
				if (reqShiftTxt.getText().equalsIgnoreCase("true")
						|| reqShiftTxt.getText().equalsIgnoreCase("false"))
					xShiftTxt.requestFocus();
				else
					throw new IllegalArgumentException();
			} catch (IllegalArgumentException iae) { reqShiftTxt.setText(""); }
		}
		else if (bName.equals(xShiftTxt.getName()))
		{
			try {
				Integer.parseInt(xShiftTxt.getText());
				yShiftTxt.requestFocus();
			} catch (NumberFormatException nfe) { xShiftTxt.setText(""); }
		}
		else if (bName.equals(yShiftTxt.getName()))
		{
			try {
				Integer.parseInt(yShiftTxt.getText());
				totalWidthTxt.requestFocus();
			} catch (NumberFormatException nfe) { yShiftTxt.setText(""); }
		}
		else if (bName.equals(totalWidthTxt.getName()))
		{
			try {
				Integer.parseInt(totalWidthTxt.getText());
				totalHeightTxt.requestFocus();
			} catch (NumberFormatException nfe) { totalWidthTxt.setText(""); }
		}
		else if (bName.equals(totalHeightTxt.getName()))
		{
			try {
				Integer.parseInt(totalHeightTxt.getText());
				widthTxt.requestFocus();
			} catch (NumberFormatException nfe) { totalHeightTxt.setText(""); }
		}
	}
}
