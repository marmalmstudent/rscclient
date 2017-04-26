package org.conf.cachedev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
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
	private SpritesTab sprites;
	private ModelsPanel models;
	private SoundsPanel sounds;
	private JButton mpbWorkon, mpbWorkoff, mpbImport, mpbExport;
	
	public static final String FP_SIGN = "fp_";
	public static final String MPB_SIGN = "mpb_";
	public static final String MPC_SIGN = "mpc_";
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

		sprites = new SpritesTab(this);
		mpCenter.addTab(CDConst.DB_NAMES[CDConst.SPRITES_ID], sprites);
		models = new ModelsPanel(this);
		mpCenter.addTab(CDConst.DB_NAMES[CDConst.MODELS_ID], models);
		sounds = new SoundsPanel(this);
		mpCenter.addTab(CDConst.DB_NAMES[CDConst.SOUNDS_ID], sounds);
		
		spSelected[0] = true;
		mpCenter.addChangeListener(this);
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
		filePanel.setPreferredSize(new Dimension(700, 28));
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
		if (spSelected[CDConst.SPRITES_ID]) {
			if (bName.equals(mpbWorkon.getName())) {
				sprites.handleWorkon();
			} else if (bName.equals(mpbWorkoff.getName())) {
				sprites.handleWorkoff();
			} else if (bName.equals(mpbImport.getName())) {
				sprites.handleInsert();
			} else if (bName.equals(mpbExport.getName())) {
				sprites.handleExtract();
			}
		} else if (spSelected[CDConst.MODELS_ID]) {
			if (bName.equals(mpbWorkon.getName())) {
				cdc.workonModels();
			} else if (bName.equals(mpbWorkoff.getName())) {
				cdc.workoffModels();
			} else if (bName.equals(mpbImport.getName())) {
				; // TODO
			} else if (bName.equals(mpbExport.getName())) {
				cdc.extractModels();
			}
		} else if (spSelected[CDConst.LANDSCAPES_ID]) {
			if (bName.equals(mpbWorkon.getName())) {
				; // TODO
			} else if (bName.equals(mpbWorkoff.getName())) {
				; // TODO
			} else if (bName.equals(mpbImport.getName())) {
				; // TODO
			} else if (bName.equals(mpbExport.getName())) {
				; // TODO
			}
		} else if (spSelected[CDConst.SOUNDS_ID]) {
			if (bName.equals(mpbWorkon.getName())) {
				cdc.workonSounds();
			} else if (bName.equals(mpbWorkoff.getName())) {
				cdc.workoffSounds();
			} else if (bName.equals(mpbImport.getName())) {
				cdc.insertSounds();
			} else if (bName.equals(mpbExport.getName())) {
				cdc.extractSounds();
			}
		}
	}
	
	public File getDefaultPath()
	{
		File path = new File(CDConst.devDir);
		if (spSelected[CDConst.SPRITES_ID]) {
			path = sprites.getDefaultPath();
		} else if (spSelected[CDConst.MODELS_ID])
			path = new File(CDConst.ModelsDir);
		else if (spSelected[CDConst.LANDSCAPES_ID])
			path = new File(CDConst.LandscapesDir);
		else if (spSelected[CDConst.SOUNDS_ID])
			path = new File(CDConst.SoundsDir);
		return path;
	}
	
	public void setFileVars(File path)
	{
		if (spSelected[CDConst.SPRITES_ID]) {
			sprites.fileSelected(path);
		} else if (spSelected[CDConst.MODELS_ID])
			models.fileSelected(path);
		else if (spSelected[CDConst.LANDSCAPES_ID])
			; // TODO: set values for landscapes
		else if (spSelected[CDConst.SOUNDS_ID])
			sounds.fileSelected(path);
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
		JFileChooser c = new JFileChooser(CDConst.devDir);
		c.setCurrentDirectory(getDefaultPath());
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
			setFileVars(new File(path));
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
			if (tName.startsWith(TexturePanel.SIGN))
				sprites.handleEvent(tName); // sprite panel
			else if (tName.startsWith(ModelsPanel.SIGN))
				models.handleEvent(tName); // models panel
			else if (tName.startsWith(SoundsPanel.SIGN))
				sounds.handleEvent(tName); // models panel
		}
	}
	
	private void resetSelected()
	{
    	for (int i = 0; i < spButtons; spSelected[i++] = false);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
	    JTabbedPane tabSource = (JTabbedPane) e.getSource();
	    String tab = tabSource.getTitleAt(tabSource.getSelectedIndex());
	    if (tab.equals(CDConst.DB_NAMES[CDConst.SPRITES_ID]))
	    {
	    	resetSelected();
	    	spSelected[CDConst.SPRITES_ID] = true;
	    }
	    else if (tab.equals(CDConst.DB_NAMES[CDConst.MODELS_ID]))
	    {
	    	resetSelected();
	    	spSelected[CDConst.MODELS_ID] = true;
	    }
	    else if (tab.equals(CDConst.DB_NAMES[CDConst.LANDSCAPES_ID]))
	    {
	    	resetSelected();
	    	spSelected[CDConst.LANDSCAPES_ID] = true;
	    }
	    else if (tab.equals(CDConst.DB_NAMES[CDConst.SOUNDS_ID]))
	    {
	    	resetSelected();
	    	spSelected[CDConst.SOUNDS_ID] = true;
	    }
	}
}
