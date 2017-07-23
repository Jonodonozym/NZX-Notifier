
package jdz.NZXN.utils;

public class ComparePrice {
	public static boolean checkPrice(double p1, double p2, String operator) {
		switch (operator) {
		case ">":
			return p2 > p1;
		case ">=":
			return p2 >= p1;
		case "<":
			return p2 < p1;
		case "<=":
			return p2 <= p1;
		case "=":
			return p2 == p1;
		case "Any change":
			return p2 != p1;
		default:
			return false;
		}
	}
}
