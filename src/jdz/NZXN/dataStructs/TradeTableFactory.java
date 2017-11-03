
package jdz.NZXN.dataStructs;

import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jdz.NZXN.dataStructs.TradeOffer.TradeOfferType;
import jdz.NZXN.utils.FileLogger;

public class TradeTableFactory {
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

	public static String toString(TradeTableHistory history) {
		return ""; // TODO
	}
	
	public static TradeTableHistory tradeTableHistoryFromString(TradeTableHistory history) {
		return null; // TODO
	}
	
	public static String toString(TradeTable table) {
		StringWriter sw = new StringWriter();
		sw.append(table.getSecurityCode() + '\n');
		sw.append(table.getCreationTime() + "" + '\n');

		sw.append(tradeOverviewToString(table.getOverview()) + '\n');

		sw.append("BIDS" + '\n');
		for (TradeOffer offer : table.getBids())
			sw.append('\t'+tradeOfferToString(offer) + '\n');

		sw.append("ASKS" + '\n');
		for (TradeOffer offer : table.getAsks())
			sw.append('\t'+tradeOfferToString(offer) + '\n');

		sw.append("TRADES" + '\n');
		for (TradeOffer offer : table.getTrades())
			sw.append('\t'+tradeOfferToString(offer) + '\n');

		sw.append("TABLEEND");

		return sw.toString();
	}

	public static TradeTable fromString(String string) {
		Scanner s = new Scanner(string);

		String securityCode = s.nextLine();
		LocalDateTime creationTime = Instant.ofEpochMilli(Long.parseLong(s.nextLine())).atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		TradeOverview overview = tradeOverviewFromString(s.nextLine());

		List<TradeOffer> bids = new ArrayList<TradeOffer>();
		List<TradeOffer> asks = new ArrayList<TradeOffer>();
		List<TradeOffer> trades = new ArrayList<TradeOffer>();

		s.nextLine();
		String line = s.nextLine();
		while (!line.startsWith("ASKS")) {
			bids.add(tradeOfferFromString(line));
			line = s.nextLine();
		}
		while (!line.startsWith("TRADES")) {
			asks.add(tradeOfferFromString(line));
			line = s.nextLine();
		}
		while (!line.startsWith("TABLEEND")) {
			trades.add(tradeOfferFromString(line));
			line = s.nextLine();
		}

		s.close();
		return new TradeTable(securityCode, overview, bids, asks, trades, creationTime);
	}

	private static String tradeOverviewToString(TradeOverview overview) {
		return overview.getPrice() + ";" + overview.getVwap() + ";" + overview.getBuy() + ";" + overview.getSell() + ";"
				+ overview.getVolume() + ";" + overview.getValue() + ";" + overview.getTime() + ";";
	}

	private static TradeOverview tradeOverviewFromString(String s) {
		try {
			String args[] = s.split(";");
			double price = Double.parseDouble(args[0]);
			double vwap = Double.parseDouble(args[1]);
			double buy = Double.parseDouble(args[2]);
			double sell = Double.parseDouble(args[3]);
			double volume = Double.parseDouble(args[4]);
			double value = Double.parseDouble(args[5]);
			LocalDateTime time = LocalDateTime.parse(args[6]);
			return new TradeOverview(price, vwap, buy, sell, volume, value, time);
		} catch (Exception e) {
			FileLogger.createErrorLog(e);
			return null;
		}
	}

	private static String tradeOfferToString(TradeOffer offer) {
		return offer.getPrice() + ";" + offer.getVolume() + ";" + offer.getTime() + ";" + offer.getCond() + ";"
				+ offer.getType().name();
	}

	private static TradeOffer tradeOfferFromString(String s) {
		try {
			String args[] = s.split(";");
			double price = Double.parseDouble(args[0]);
			double volume = Double.parseDouble(args[1]);
			TradeOfferType type = TradeOfferType.valueOf(args[4]);
			LocalTime time = LocalTime.parse(args[3], dtf);
			return new TradeOffer(price, volume, time, args[4], type);
		} catch (Exception e) {
			FileLogger.createErrorLog(e);
			return null;
		}
	}
}
