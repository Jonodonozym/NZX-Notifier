/**
 * MainConfigPane.java
 *
 * Created by Jaiden Baker on Jul 6, 2017 3:21:52 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 13, 2017 9:48:24 AM
 */

package jdz.NZXN.Config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jdz.NZXN.WebApi.MNZXWebApi;
import jdz.NZXN.main.CheckAnnouncementsTask;

@SuppressWarnings("serial")
public class MainConfigPane extends JPanel{
	private int border = 16;
	private SpinnerNumberModel spinnerModel;
	private JLabel nextCheck;
	private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd MMMM, hh:mm:ss");

	MainConfigPane(ConfigWindow configWindow, Config config){
		setBorder(BorderFactory.createEmptyBorder(border, border, border/2, border));
		setLayout(new BorderLayout());
		
		JPanel MainContents = new JPanel();
		MainContents.setLayout(new BoxLayout(MainContents, BoxLayout.Y_AXIS));
		
		for (JLabel l: getTimeLabels())
			MainContents.add(l);
		
		MainContents.add(getCheckNowPanel());
		
		MainContents.add(Box.createVerticalStrut(6));
		MainContents.add(new JSeparator(SwingConstants.HORIZONTAL));
		MainContents.add(Box.createVerticalStrut(6));

		for (JComponent c: getConfigComponents(configWindow, config))
			MainContents.add(c);
			
		add(MainContents,BorderLayout.PAGE_START);
		
		JPanel noticePanel = new JPanel();
		JLabel copyrightNotice = new JLabel("Copyright © Jaiden Baker 2017. All rights reserved.");
		noticePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		noticePanel.add(copyrightNotice);
		this.add(noticePanel,BorderLayout.PAGE_END);
		
	}
	
	private JLabel[] getTimeLabels(){
		JLabel lastCheck =   new JLabel("<html>Last Check:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#00C1D8>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getLastCheck())+"</font>");
		JLabel currentTime = new JLabel("<html>Current Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#DE2700>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getCurrentTime())+"</font>");
		nextCheck =   new JLabel("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getNextCheck())+"</font>");
		

		lastCheck.setAlignmentX(LEFT_ALIGNMENT);
		currentTime.setAlignmentX(LEFT_ALIGNMENT);
		nextCheck.setAlignmentX(LEFT_ALIGNMENT);
		
		nextCheck.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
		
		CheckAnnouncementsTask.runningTask.addTaskAfterCheck(new Runnable() {
			@Override
			public void run() {
				lastCheck.setText("<html>Last Check:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#00C1D8>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getLastCheck())+"</font>");
				nextCheck.setText("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getNextCheck())+"</font>");
			}
		});
		
		CheckAnnouncementsTask.runningTask.addTaskEachSecond(new Runnable() {
			@Override
			public void run() {
				currentTime.setText("<html>Current Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=#DE2700>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getCurrentTime())+"</font>");
			}
		});
		
		return new JLabel[]{ lastCheck, currentTime, nextCheck };
	}
	
	private JPanel getCheckNowPanel(){
		JPanel checkNowPanel = new JPanel();
		JLabel checkResults = new JLabel("");
		if (!MNZXWebApi.checkConnection())
			checkResults.setText("<html><font color=#DE2700>Error: no connection to the NZX website</font>");
		checkNowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton checkNow = new JButton("Check Now");
		checkNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (MNZXWebApi.checkConnection()){
					CheckAnnouncementsTask.runningTask.check();
					checkResults.setText("Checking now...");
					new Timer().schedule(new TimerTask() {
						@Override public void run() {
							checkResults.setText("");
						}
					}, 10000);
				}
				else{
					checkResults.setText("<html><font color=#DE2700>Error: no connection to the NZX website</font>");
				}
			}
		});
		checkNowPanel.add(checkNow);
		checkNowPanel.add(checkResults);
		checkNowPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		return checkNowPanel;
	}
	
	private JComponent[] getConfigComponents(ConfigWindow configWindow, Config config){
		JPanel spinnerPanel = new JPanel();
		FlowLayout spinnerLayout = new FlowLayout(FlowLayout.LEFT);
		spinnerLayout.setHgap(0);
		spinnerPanel.setLayout(spinnerLayout);
		JLabel spinnerLabel = new JLabel("Check Interval (Minutes)");
		spinnerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
		spinnerPanel.add(spinnerLabel);
		spinnerModel = new SpinnerNumberModel(config.getInterval(), 1, Integer.MAX_VALUE, 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		spinner.getEditor().setPreferredSize(new Dimension(48,12));
		spinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				CheckAnnouncementsTask.runningTask.setIntervalMinutes(spinnerModel.getNumber().intValue());
				nextCheck.setText("<html>Next Check At:&nbsp;&nbsp;<font color=#DE2700>" + timeFormat.format(CheckAnnouncementsTask.runningTask.getNextCheck())+"</font>");
			}
		});
		
		spinnerPanel.add(spinner);
		spinnerPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		
		return new JComponent[]{spinnerPanel};
	}
	
	void reloadConfig(Config config){
		spinnerModel.setValue(config.getInterval());
	}
	
	void saveConfig(Config config){
		config.setInterval(spinnerModel.getNumber().intValue());
	}
}
