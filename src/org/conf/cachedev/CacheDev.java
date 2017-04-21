package org.conf.cachedev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CacheDev extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel filePanel;
	private JTextField pathTxt;
	private JButton pathBtn;

	private JPanel sidePanel, mainPanel, mpBottom, mpCenter;
	private CacheDevSprites sprites;
	private JButton spDevInit, spSprites, spModels, spLandscapes,
	spSounds;
	private JButton mpbWorkon, mpbWorkoff, mpbImport, mpbExport;
	
	public static final String SP_SIGN = "sp_";
	public static final String FP_SIGN = "fp_";
	public static final String MPB_SIGN = "mpb_";
	private final int spButtons = 10;
	private boolean[] spSelected = new boolean[spButtons];
	
	public CacheDev()
	{
		super("Cache development utility");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/* Side panel */
		sidePanel = new JPanel(new GridLayout(10, 1));
		
		spDevInit = new JButton("Initialize\nDirectories");
		spDevInit.setName(SP_SIGN+"devinit");
		spDevInit.addActionListener(this);
		
		spSprites = new JButton("Sprites");
		spSprites.setName(SP_SIGN+"sprites");
		spSprites.addActionListener(this);
		
		spModels = new JButton("Models");
		spModels.setName(SP_SIGN+"models");
		spModels.addActionListener(this);
		
		spLandscapes = new JButton("Landscapes");
		spLandscapes.setName(SP_SIGN+"landscapes");
		spLandscapes.addActionListener(this);
		
		spSounds = new JButton("Sounds");
		spSounds.setName(SP_SIGN+"sounds");
		spSounds.addActionListener(this);
		
		sidePanel.add(spDevInit);
		sidePanel.add(spSprites);
		sidePanel.add(spModels);
		sidePanel.add(spLandscapes);
		sidePanel.add(spSounds);
		add(sidePanel, BorderLayout.WEST);
		
		mainPanel = new JPanel(new BorderLayout());

		filePanel = new JPanel(new BorderLayout());
		pathTxt = new JTextField("");
		pathTxt.setName(FP_SIGN+"pathtxt");
		pathTxt.addActionListener(this);
		pathTxt.setEditable(false);
		pathTxt.setBackground(Color.WHITE);
		pathBtn = new JButton("Browse");
		pathBtn.setName(FP_SIGN+"bathbtn");
		pathBtn.addActionListener(this);
		filePanel.add(pathTxt, BorderLayout.CENTER);
		filePanel.add(pathBtn, BorderLayout.EAST);
		mainPanel.add(filePanel, BorderLayout.NORTH);

		sprites = new CacheDevSprites(this);
		mpCenter = new JPanel(new BorderLayout());
		mainPanel.add(mpCenter, BorderLayout.CENTER);
		
		mpBottom = new JPanel(new GridLayout(1,4));
		
		mpbWorkon = new JButton("Load Cache");
		mpbWorkon.setName(MPB_SIGN+"workon");
		mpbWorkon.addActionListener(this);
		
		mpbWorkoff = new JButton("Save Cache");
		mpbWorkoff.setName(MPB_SIGN+"workff");
		mpbWorkoff.addActionListener(this);
		
		mpbImport = new JButton("Import Entry");
		mpbImport.setName(MPB_SIGN+"import");
		mpbImport.addActionListener(this);
		
		mpbExport = new JButton("Export Entry");
		mpbExport.setName(MPB_SIGN+"export");
		mpbExport.addActionListener(this);
		
		mpBottom.add(mpbWorkon);
		mpBottom.add(mpbWorkoff);
		mpBottom.add(mpbImport);
		mpBottom.add(mpbExport);
		mainPanel.add(mpBottom, BorderLayout.SOUTH);

		add(mainPanel, BorderLayout.CENTER);
		
		setResizable(false);
		setVisible(true);
		pack();
	}
	
	public static void main(String args[])
	{
		new CacheDev();
	}
	
	public void handleSPEvents(ActionEvent e)
	{
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() instanceof JButton)
		{
			JButton b = (JButton) e.getSource();
			String bName = b.getName();
			if (bName.startsWith(SP_SIGN))
			{ // side panel
				if (bName.equals(spDevInit.getName()))
				{
					if (!spSelected[0])
						for (int i = 0; i < spButtons; ++i)
							spSelected[i] = false;
					spSelected[0] = true;
				}
				else if (bName.equals(spSprites.getName()))
				{
					if (!spSelected[1])
					{
						for (int i = 0; i < spButtons; ++i)
							spSelected[i] = false;
						mpCenter.removeAll();
						mpCenter.add(sprites, BorderLayout.CENTER);
						mpCenter.revalidate();
					}
					spSelected[1] = true;
				}
				else if (bName.equals(spModels.getName()))
				{
					if (!spSelected[2])
						for (int i = 0; i < spButtons; ++i)
							spSelected[i] = false;
					spSelected[2] = true;	
				}
				else if (bName.equals(spLandscapes.getName()))
				{
					if (!spSelected[3])
						for (int i = 0; i < spButtons; ++i)
							spSelected[i] = false;
					spSelected[3] = true;
				}
				else if (bName.equals(spSounds.getName()))
				{
					if (!spSelected[4])
						for (int i = 0; i < spButtons; ++i)
							spSelected[i] = false;
					spSelected[4] = true;
				}
			}
			else if (bName.startsWith(MPB_SIGN))
			{ // main panel bottom
			}
			else if (bName.startsWith(FP_SIGN))
			{ // file panel
				JFileChooser c = new JFileChooser(Main.devFolder);
				// Demonstrate "Open" dialog:
				int rVal = c.showOpenDialog(new JFrame());
				if (rVal == JFileChooser.APPROVE_OPTION) {
					String str = c.getCurrentDirectory().toString()
							+ "/" + c.getSelectedFile().getName();
					String[] pth = str.split("/");
					int rootDir = 0;
					for (int i = 0; i < pth.length; ++i)
						if (pth[i].equals("src"))
							rootDir = i;
					StringBuilder sb = new StringBuilder();
					int i;
					for (i = rootDir; i < pth.length-1; sb.append(pth[i++]+"/"));
					sb.append(pth[i]);
					String path = sb.toString();
					pathTxt.setText(path);
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					pathTxt.setText("");
				}
			}
		}
		else if (e.getSource() instanceof JTextField)
		{
			JTextField t = (JTextField) e.getSource();
			String tName = t.getName();
			if (tName.startsWith(CacheDevSprites.SIGN))
			{ // sprite panel
				sprites.handleEvent(tName);
			}
		}
	}
}
