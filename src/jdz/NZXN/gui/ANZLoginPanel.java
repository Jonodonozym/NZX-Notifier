
package jdz.NZXN.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import jdz.NZXN.config.PasswordVault;
import jdz.NZXN.resources.Resources;
import jdz.NZXN.config.LoginDetails;
import jdz.NZXN.utils.swing.JImagePanel;
import jdz.NZXN.webApi.anz.ANZLoginListener;
import jdz.NZXN.webApi.anz.ANZWebApi;

public class ANZLoginPanel extends JPanel implements ANZLoginListener {
	private static final long serialVersionUID = 7352090905002277948L;
	private static final int maxFailedAttempts = 3;
	private static final long loginFailedMessageDuration = 10000L;

	private final JTextField username;
	private final JTextField password;
	private final JCheckBox rememberDetails;
	private final JLabel loginFailedMessage;

	private Map<String, Integer> failedAttempts = new HashMap<String, Integer>();

	public ANZLoginPanel(ConfigWindow configWindow) {
		ANZWebApi.instance.addLoginListener(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JImagePanel image = new JImagePanel(Resources.ANZLogo);
		LoginDetails anzDetails = PasswordVault.getANZDetails();
		if (!anzDetails.isEmpty()) {
			username = new JTextField(anzDetails.getUsername());
			password = new JPasswordField(anzDetails.getPassword());
		}

		else {
			username = new JTextField("username");
			password = new JPasswordField("password");
		}

		username.setMaximumSize(new Dimension(128, 24));
		password.setMaximumSize(new Dimension(128, 24));

		rememberDetails = new JCheckBox("Remember details");
		rememberDetails.setBackground(Color.white);
		rememberDetails.setSelected(PasswordVault.isPasswordRemembered());

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener((e) -> {
			ANZWebApi.instance.login(username.getText(), password.getText());
		});

		loginFailedMessage = new JLabel("");
		loginFailedMessage.setHorizontalAlignment(SwingConstants.CENTER);

		image.setAlignmentX(CENTER_ALIGNMENT);
		username.setAlignmentX(CENTER_ALIGNMENT);
		password.setAlignmentX(CENTER_ALIGNMENT);
		rememberDetails.setAlignmentX(CENTER_ALIGNMENT);
		loginButton.setAlignmentX(CENTER_ALIGNMENT);
		loginFailedMessage.setAlignmentX(CENTER_ALIGNMENT);

		int spacing = 16;
		add(Box.createVerticalStrut(spacing * 2));
		add(image);
		add(Box.createVerticalStrut(spacing * 2));
		add(username);
		add(Box.createVerticalStrut(spacing));
		add(password);
		add(Box.createVerticalStrut(spacing));
		add(rememberDetails);
		add(Box.createVerticalStrut(spacing));
		add(loginButton);
		add(Box.createVerticalStrut(spacing));
		add(loginFailedMessage);

	}

	boolean login() {
		return ANZWebApi.instance.login(username.getText(), password.getText());
	}

	@Override
	public void onLoginAttempt(boolean isSuccessful) {
		if (isSuccessful) {
			if (PasswordVault.isPasswordRemembered())
				PasswordVault.setANZDetails(username.getText(), password.getText());
			failedAttempts.put(username.getText(), 0);
		}
		else {
			if (!failedAttempts.containsKey(username.getText()))
				failedAttempts.put(username.getText(), 1);
			else
				failedAttempts.put(username.getText(), failedAttempts.get(username.getText()));
			displayLoginFailedMessage();
		}
	}

	public void displayLoginFailedMessage() {
		if (failedAttempts.get(username.getText()) >= maxFailedAttempts) {
			loginFailedMessage.setText(format(
					"Invalid username or password. After 3 attempts with the same account name, you will be locked out of your account. in which case call 0800 805 777  (+64 4 499 6655 if overseas)"));
		}
		else {
			loginFailedMessage.setText(format("Invalid username or password"));
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					loginFailedMessage.setText("");
				}
			}, loginFailedMessageDuration);
		}
	}

	private String format(String s) {
		return "<html><p width=\"256\"><font color=#DE2700>" + s + "</font></p></html>";
	}
}
