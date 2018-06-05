/**
 * Notification.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 2:25:33 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 17, 2017 1:34:23 PM
 */

package jdz.NZXN.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import jdz.NZXN.resources.Resources;
import jdz.NZXN.utils.swing.JImagePanel;

/**
 * Abstract Notification class. This JDialog displays a container with the nxz
 * logo, the title and
 * a 'close notification' button at the top and a content panel beneath it. The
 * content panel
 * is created by extending classes
 * 
 * By default, it is not visible and must be made visible after calling
 * displayContents();
 * 
 * Please add a notification to the NotificationManager if you want it to be
 * positioned correctly
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
abstract class NotificationDialog extends JDialog {
	protected static final int width = 520;
	
	private static final Font topFont = new Font("Calibri", Font.BOLD, 20);
	private static final int border = 12;

	NotificationDialog() {
		setUndecorated(true);
		setVisible(false);
	}

	/**
	 * Must be called by implementing classes to display the contents (does not do
	 * it by default)
	 */
	protected void displayContents() {
		JPanel backgroundPanel = new BackgroundPanel();
		setContentPane(backgroundPanel);
		backgroundPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		backgroundPanel.setLayout(new BorderLayout());

		// contents that will be displayed under the top panel
		JPanel contents = getNotificationPanel();

		// initializing the top panel for the logo, name and close button
		final JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		titlePanel.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		titlePanel.setMaximumSize(new Dimension(1024, Resources.bannerImage.getHeight() + border * 2));

		// displaying the logo
		JImagePanel imagePanel = new JImagePanel(Resources.bannerImage);

		// displaying the title of the notification panel
		JLabel titleLabel = new JLabel(
				"<html><div style='text-align: center;'>" + contents.getName() + "</div></html>");
		titleLabel.setFont(topFont);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// close button
		final JButton closeButton = new JButton(new AbstractAction("x") {
			@Override
			public void actionPerformed(final ActionEvent e) {
				NotificationManager.delete(NotificationDialog.this);
				dispose();
			}
		});
		closeButton.setBackground(Color.WHITE);
		closeButton.setOpaque(false);
		closeButton.setMargin(new Insets(1, 4, 1, 4));
		closeButton.setFocusable(false);


		// adding everything to the top panel
		titlePanel.add(imagePanel, BorderLayout.LINE_START);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(closeButton, BorderLayout.LINE_END);

		// adding the title panel and contents to this JDialog's content pane
		backgroundPanel.add(titlePanel, BorderLayout.PAGE_START);
		backgroundPanel.add(contents, BorderLayout.PAGE_END);

		validate();
		backgroundPanel.setPreferredSize(new Dimension(width, backgroundPanel.getHeight()));

		// would fuck up rendering 60% of the time calling pack and setVisible without
		// invokeLater
		// don't ask why cause I have no idea. Solution on StackOverflow was confusing.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pack();
			}
		});
	}

	/**
	 * Abstract method to fetch the contents of the notification
	 * 
	 * @return
	 */
	protected abstract JPanel getNotificationPanel();

	private class BackgroundPanel extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getParent().getWidth() - 1, getParent().getHeight() - 1);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(0, 0, getParent().getWidth() - 1, getParent().getHeight() - 1);
			g.drawRect(1, 1, getParent().getWidth() - 3, getParent().getHeight() - 3);
		}
	}
}