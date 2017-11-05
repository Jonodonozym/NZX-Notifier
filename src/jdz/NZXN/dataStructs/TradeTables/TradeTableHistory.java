
package jdz.NZXN.dataStructs.TradeTables;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jdz.NZXN.utils.debugging.FileLogger;
import lombok.Getter;

public class TradeTableHistory {
	@Getter private final String securityCode;
	@Getter private final LocalDate date = LocalDate.now();
	private List<TradeTable> pastTables = new ArrayList<TradeTable>();
	private List<TradeOffer> pastTrades = new ArrayList<TradeOffer>();
	
	public TradeTableHistory(String securityCode) {
		this.securityCode = securityCode;
	}
	
	public void add(TradeTable table) {
		if (table.getSecurityCode().equalsIgnoreCase(securityCode)) {
			LocalTime lastTrade = pastTrades.get(pastTrades.size()-1).getTime();
			
			for (TradeOffer offer: table.getTrades())
				if (offer.getTime().isAfter(lastTrade))
					pastTrades.add(offer);
				else
					break;
			
			pastTables.add(table);
		}
		else
			FileLogger.createErrorLog(new IllegalArgumentException("TradeTable code "+table.getSecurityCode()+" Doesn't equal history code "+securityCode));
	}
	
	public List<TradeTable> getTables(){
		return Collections.unmodifiableList(pastTables);
	}
	
	public List<TradeOffer> getTrades(){
		return Collections.unmodifiableList(pastTrades);
	}
}
