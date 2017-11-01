/**
 * PricePanel.java
 *
 * Created by Jaiden Baker on Jul 12, 2017 12:42:46 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 12, 2017 1:46:37 PM
 */

package jdz.NZXN.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PricePanel extends JPanel{
	private static final long serialVersionUID = 1550510965751649892L;
	private JTextField security;
	private JComboBox<String> operator;
	private JFormattedTextField price;
	private JButton removeButton;
	
	private static DecimalFormat df = new DecimalFormat("#.#");
	
	PricePanel(PriceConfigPane parent){
		this("[Security]",">",-1,parent);
	}
	
	private PricePanel(String securityName, String operatorString, double amount, PriceConfigPane parent){
		
		setMaximumSize(new Dimension(1024,24));
		removeButton = new JButton("-");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.removePanel(PricePanel.this);
				parent.repaint();
			}
		});
		removeButton.setPreferredSize(new Dimension(40,24));
		
		price = new JFormattedTextField(NumberFormat.getNumberInstance());
		price.setValue(new Double(amount));
		price.setColumns(12);

		security = new JTextField(securityName);
		security.setColumns(12);
		
		String[] operators = { ">", ">=", "<", "<=", "=", "Any change" };
		operator = new JComboBox<String>(operators);
		operator.setSelectedIndex(Arrays.asList(operators).indexOf(operatorString));
		operator.setPreferredSize(new Dimension(64,24));
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(removeButton);
		add(Box.createHorizontalStrut(16));
		add(security);
		add(Box.createHorizontalStrut(16));
		add(operator);
		add(Box.createHorizontalStrut(16));
		add(price);
		
		setAlignmentX(LEFT_ALIGNMENT);
	}
	
	static PricePanel fromString(PriceConfigPane parent, String string){
		String[] args = string.split(":");
		return new PricePanel(args[0],args[1],Double.parseDouble(args[2]),parent);
	}
	
	public String toString(){
		return security.getText().toUpperCase()+":"+operator.getSelectedItem()+":"+df.format(price.getValue());
	}
}
