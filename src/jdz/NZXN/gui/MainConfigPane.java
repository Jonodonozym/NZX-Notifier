/**
 * MainConfigPane.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 3:21:52 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 1, 2017 12:26:01 PM
 */

package jdz.NZXN.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import jdz.NZXN.config.ConfigChangeListener;
import jdz.NZXN.config.ConfigProperty;
import jdz.NZXN.tasks.CheckAnnouncementsTask;
import jdz.NZXN.webApi.nzx.NZXWebApi;

@SuppressWarnings("serial")
public class MainConfigPane extends JPanel {
	private int border = 16;
	private SpinnerNumberModel spinnerModel;
	private JLabel nextCheck;
	private JCheckBox isMutedCheckbox;
	private DateFormat timeFormat = new SimpleDateFormat("dd MMMM, hh:mm:ss");

	MainConfigPane(ConfigWindow configWindow) {
		setBorder(BorderFactory.createEmptyBorder(border, border, border / 2, border));
		setLayout(new BorderLayout());

		JPanel MainContents = new JPanel();
		MainContents.setLayout(new BoxLayout(MainContents, BoxLayout.Y_AXIS));

		for (JLabel l : getTimeLabels())
			MainContents.add(l);

		MainContents.add(getCheckNowPanel());

		MainContents.add(Box.createVerticalStrut(6));
		MainContents.add(new JSeparator(SwingConstants.HORIZONTAL));
		MainContents.add(Box.createVerticalStrut(6));

		for (JComponent c : getConfigComponents(configWindow))
			MainContents.add(c);

		add(MainContents, BorderLayout.PAGE_START);

		JPanel noticePanel = new JPanel();
		JLabel copyrightNotice = new JLabel("Copyright © Jaiden Baker 2017. All rights reserved.");
		noticePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		noticePanel.add(copyrightNotice);
		this.add(noticePanel, BorderLayout.PAGE_END);

	}

	private JLabel[] getTimeLabels() {
		JLabel lastCheck = new JLabel(
				"<html>Last Check:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#00C1D8>"
						+ timeFormat.format(CheckAnnouncementsTask.getInstance().getLastCheck()) + "</font>");
		JLabel currentTime = new JLabel("<html>Current Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#DE2700>"
				+ timeFormat.format(CheckAnnouncementsTask.getInstance().getCurrentTime()) + "</font>");
		nextCheck = new JLabel("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>"
				+ timeFormat.format(CheckAnnouncementsTask.getInstance().getNextCheck()) + "</font>");


		lastCheck.setAlignmentX(LEFT_ALIGNMENT);
		currentTime.setAlignmentX(LEFT_ALIGNMENT);
		nextCheck.setAlignmentX(LEFT_ALIGNMENT);

		nextCheck.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

		CheckAnnouncementsTask.getInstance().addTaskAfterCheck(new Runnable() {
			@Override
			public void run() {
				lastCheck
						.setText("<html>Last Check:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#00C1D8>"
								+ timeFormat.format(CheckAnnouncementsTask.getInstance().getLastCheck()) + "</font>");
				nextCheck.setText("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>"
						+ timeFormat.format(CheckAnnouncementsTask.getInstance().getNextCheck()) + "</font>");
			}
		});

		CheckAnnouncementsTask.getInstance().addTaskEachSecond(new Runnable() {
			@Override
			public void run() {
				currentTime.setText("<html>Current Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#DE2700>"
						+ timeFormat.format(CheckAnnouncementsTask.getInstance().getCurrentTime()) + "</font>");
			}
		});

		return new JLabel[] { lastCheck, currentTime, nextCheck };
	}

	private JPanel getCheckNowPanel() {
		JPanel checkNowPanel = new JPanel();
		JLabel checkResults = new JLabel("");
		if (!NZXWebApi.instance.canConnect())
			checkResults.setText("<html><font color=#DE2700>Error: no connection to the NZX website</font>");
		checkNowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton checkNow = new JButton("Check Now");
		checkNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (NZXWebApi.instance.canConnect()) {
					checkResults.setText("Checking now...");
					repaint();
					CheckAnnouncementsTask.getInstance().check();
					checkResults.setText("Check completed");
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							checkResults.setText("");
						}
					}, 10000);
				}
				else {
					checkResults.setText("<html><font color=#DE2700>Error: no connection to the NZX website</font>");
				}
			}
		});
		checkNowPanel.add(checkNow);
		checkNowPanel.add(checkResults);
		checkNowPanel.setAlignmentX(LEFT_ALIGNMENT);

		return checkNowPanel;
	}

	private JComponent[] getConfigComponents(ConfigWindow configWindow) {
		JPanel checkIntervalPanel = new JPanel();
		FlowLayout checkIntervalLayout = new FlowLayout(FlowLayout.LEFT);
		checkIntervalLayout.setHgap(0);
		checkIntervalPanel.setLayout(checkIntervalLayout);
		JLabel checkIntervalLabel = new JLabel("Check Interval (Minutes)");
		checkIntervalLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
		checkIntervalPanel.add(checkIntervalLabel);
		spinnerModel = new SpinnerNumberModel((int) ConfigProperty.CHECK_INTERVAL_MINUTES.get(), 1, 1440, 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		spinner.getEditor().setPreferredSize(new Dimension(48, 12));
		spinner.addChangeListener((e) -> {
			ConfigProperty.CHECK_INTERVAL_MINUTES.set(spinnerModel.getNumber().intValue());
			nextCheck.setText("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>"
					+ timeFormat.format(CheckAnnouncementsTask.getInstance().getNextCheck()) + "</font>");
		});
		ConfigChangeListener.register(ConfigProperty.CHECK_INTERVAL_MINUTES, (newValue) -> {
			spinnerModel.setValue(newValue);
		});

		checkIntervalPanel.add(spinner);
		checkIntervalPanel.setAlignmentX(LEFT_ALIGNMENT);

		isMutedCheckbox = new JCheckBox("Silent mode");
		isMutedCheckbox.setToolTipText(
				"<html><p width=\"256\">If selected, notifications will no longer stay on top of other windows or play sounds. </p></html>");
		isMutedCheckbox.setBackground(Color.white);
		isMutedCheckbox.addItemListener((e) -> {
			ConfigProperty.IS_MUTED.set(e.getStateChange() == ItemEvent.SELECTED);
		});
		ConfigChangeListener.register(ConfigProperty.IS_MUTED, (newValue) -> {
			isMutedCheckbox.setSelected(newValue);
		});

		return new JComponent[] { checkIntervalPanel, isMutedCheckbox };
	}

	void reloadConfig() {
		spinnerModel.setValue(ConfigProperty.CHECK_INTERVAL_MINUTES.get());
		isMutedCheckbox.setSelected(ConfigProperty.IS_MUTED.get());
	}

	void saveConfig() {
		ConfigProperty.CHECK_INTERVAL_MINUTES.set(spinnerModel.getNumber().intValue());
		ConfigProperty.IS_MUTED.set(isMutedCheckbox.isSelected());
	}
}
