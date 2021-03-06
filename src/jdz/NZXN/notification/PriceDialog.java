/**
 * PriceNotification.java
 *
 * Created by Jaiden Baker on Jul 13, 2017 11:36:27 AM
 * Copyright � 2017. All rights reserved.
 * 
 * Last modified on Nov 1, 2017 11:56:04 AM
 */

package jdz.NZXN.notification;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jdz.NZXN.checker.PriceChangeEvent;
import jdz.NZXN.utils.swing.JHyperlink;
import jdz.NZXN.webApi.Websites;

/**
 * Notification class that displays a price on the screen
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
class PriceDialog extends NotificationDialog {
	private static final Font priceFont = new Font("Calibri", Font.PLAIN, 40);
	private static final Font secFont = new Font("Calibri", Font.PLAIN, 20);
	
	private final String security;
	private final double price, oldPrice;

	public PriceDialog(PriceChangeEvent event) {
		super();

		this.security = event.getSecurity();
		this.price = event.getPrice();
		this.oldPrice = event.getOldPrice();
		setMinimumSize(new Dimension(width, 144));

		displayContents();
	}

	@Override
	protected JPanel getNotificationPanel() {
		final JPanel contentPanel = new JPanel();
		contentPanel.setName(security + " Price Alert");
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

		String arrow = oldPrice < price ? "<font color=#49A942>&#x25B2;</font>" : "<font color=#C12828>&#x25BC;</font>";
		arrow = oldPrice == price ? "" : arrow;

		JLabel priceLabel = new JHyperlink(
				"<html>" + arrow + "&nbsp;&nbsp;" + price + "�&nbsp;&nbsp;" + arrow + "</html>",
				Websites.NZXsecurityURL + security);
		JLabel secLabel = new JHyperlink(security, Websites.NZXsecurityURL + security);

		priceLabel.setFont(priceFont);
		secLabel.setFont(secFont);

		priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		secLabel.setHorizontalAlignment(SwingConstants.CENTER);

		contentPanel.add(priceLabel, BorderLayout.CENTER);
		contentPanel.add(secLabel, BorderLayout.PAGE_END);
		return contentPanel;
	}
}