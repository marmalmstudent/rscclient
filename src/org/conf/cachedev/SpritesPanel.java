package org.conf.cachedev;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.conf.cachedev.sprites.Sprite;

public abstract class SpritesPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public JLabel widthLbl, heightLbl, reqShiftLbl,
	xShiftLbl, yShiftLbl, camAngle1Lbl, camAngle2Lbl;
	public JTextField widthTxt, heightTxt, reqShiftTxt,
	xShiftTxt, yShiftTxt, camAngle1Txt, camAngle2Txt;

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
		camAngle1Lbl = new JLabel("Camera Angle 1");
		camAngle1Lbl.setName(SIGN+"camangle1lbl");
		camAngle2Lbl = new JLabel("Camera Angle 2");
		camAngle2Lbl.setName(SIGN+"camangle2lbl");
		
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
		camAngle1Txt = new JTextField("");
		camAngle1Txt.setName(SIGN+"camAngle1txt");
		camAngle1Txt.addActionListener(cd);
		camAngle2Txt = new JTextField("");
		camAngle2Txt.setName(SIGN+"camAngle2txt");
		camAngle2Txt.addActionListener(cd);

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
		add(camAngle1Lbl);
		add(camAngle1Txt);
		add(camAngle2Lbl);
		add(camAngle2Txt);
	}
	
	public boolean validateEntry()
	{
		JTextField[] tf = {
				widthTxt, heightTxt, xShiftTxt,
				yShiftTxt, camAngle1Txt, camAngle2Txt
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
			valid = false;
		return valid;
	}
	
	public abstract void exportSprite();
	public abstract boolean importSprite();
	
	public boolean checkValidEntries()
	{
		if (selectedFile == null || !selectedFile.exists())
			return false;
		String parent = selectedFile.getParentFile().getName();
		if (parent.equals("png")) {
			if (!validateEntry())
				return false;
			return true;
		}
		return false;
	}
	
	/**
	 * What happens when the user selects a file from the
	 * JFileChooser.
	 * @param f
	 */
	public void fileSelected(File f)
	{
		selectedFile = f;
		String parent = f.getParentFile().getName();
		if (parent.equals("png")) {
			File datFile = new File(FileOperations.getFileName(f, ".png"));
			Sprite sprite;
			if (datFile.exists())
			{
				cd.cdc.getTexture().newDat(datFile);
				sprite = cd.cdc.getTexture().getSprite();
				cd.cdc.getTexture().newPNG(f, sprite.getRequiresShift(),
						sprite.getXShift(), sprite.getYShift(),
						sprite.getCameraAngle1(), sprite.getCameraAngle2());
			}
			else
				cd.cdc.getTexture().newPNG(f, false, 0, 0, 0, 0);
			sprite = cd.cdc.getTexture().getSprite();
			widthTxt.setText(Integer.toString(sprite.getWidth()));
			heightTxt.setText(Integer.toString(sprite.getHeight()));
			reqShiftTxt.setText(sprite.getRequiresShift() ? "true" : "false");
			xShiftTxt.setText(Integer.toString(sprite.getXShift()));
			yShiftTxt.setText(Integer.toString(sprite.getYShift()));
			camAngle1Txt.setText(Integer.toString(sprite.getCameraAngle1()));
			camAngle2Txt.setText(Integer.toString(sprite.getCameraAngle2()));
		}
	}

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
				camAngle1Txt.requestFocus();
			} catch (NumberFormatException nfe) { yShiftTxt.setText(""); }
		}
		else if (bName.equals(camAngle1Txt.getName()))
		{
			try {
				Integer.parseInt(camAngle1Txt.getText());
				camAngle2Txt.requestFocus();
			} catch (NumberFormatException nfe) { camAngle1Txt.setText(""); }
		}
		else if (bName.equals(camAngle2Txt.getName()))
		{
			try {
				Integer.parseInt(camAngle2Txt.getText());
				widthTxt.requestFocus();
			} catch (NumberFormatException nfe) { camAngle2Txt.setText(""); }
		}
	}
}
