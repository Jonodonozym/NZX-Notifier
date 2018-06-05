/**
 * TradeOverview.java
 *
 * Created by Jaiden Baker on Jul 26, 2017 11:16:20 AM
 * Copyright © 2017. All rights reserved.
 * 
 * Jul 26, 2017 11:31:50 AM
 */

package jdz.NZXN.dataStructs.TradeTables;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jdz.NZXN.resources.Fonts;
import lombok.Data;

@Data
public class TradeOverview {
	private final double price, vwap, buy, sell, volume, value;
	private final LocalDateTime time;

	public JPanel toPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.BOTH;

		JLabel price = new JLabel(this.price + "");
		JLabel time = new JLabel(this.time + "");
		price.setFont(Fonts.bigFont);
		time.setFont(Fonts.smallFont);

		cons.weighty = 2;
		panel.add(price, cons);
		cons.weighty = 1;
		cons.gridy = 1;
		panel.add(time, cons);

		String[] headers = new String[] { "VWAP", "Buy", "Sell", "Volume", "Value" };
		String[] contents = new String[] { vwap + "", buy + "", sell + "", volume + "", "$" + value };
		JLabel[] headerLabels = new JLabel[5];
		JLabel[] headerContents = new JLabel[5];
		for (int i = 0; i < 5; i++) {
			headerLabels[i] = new JLabel(headers[i]);
			headerContents[i] = new JLabel(contents[i]);
			headerLabels[i].setFont(Fonts.smallBoldFont);
			headerContents[i].setFont(Fonts.smallFont);

			cons.gridx++;
			cons.gridy = 0;
			cons.weighty = 1;
			panel.add(headerLabels[i]);
			cons.gridy = 1;
			cons.weighty = 3;
			panel.add(headerContents[i]);
		}
		return panel;
	}
}
