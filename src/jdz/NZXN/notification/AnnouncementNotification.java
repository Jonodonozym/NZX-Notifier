/**
 * Notification.java
 *
 * Created by Jaiden Baker on Jul 1, 2017 2:25:33 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 1, 2017 11:56:11 AM
 */

package jdz.NZXN.notification;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jdz.NZXN.dataStructs.Announcement;
import jdz.NZXN.utils.JHyperlink;

/**
 * Notification class that displays announcements on the screen
 *
 * @author Jaiden Baker
 */
@SuppressWarnings("serial")
public class AnnouncementNotification extends Notification {
  public static final Font mainFont = new Font("Calibri", Font.PLAIN, 12);
  private static int maxAnnouncements = 10;
  private List<Announcement> announcements;

  public AnnouncementNotification(List<Announcement> announcements) {
    super();
    this.announcements = announcements;
    
    // calculating height
    double lines = 5+( announcements.size()>maxAnnouncements?maxAnnouncements+1.5:announcements.size() );
    setMinimumSize(new Dimension(width, (int)(16*lines)));
    
    displayContents();
  }

  protected JPanel getNotificationPanel(){
	  
	  	// initializing content panel
	    JPanel contentPanel = new JPanel();
	    contentPanel.setName(announcements.size()+" New Announcements");
	    contentPanel.setLayout(new GridBagLayout());

	    final GridBagConstraints constraints = new GridBagConstraints();
	    constraints.gridx = 0;
	    constraints.gridy = 0;
	    constraints.weightx = 1.0f;
	    constraints.weighty = 1.0f;
	    constraints.fill = GridBagConstraints.BOTH;
	    constraints.insets = new Insets(0, 12, 12, 12);
	    
	    // creating new container for the announcements to go in
	    final Container labelC = new Container();
	    labelC.setLayout(new BorderLayout(16,0));
	    
	    // columns for the announcements
	    // security, the announcement title and the time respectivley
	    int size = Math.min(maxAnnouncements, announcements.size());
	    final JPanel col1 = new JPanel();
	    final JPanel col2 = new JPanel();
	    final JPanel col3 = new JPanel();
	    col1.setLayout(new GridLayout(size, 1));
	    col2.setLayout(new GridLayout(size, 1));
	    col3.setLayout(new GridLayout(size, 1));

	    // adding each announcement to the columns
	    int i=maxAnnouncements;
	    for (Announcement a: announcements){
	    	if (i-- == 0)
	    		break;
	    	
		    String l2Text = a.notification;
		    if (a.isPriceSensitive)
		    	l2Text = "<html><font color=#DE2700>(P) </font>"+a.notification+"</html>";
		    if (a.isThirdParty)
		    	l2Text = "<html>(3) "+a.notification+"</html>";
		    
		    col1.add(new JHyperlink(a.company, a.companyURL));
		    col2.add(new JHyperlink(l2Text, a.url));
		    col3.add(new JLabel(a.time));
	    }

	    // adding columns to the container, then to the content panel
	    labelC.add(col1,BorderLayout.LINE_START);
	    labelC.add(col2,BorderLayout.CENTER);
	    labelC.add(col3,BorderLayout.LINE_END);

	    contentPanel.add(labelC, constraints);
	    
	    // adds 'and x more announcements' label if there are more than maxAnnouncements
	    if (announcements.size() > maxAnnouncements){
		    constraints.gridy = 2;
		    final JHyperlink l = new JHyperlink("And "+(announcements.size()-maxAnnouncements)+" more...",
		    		"https://www.nzx.com/markets/NZSX/announcements");
		    contentPanel.add(l,constraints);
	    }

	    // finally, launch all global nukes into the sun with the next line
	    return contentPanel;
  }
}