
package jdz.NZXN.webApi.anz;

public enum ANZView {
	QUOTE, DETAILED, DEPTH, CHARTS, NEWS, REUTERS;

	@Override
	public String toString() {
		return this.name().charAt(0) + this.name().substring(1).toLowerCase();
	}
}
