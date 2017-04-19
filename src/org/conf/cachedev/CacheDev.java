package org.conf.cachedev;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CacheDev extends JFrame implements ActionListener
{
	private JButton pngToDat, datToPNG, dtpRun, dtpCancel;
	private JTextField dtpStartField, dtpEndField;
	private JPanel panel;
	private Sprites sp;
	private Textures tx;
	public CacheDev()
	{
		super("Cache development utility");
		panel = new JPanel(new GridLayout(4, 1));
		pngToDat = new JButton("png to dat");
		pngToDat.setName("pngtodat");
		pngToDat.addActionListener(this);
		panel.add(pngToDat);
		datToPNG = new JButton("dat to png");
		datToPNG.setName("dattopng");
		datToPNG.addActionListener(this);
		panel.add(datToPNG);
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		pack();
	}
	
	public static void main(String args[])
	{
		CacheDev cd = new CacheDev();
	}
	
	private void pngToDat()
	{
		System.out.println("Converting PNG to DAT");
	}
	
	private void datToPNG()
	{
		JFrame dtpFrame = new JFrame("Converting DAT to PNG");
		JPanel dtpPanel = new JPanel(new BorderLayout());
		
		JTextArea infoText = new JTextArea();
		infoText.setEditable(false);
		infoText.setLineWrap(true);
		infoText.setText("Write the file name of the first and last file"
				+ " to be converted. Every file inbetween the first and"
				+ " last file will be converted.");
		dtpPanel.add(infoText, BorderLayout.NORTH);
		
		JPanel entryPanel = new JPanel(new GridLayout(3,2));
		JLabel startLabel = new JLabel("Start file");
		entryPanel.add(startLabel, 0);
		JTextField dtpStartField = new JTextField("");
		entryPanel.add(dtpStartField, 1);
		JLabel endLabel = new JLabel("End file");
		entryPanel.add(endLabel, 2);
		JTextField dtpEndField = new JTextField("");
		entryPanel.add(dtpEndField, 3);
		dtpPanel.add(entryPanel, BorderLayout.CENTER);
		
		JPanel ctrlPanel = new JPanel(new GridLayout(1, 2));
		dtpRun = new JButton("Run");
		dtpRun.setName("dtprun");
		dtpRun.addActionListener(this);
		ctrlPanel.add(dtpRun);
		dtpCancel = new JButton("Cancel");
		dtpCancel.setName("dtpcancel");
		dtpCancel.addActionListener(this);
		ctrlPanel.add(dtpCancel);
		dtpPanel.add(ctrlPanel, BorderLayout.SOUTH);
		
		dtpFrame.add(dtpPanel);
		dtpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dtpFrame.setResizable(false);
		dtpFrame.setVisible(true);
		dtpFrame.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton b = (JButton) e.getSource();
		if (b.getName().equals(pngToDat.getName()))
			pngToDat();
		else if (b.getName().equals(datToPNG.getName()))
			datToPNG();
		else if (b.getName().startsWith("dtp"))
			if (dtpRun != null && b.getName().equals(dtpRun.getName()))
			{
				; // collect info from dtpStartField as start value
				; // collect info from dtpEndField as end value
			}
			else if (dtpCancel != null && b.getName().equals(dtpCancel.getName()))
				; // dispose of window
	}
}
