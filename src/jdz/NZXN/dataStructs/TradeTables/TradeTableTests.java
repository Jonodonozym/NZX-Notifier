
package jdz.NZXN.dataStructs.TradeTables;

import static jdz.NZXN.utils.debugging.ObjectSizeFetcher.sizeOf;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jdz.NZXN.dataStructs.TradeTables.TradeOffer.TradeOfferType;

public class TradeTableTests {

	@Test
	public void sizeOfTable() {
		List<TradeOffer> blankOffers = new ArrayList<TradeOffer>();
		for (int i=0; i<12; i++)
			blankOffers.add(new TradeOffer(1, 1, TradeOfferType.ASK));
		TradeOverview overview = new TradeOverview(1, 1, 1, 1, 1, 1, LocalDateTime.now());
		
		TradeTable table = new TradeTable("ANZ", overview , blankOffers, new ArrayList<TradeOffer>(blankOffers), new ArrayList<TradeOffer>(blankOffers));
		
		logSizeOf(table);
		logSizeOf(TradeTableFactory.toString(table));
	}

	private static void logSizeOf(Object o) {
		System.out.println("Size of "+o.getClass().getName()+": "+sizeOf(o)+" bytes");
	}
}
