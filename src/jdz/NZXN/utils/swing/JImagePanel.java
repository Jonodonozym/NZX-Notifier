
package jdz.NZXN.utils.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JImagePanel extends JPanel{
	private static final long serialVersionUID = 1082374290242883422L;
	
	private final BufferedImage image;
	
	public JImagePanel(BufferedImage image) {
		this.image = image;
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
	}
		
	
}
