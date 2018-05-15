/**
 * TradeTable.java
 *
 * Created by Jaiden Baker on Jul 25, 2017 1:47:26 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 3, 2017 12:56:05 PM
 */

package jdz.NZXN.dataStructs.TradeTables;

import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import lombok.Getter;


public class TradeTable{
	@Getter private final String securityCode;
	@Getter private final TradeOverview overview;
	private final List<TradeOffer> bids;
	private final List<TradeOffer> asks;
	private final List<TradeOffer> pastTrades;
	@Getter private final LocalDateTime creationTime;
	
	private final Map<SecurityMetric, Double> metrics = new HashMap<SecurityMetric, Double>(SecurityMetric.values().length);
	
	TradeTable(String securityCode, TradeOverview overview, List<TradeOffer> bids, List<TradeOffer> asks, List<TradeOffer> pastTrades, LocalDateTime creationTime) {
		this.securityCode = securityCode;
		this.overview = overview;
		this.bids = bids;
		this.asks = asks;
		this.pastTrades = pastTrades;
		this.creationTime = creationTime;
		
		for (SecurityMetric metric: SecurityMetric.values())
			metrics.put(metric, metric.calculate(this));
	}

	TradeTable(String securityCode, TradeOverview overview, List<TradeOffer> bids, List<TradeOffer> asks, List<TradeOffer> pastTrades) {
		this(securityCode, overview, bids, asks, pastTrades, LocalDateTime.now());
	}
	
	public double getMetric(SecurityMetric metric) {
		return metrics.get(metric);
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
