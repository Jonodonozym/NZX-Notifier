
package jdz.NZXN.checker;

import lombok.Data;

@Data
public class PriceChangeEvent {
	private final String security;
	private final double price;
	private final double oldPrice;
}
