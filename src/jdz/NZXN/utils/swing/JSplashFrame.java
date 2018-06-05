
package jdz.NZXN.utils.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jdz.NZXN.resources.Resources;

@SuppressWarnings("serial")
public class JSplashFrame extends JFrame {

	public JSplashFrame() {
		setVisible(false);
		setUndecorated(true);
		setBackground(Color.WHITE);
		JPanel bannerImage = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(Resources.bannerImage, 0, 0, null);
			}
		};
		bannerImage
				.setPreferredSize(new Dimension(Resources.bannerImage.getWidth(), Resources.bannerImage.getHeight()));
		add(bannerImage);
		setIconImage(Resources.appIcon);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pack();
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
				setAlwaysOnTop(true);
				setVisible(true);
			}
		});
	}
}
