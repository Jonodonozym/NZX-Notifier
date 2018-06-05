
package jdz.NZXN.dataStructs.TradeTables;

public enum SecurityMetric {
	BID_WEIGHTED_VOLUME, ASK_WEIGHTED_VOLUME, BID_VOLUME, ASK_VOLUME, BID_TO_ASK_WEIGHTED_VOLUME, MARKET_VALUE, TRADED_VOLUME;

	public double calculate(TradeTable table) {
		switch (this) {
		case MARKET_VALUE:
			return table.getOverview().getValue();
		case BID_WEIGHTED_VOLUME:
			return getVolume(table, true, true);
		case ASK_WEIGHTED_VOLUME:
			return getVolume(table, false, true);
		case BID_VOLUME:
			return getVolume(table, true, false);
		case ASK_VOLUME:
			return getVolume(table, false, false);
		case BID_TO_ASK_WEIGHTED_VOLUME:
			return getVolume(table, true, true) - getVolume(table, false, true);
		case TRADED_VOLUME:
			return getTradedVolume(table);
		default:
			throw new UnsupportedOperationException("Cannot calculate SecurityMetric " + name());
		}
	}

	private double getVolume(TradeTable table, boolean isBids, boolean isWeighted) {
		double totalWeightedVolume = 0;

		double limit = isBids ? table.getOverview().getBuy() : table.getOverview().getSell();

		for (TradeOffer o : isBids ? table.getBids() : table.getAsks()) {
			double deviation = isWeighted ? Math.abs(limit - o.getPrice()) : 0;
			totalWeightedVolume += o.getVolume() / (deviation + 1);
		}

		return totalWeightedVolume / table.getOverview().getValue();
	}

	private double getTradedVolume(TradeTable table) {
		return 0;
	}
}
