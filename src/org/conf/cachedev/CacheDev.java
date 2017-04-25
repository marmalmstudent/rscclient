package org.conf.cachedev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CacheDev extends JFrame implements ActionListener, ChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel filePanel;
	private JTextField pathTxt;
	private JButton pathBtn;

	private JPanel mainPanel, mpBottom, mpbButtons, mpbProgress;
	private JTabbedPane mpCenter;
	private JProgressBar progress;
	private SpritesPanel sprites;
	private ModelsPanel models;
	private SoundsPanel sounds;
	private JButton spSprites, spModels, spLandscapes,
	spSounds;
	private JButton mpbWorkon, mpbWorkoff, mpbImport, mpbExport;
	
	public static final String FP_SIGN = "fp_";
	public static final String MPB_SIGN = "mpb_";
	private final int spButtons = 10;
	private boolean[] spSelected = new boolean[spButtons];
	public CDControl cdc;
	
	public CacheDev()
	{
		super("Cache development utility");
		cdc = new CDControl();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel(new BorderLayout());
		/* */
		mpCenter = new JTabbedPane();
		mpCenter.addChangeListener(this);
        
		sprites = new SpritesPanel(this);
		mpCenter.addTab(CDConstants.DB_NAMES[CDConstants.SPRITES_ID], sprites);
		models = new ModelsPanel(this);
		mpCenter.addTab(CDConstants.DB_NAMES[CDConstants.MODELS_ID], models);
		sounds = new SoundsPanel(this);
		mpCenter.addTab(CDConstants.DB_NAMES[CDConstants.SOUNDS_ID], sounds);
	
		mpCenter.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		/* */

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

		mainPanel.add(mpCenter, BorderLayout.CENTER);
		
		mpBottom = new JPanel(new BorderLayout());
		mpbButtons = new JPanel(new GridLayout(1,4));
		mpbProgress = new JPanel(new GridLayout(1,1));
		
		mpbWorkon = new JButton("Load Cache");
		mpbWorkon.setName(MPB_SIGN+"workon");
		mpbWorkon.addActionListener(this);
		
		mpbWorkoff = new JButton("Save Cache");
		mpbWorkoff.setName(MPB_SIGN+"workff");
		mpbWorkoff.addActionListener(this);
		
		mpbImport = new JButton("Import Entry");
		mpbImport.setName(MPB_SIGN+"import");
		mpbImport.addActionListener(this);
		
		mpbExport = new JButton("Export All");
		mpbExport.setName(MPB_SIGN+"export");
		mpbExport.addActionListener(this);
		
		mpbButtons.add(mpbWorkon);
		mpbButtons.add(mpbWorkoff);
		mpbButtons.add(mpbImport);
		mpbButtons.add(mpbExport);
		
		progress = new JProgressBar();
		progress.setString("Ready when moving!");
		progress.setStringPainted(true);
		progress.setIndeterminate(true);
        mpbProgress.add(progress);
        
        mpBottom.add(mpbButtons, BorderLayout.NORTH);
        mpBottom.add(mpbProgress, BorderLayout.SOUTH);
		
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
	
	/**
	 * Mainpanel bottom bar events.
	 * @param e
	 */
	private void handleMPBEvents(JButton b)
	{
		String bName = b.getName();
		if (bName.equals(mpbWorkon.getName()))
		{ // unzip currently selected database
			if (spSelected[CDConstants.SPRITES_ID]) {
				cdc.workonSprites();
			} else if (spSelected[CDConstants.MODELS_ID]) {
				cdc.workonModels();
			} else if (spSelected[CDConstants.LANDSCAPES_ID]) {
				; // TODO
			} else if (spSelected[CDConstants.SOUNDS_ID]) {
				cdc.workonSounds();
			}
		}
		else if (bName.equals(mpbWorkoff.getName()))
		{ // zip currently selected database
			if (spSelected[CDConstants.SPRITES_ID]) {
				cdc.workoffSprites();
			} else if (spSelected[CDConstants.MODELS_ID]) {
				cdc.workoffModels();
			} else if (spSelected[CDConstants.LANDSCAPES_ID]) {
				; // TODO
			} else if (spSelected[CDConstants.SOUNDS_ID]) {
				cdc.workoffSounds();
			}
		}
		else if (bName.equals(mpbImport.getName()))
		{ // convert exported file
			if (spSelected[CDConstants.SPRITES_ID])
				; // TODO
			else if (spSelected[CDConstants.MODELS_ID])
				; // TODO
			else if (spSelected[CDConstants.LANDSCAPES_ID])
				; // TODO
			else if (spSelected[CDConstants.SOUNDS_ID])
				cdc.insertSounds();
		}
		else if (bName.equals(mpbExport.getName()))
		{ // convert imported file
			if (spSelected[CDConstants.SPRITES_ID])
				cdc.extractSprites();
			else if (spSelected[CDConstants.MODELS_ID])
				cdc.extractModels();
			else if (spSelected[CDConstants.LANDSCAPES_ID])
				; // TODO
			else if (spSelected[CDConstants.SOUNDS_ID])
				cdc.extractSounds();
		}
	}
	
	/**
	 * File panel events.
	 * @param b
	 */
	private void handleFPEvents(JButton b)
	{
		int j = 0;
		for (; j < spSelected.length && !spSelected[j]; ++j);
		if (j == spSelected.length)
			return;
		JFileChooser c = new JFileChooser(CDControl.devFolder);
		if (spSelected[CDConstants.SPRITES_ID]) {
			c.setCurrentDirectory(new File(CDControl.devFolderSpritesPNG));
		} else if (spSelected[CDConstants.MODELS_ID])
			c.setCurrentDirectory(new File(CDControl.devFolderModels));
		else if (spSelected[CDConstants.LANDSCAPES_ID])
			c.setCurrentDirectory(new File(CDControl.devFolderLandscapes));
		else if (spSelected[CDConstants.SOUNDS_ID])
			c.setCurrentDirectory(new File(CDControl.devFolderSounds));
		int rVal = c.showOpenDialog(new JFrame());
		if (rVal == JFileChooser.APPROVE_OPTION)
		{ // "Open"
			String filePath = c.getCurrentDirectory().toString()
					+ "/" + c.getSelectedFile().getName();
			String[] dirs = filePath.split("/");
			int rootDir = 0;
			for (int i = 0; i < dirs.length; ++i)
				if (dirs[i].equals("src"))
					rootDir = i;
			StringBuilder sb = new StringBuilder();
			int i;
			for (i = rootDir; i < dirs.length-1; sb.append(dirs[i++]+"/"));
			sb.append(dirs[i]);
			String path = sb.toString();
			pathTxt.setText(path);
			if (spSelected[CDConstants.SPRITES_ID]) {
				sprites.fileSelected(new File(path));
			} else if (spSelected[CDConstants.MODELS_ID])
				models.fileSelected(new File(path));
			else if (spSelected[CDConstants.LANDSCAPES_ID])
				; // TODO: set values for landscapes
			else if (spSelected[CDConstants.SOUNDS_ID])
				sounds.fileSelected(new File(path));
		}
		if (rVal == JFileChooser.CANCEL_OPTION)
		{ // "Cancel"
			pathTxt.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		if (e.getSource() instanceof JButton)
		{
			JButton b = (JButton) e.getSource();
			String bName = b.getName();
			if (bName.startsWith(MPB_SIGN))
				handleMPBEvents(b); // main panel bottom
			else if (bName.startsWith(FP_SIGN))
				handleFPEvents(b); // file panel
		}
		else if (e.getSource() instanceof JTextField)
		{
			JTextField t = (JTextField) e.getSource();
			String tName = t.getName();
			if (tName.startsWith(SpritesPanel.SIGN))
				sprites.handleEvent(tName); // sprite panel
			else if (tName.startsWith(ModelsPanel.SIGN))
				models.handleEvent(tName); // models panel
			else if (tName.startsWith(SoundsPanel.SIGN))
				sounds.handleEvent(tName); // models panel
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
	    JTabbedPane tabSource = (JTabbedPane) e.getSource();
	    String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());
	    if (tab.equals(CDConstants.DB_NAMES[CDConstants.SPRITES_ID])) {
	    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	    	spSelected[CDConstants.SPRITES_ID] = true;
	    }
	    else if (tab.equals(CDConstants.DB_NAMES[CDConstants.MODELS_ID])) {
	    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	    	spSelected[CDConstants.MODELS_ID] = true;
	    }
	    else if (tab.equals(CDConstants.DB_NAMES[CDConstants.LANDSCAPES_ID])) {
	    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	    	spSelected[CDConstants.LANDSCAPES_ID] = true;
	    }
	    else if (tab.equals(CDConstants.DB_NAMES[CDConstants.SOUNDS_ID])) {
	    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	    	spSelected[CDConstants.SOUNDS_ID] = true;
	    }
	}
}
