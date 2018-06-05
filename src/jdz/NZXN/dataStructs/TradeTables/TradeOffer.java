
package jdz.NZXN.dataStructs.TradeTables;

import java.time.LocalTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TradeOffer {

	private final double price, volume;
	private final LocalTime time;
	private final String cond;
	private final TradeOfferType type;

	TradeOffer(double price, double volume, LocalTime time, String cond) {
		this(price, volume, time, cond, TradeOfferType.TRADE);
	}

	TradeOffer(double price, double volume, TradeOfferType type) {
		this(price, volume, LocalTime.now(), "", type);
	}

	enum TradeOfferType {
		BID, ASK, TRADE;
	}
}