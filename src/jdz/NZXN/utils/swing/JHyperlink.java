/**
 * JHyperlink.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 3:55:45 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 6, 2017 5:05:08 PM
 */

package jdz.NZXN.utils.swing;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class JHyperlink extends JLabel{
	public JHyperlink(String label, String url){
		 super(label);
		 setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		 addMouseListener(new MouseAdapter() {
		   public void mouseClicked(MouseEvent e) {
		      if (e.getClickCount() > 0) {
		          if (Desktop.isDesktopSupported()) {
		                Desktop desktop = Desktop.getDesktop();
		                try {
		                    URI uri = new URI(url);
		                    desktop.browse(uri);
		                } catch (IOException | URISyntaxException ex) {
		                    ex.printStackTrace();
		                }
		        }
		      }
		   }
		});
	}
}
