
package jdz.NZXN.dataStructs.TradeTables;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jdz.NZXN.dataStructs.TradeTables.TradeOffer.TradeOfferType;

public class TradeTableBuilder {
	private final String securityCode;
	private final TradeOverview overview;
	private final List<TradeOffer> bids = new ArrayList<TradeOffer>();
	private final List<TradeOffer> asks = new ArrayList<TradeOffer>();
	private final List<TradeOffer> pastTrades = new ArrayList<TradeOffer>();
	private LocalDateTime creationTime = null;

	public TradeTableBuilder(String securityCode, TradeOverview overview) {
		this.overview = overview;
		this.securityCode = securityCode;
	}
	
	public TradeTableBuilder withCreationTime(LocalDateTime time) {
		creationTime = time;
		return this;
	}
	
	public TradeTableBuilder addBid(double price, double volume){
		bids.add(new TradeOffer(price, volume, TradeOfferType.BID));
		return this;
	}
	
	public TradeTableBuilder addAsk(double price, double volume){
		asks.add(new TradeOffer(price, volume, TradeOfferType.ASK));
		return this;
	}
	
	public TradeTableBuilder addTrade(double price, double volume, LocalTime time, String cond){
		pastTrades.add(new TradeOffer(price, volume, time, cond));
		return this;
	}
	
	public TradeTable create() {
		if (creationTime == null)
			return new TradeTable(securityCode, overview, bids, asks, pastTrades);
		return new TradeTable(securityCode, overview, bids, asks, pastTrades, creationTime);
	}
}
