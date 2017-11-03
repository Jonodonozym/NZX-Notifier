/**
 * TradeTable.java
 *
 * Created by Jaiden Baker on Jul 25, 2017 1:47:26 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 3, 2017 12:56:05 PM
 */

package jdz.NZXN.dataStructs;

import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import lombok.Getter;

@Getter
public class TradeTable{
	private final String securityCode;
	private final TradeOverview overview;
	private final List<TradeOffer> bids;
	private final List<TradeOffer> asks;
	private final List<TradeOffer> pastTrades;
	private final LocalDateTime creationTime;
	
	// Mathematical constants
	private final double bidWeightedVolume;
	private final double askWeightedVolume;
	private final double bidAskWeightedVolumeRatio;
	
	TradeTable(String securityCode, TradeOverview overview, List<TradeOffer> bids, List<TradeOffer> asks, List<TradeOffer> pastTrades, LocalDateTime creationTime) {
		this.securityCode = securityCode;
		this.overview = overview;
		this.bids = bids;
		this.asks = asks;
		this.pastTrades = pastTrades;
		this.creationTime = creationTime;
		
		bidWeightedVolume = calculateWeightedVolume(true);
		askWeightedVolume = calculateWeightedVolume(false);
		this.bidAskWeightedVolumeRatio = askWeightedVolume - bidWeightedVolume;
	}

	TradeTable(String securityCode, TradeOverview overview, List<TradeOffer> bids, List<TradeOffer> asks, List<TradeOffer> pastTrades) {
		this(securityCode, overview, bids, asks, pastTrades, LocalDateTime.now());
	}
	
	private double calculateWeightedVolume(boolean b) {
		double totalWeightedVolume = 0;
		
		double limit = b?overview.getBuy():overview.getSell();
		
		for(TradeOffer o: b?bids:asks) {
			double deviation = Math.abs(limit-o.getPrice());
			totalWeightedVolume += o.getVolume() / deviation+1;
		}
		
		return totalWeightedVolume / overview.getValue();
	}
	
	public List<TradeOffer> getTrades() {
		return Collections.unmodifiableList(pastTrades);
	}
	
	public List<TradeOffer> getAsks(){
		return Collections.unmodifiableList(asks);
	}
	
	public List<TradeOffer> getBids(){
		return Collections.unmodifiableList(bids);
	}

	public JPanel toJPanel(){
		return toJPanel(Integer.MAX_VALUE);
	}
	
	public JPanel toJPanel(int maxRows){
		// TODO include TradeOverview
		JPanel table = new JPanel();
		table.setLayout(new GridLayout(1, 3));
		
		String[] titles = new String[]{ "Bid", "Ask" , "Past Trades"};
		String[] subColumnNames = new String[] {"Price", "Volume"};
		
		List<List<TradeOffer>> tradeColumns = Arrays.asList(bids, asks, pastTrades);
		
		// for each trade type in bid ask and pastTrades
		for (int i=0; i<tradeColumns.size(); i++){
			JPanel column = new JPanel();
			column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
			column.add(new JLabel(titles[i]));
			
			// create cub-columns for price and volume
			JPanel[] subColumns = new JPanel[2];
			for (int j=0; j<subColumns.length; j++){
				subColumns[j].setLayout(new BoxLayout(subColumns[j], BoxLayout.Y_AXIS));
				subColumns[j].add(new JLabel(subColumnNames[j]));
				subColumns[j].add(new JSeparator(SwingConstants.HORIZONTAL));
			}
			
			for (int j=0; j<maxRows; j++){
				if (tradeColumns.get(i).size() <= j) break;
				TradeOffer offer = tradeColumns.get(i).get(j);
				subColumns[0].add(new JLabel(""+offer.getPrice()));
				subColumns[1].add(new JLabel(""+offer.getVolume()));
			}
			
			table.add(column);
		}
		
		return table;
	}
}
