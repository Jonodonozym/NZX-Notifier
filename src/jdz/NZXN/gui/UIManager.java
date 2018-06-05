
package jdz.NZXN.gui;

import java.awt.Color;

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

public class UIManager {
	public static void useCleanStyle() {
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
			javax.swing.UIManager.put("Panel.background", Color.WHITE);
			javax.swing.UIManager.put("OptionPane.background", Color.WHITE);

			javax.swing.UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			javax.swing.UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			javax.swing.UIManager.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
			javax.swing.UIManager.put("ComboBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
