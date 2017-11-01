/**
 * PriceConfigPane.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 3:22:01 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 5:05:08 PM
 */

package jdz.NZXN.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import jdz.NZXN.Config.Config;

public class PriceConfigPane extends JPanel{
	private static final long serialVersionUID = -5428649224901878255L;
	private JPanel priceListPanel;
	private JCheckBox enablePrice;
	private int border = 12;
	
	PriceConfigPane(ConfigWindow configWindow, Config config){
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
		
		JPanel headerConfig = new JPanel();
		
		headerConfig.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    constraints.weightx = 1.0f;
	    constraints.weighty = 0f;
	    constraints.fill = GridBagConstraints.BOTH;
	    
	    
		enablePrice = new JCheckBox("Enable price alerts");
		enablePrice.setBackground(Color.WHITE);
		enablePrice.setSelected(config.getPriceEnabled());
		JLabel priceAlertDisclosure = new JLabel("Prices are from the nzx website and are delayed 15-20 minutes");
		priceAlertDisclosure.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 0));
		
		headerConfig.add(enablePrice,constraints);
		constraints.ipady = 14;
		constraints.gridy++;
		headerConfig.add(priceAlertDisclosure,constraints);
		constraints.gridy++;
		headerConfig.add(new JSeparator(SwingConstants.HORIZONTAL),constraints);
		constraints.ipady = 8;
		
		
		priceListPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(priceListPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		priceListPanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		priceListPanel.setLayout(new BoxLayout(priceListPanel, BoxLayout.Y_AXIS));
		
		reloadConfig(config);
		
		add(headerConfig, BorderLayout.PAGE_START);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	void reloadConfig(Config config){
		priceListPanel.removeAll();
		
		for (String s: config.getPriceAlerts())
			priceListPanel.add(PricePanel.fromString(this, s));
		
		JButton addButton = new JButton("+");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				priceListPanel.add(new PricePanel(PriceConfigPane.this));
				priceListPanel.remove(addButton);
				priceListPanel.add(addButton);
				priceListPanel.repaint();
			}
		});
		addButton.setPreferredSize(new Dimension(40,24));
		priceListPanel.add(addButton);
	}
	
	void saveConfig(Config config){
		List<String> priceAlerts = new ArrayList<String>();
		for (Component p: priceListPanel.getComponents())
			if (p instanceof PricePanel)
				priceAlerts.add(((PricePanel)p).toString());
		config.setPriceAlerts(priceAlerts);
		config.setPriceEnabled(enablePrice.isSelected());
	}

	void removePanel(PricePanel panel){
		priceListPanel.remove(panel);
		revalidate();
		repaint();
	}
	
}
