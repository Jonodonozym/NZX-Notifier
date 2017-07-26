/**
 * TradeTable.java
 *
 * Created by Jaiden Baker on Jul 25, 2017 1:47:26 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Jul 26, 2017 12:06:40 PM
 */

package jdz.NZXN.utils;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class TradeTable{
	private ArrayList<TradeOffer> bids = new ArrayList<TradeOffer>();
	private ArrayList<TradeOffer> asks = new ArrayList<TradeOffer>();
	private ArrayList<Trade> pastTrades = new ArrayList<Trade>();
	
	public void addBid(double price, double volume){
		bids.add(new TradeOffer(price, volume));
	}
	
	public void addAsk(double price, double volume){
		asks.add(new TradeOffer(price, volume));
	}
	
	public void addTrade(double price, double volume, String time, String cond){
		pastTrades.add(new Trade(price, volume, time, cond));
	}

	public JPanel toJPanel(){
		return toJPanel(Integer.MAX_VALUE);
	}
	
	public JPanel toJPanel(int maxRows){
		JPanel table = new JPanel();
		table.setLayout(new GridLayout(1, 3));
		
		String[] titles = new String[]{ "Bid", "Ask" , "Past Trades"};
		List<List<? extends DataField>> offers = Arrays.asList(bids, asks, pastTrades);
		@SuppressWarnings("rawtypes")
		Class[] classes = new Class[]{TradeOffer.class, TradeOffer.class, Trade.class};
		for (int i=0; i<offers.size(); i++){
			JPanel column = new JPanel();
			column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
			column.add(new JLabel(titles[i]));
				
			int numCols = classes[i].getFields().length;
			JPanel[] cols = new JPanel[numCols];
			for (int j=0; j<numCols; j++){
				cols[j].setLayout(new BoxLayout(cols[j], BoxLayout.Y_AXIS));
				cols[j].add(new JLabel(classes[i].getFields()[j].getName()));
				cols[j].add(new JSeparator(SwingConstants.HORIZONTAL));
			}
			for (int j=0; j<maxRows; j++){
				if (offers.get(i).size() <= j) break;
				DataField offer = offers.get(i).get(j);
				for (int k=0; k<numCols; k++)
					cols[i].add(new JLabel(offer.getField(i)));
			}
			
			table.add(column);
		}
		
		return table;
	}
	
	private interface DataField{
		public String getField(int i);
	}
	
	private class TradeOffer implements DataField{
		public final double price, volume;
		public TradeOffer(double price, double volume){
			this.price = price;
			this.volume = volume;
		}
		public String getField(int i) {
			return i==0?"$"+price:volume+"";
		}
	}
	
	private class Trade implements DataField{
		public final double price, volume;
		public final String time, cond;
		public Trade(double price, double volume, String time, String cond){
			this.price = price;
			this.volume = volume;
			this.time = time;
			this.cond = cond;
		}
		public String getField(int i) {
			switch(i){
			case 0: return "$"+price;
			case 1:	return volume+"";
			case 2: return time;
			default: return cond;
			}
		}
	}
}
