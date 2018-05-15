/**
 * FilterConfigPane.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 3:21:42 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 1, 2017 12:26:09 PM
 */

package jdz.NZXN.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.io.AnnouncementIO;
import jdz.NZXN.res.Resources;
import jdz.NZXN.utils.StringUtils;

@SuppressWarnings("serial")
public class AnnounceConfigPane extends JPanel{
	private static final Font textAreaFont = new JLabel().getFont();
	private JTextArea[] textAreas;
	private JCheckBox toggleAnnouncements, toggleAnnouncementSaving;

	AnnounceConfigPane(ConfigWindow configWindow){

		setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    constraints.weightx = 1.0f;
	    constraints.weighty = 0f;
	    constraints.fill = GridBagConstraints.BOTH;
	    constraints.insets = new Insets(4, 8, 0, 8);
		
		toggleAnnouncements = new JCheckBox("Enable announcement notifications");
		toggleAnnouncements.setBackground(Color.WHITE);
		add(toggleAnnouncements,constraints);
		
		constraints.gridy++;
		
		toggleAnnouncementSaving = new JCheckBox("Enable downloading announcements");
		toggleAnnouncementSaving.setBackground(Color.WHITE);
		toggleAnnouncementSaving.setToolTipText("<html><p width=\"256\"> This option automatically downloads and "
				+ "saves announcements and their attatchments for offline viewing. This may take up a lot of "
				+ "storage space after a while and usually isn't neccessary </p></html>");
		add(toggleAnnouncementSaving,constraints);
		
		constraints.gridy++;
	    
		add(new JSeparator(SwingConstants.HORIZONTAL),constraints);
		
		textAreas = new JTextArea[6];
		JPanel[] textPanels = new JPanel[6];
		JLabel[] labels = { new JLabel("Security Whitelist"),new JLabel("Description Whitelist"),new JLabel("Type Whitelist"), 
				new JLabel("Security Blacklist"), new JLabel("Description Blacklist"), new JLabel("Type Blacklist") };
		for (int i=0; i<6; i++){
			textAreas[i] = new JTextArea();
			textAreas[i].setFont(textAreaFont);
			JScrollPane scrollPanel = new JScrollPane(textAreas[i]);
			scrollPanel.setPreferredSize(new Dimension(128,64));
			textPanels[i] = new JPanel();
			textPanels[i].setLayout(new BoxLayout(textPanels[i], BoxLayout.Y_AXIS));
			labels[i].setAlignmentX(CENTER_ALIGNMENT);
			textPanels[i].add(labels[i]);
			textPanels[i].add(scrollPanel);
		}
		
		reloadConfig();

	    ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		String[] tooltips = {
				"To only display announcements that match a certain security or type, enter "
				+ "them in the whitelist boxes, 1 on each line. Descriptions can be whitelisted if they contain "
				+ "keywords you enter like 'NAV'. Leave all areas blank to display all announcements",
				"To ignore announcements that match a certain security or type regardless of whitelist rules "
				+ "enter them in the blacklist boxes, 1 on each line. Descriptions can be blacklisted if they contain "
				+ "keywords you enter like 'NAV'. "
		};
		for(int i=0; i<6; i+=3){
			JPanel panel = new JPanel();

			JLabel info = new JLabel(new ImageIcon(Resources.infoIcon));
			info.setToolTipText("<html><p width=\"256\">" +tooltips[i/2]+"</p></html>");

			panel.add(info);
			panel.add(textPanels[i]);
			panel.add(textPanels[i+1]);
			panel.add(textPanels[i+2]);

			constraints.gridy++;
			add(panel,constraints);
		    constraints.insets = new Insets(0, 8, 4, 8);
		}
		
		constraints.gridy++;
		add(new JSeparator(SwingConstants.HORIZONTAL),constraints);
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.VERTICAL;
		
		JButton seePastAnnouncements = new JButton("View Past Announcements");
		seePastAnnouncements.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File file = AnnouncementIO.getCSVFile();
					if(file != null)
						Runtime.getRuntime().exec("explorer.exe /select," + file.getPath());
				}
				catch (IOException e1) { }
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(seePastAnnouncements);
		
		add(buttonPanel,constraints);
	}
	
	void reloadConfig(){
		toggleAnnouncements.setSelected(ConfigProperty.ANNOUNCEMENT_ALERTS_ENABLED.get());
		toggleAnnouncementSaving.setSelected(ConfigProperty.ANNOUNCEMENT_SAVING_ENABLED.get());
		
		String[] defaultText = {
				StringUtils.mergeList(ConfigProperty.SECURITY_WHITELIST.get(),"\n"),
				StringUtils.mergeList(ConfigProperty.DESCRIPTION_WHITELIST.get(),"\n"),
				StringUtils.mergeList(ConfigProperty.TYPE_WHITELIST.get(),"\n"),
				StringUtils.mergeList(ConfigProperty.SECURITY_BLACKLIST.get(),"\n"),
				StringUtils.mergeList(ConfigProperty.DESCRIPTION_BLACKLIST.get(),"\n"),
				StringUtils.mergeList(ConfigProperty.TYPE_BLACKLIST.get(),"\n")
		};
		
		for (int i=0; i<6; i++)
			textAreas[i].setText(defaultText[i]);
		repaint();
	}
	
	void saveConfig(){
		ConfigProperty.ANNOUNCEMENT_ALERTS_ENABLED.set(toggleAnnouncements.isSelected());
		ConfigProperty.ANNOUNCEMENT_SAVING_ENABLED.set(toggleAnnouncementSaving.isSelected());
		ConfigProperty.SECURITY_WHITELIST.set(Arrays.asList(textAreas[0].getText().replace(",","\n").split("\n")));
		ConfigProperty.DESCRIPTION_WHITELIST.set(Arrays.asList(textAreas[1].getText().replace(",","\n").split("\n")));
		ConfigProperty.TYPE_WHITELIST.set(Arrays.asList(textAreas[2].getText().replace(",","\n").split("\n")));
		ConfigProperty.SECURITY_BLACKLIST.set(Arrays.asList(textAreas[3].getText().replace(",","\n").split("\n")));
		ConfigProperty.DESCRIPTION_BLACKLIST.set(Arrays.asList(textAreas[4].getText().replace(",","\n").split("\n")));
		ConfigProperty.TYPE_BLACKLIST.set(Arrays.asList(textAreas[5].getText().replace(",","\n").split("\n")));
	}
	
	
}