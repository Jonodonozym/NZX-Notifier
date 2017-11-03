/**
 * ANZWebApi.java
 *
 * Created by Jaiden Baker on Nov 2, 2017 11:01:12 AM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 2, 2017 11:06:12 AM
 */

package jdz.NZXN.webApi.anz;

import java.time.LocalDateTime;

import jdz.NZXN.dataStructs.TradeOverview;
import jdz.NZXN.dataStructs.TradeTable;

public interface ANZWebApi {
	public static final ANZWebApi instance = new ANZWebApiImpl();
	
	/**
	 * Attempts to login
	 * @param username
	 * @param password
	 */
	public void login(String username, String password);
	
	/**
	 * Checks if the WebApi is currently logged
	 * @return true if currently logged in
	 */
	public boolean isLoggedIn();
	
	/**
	 * Fetches the local date and time from the ANZ website
	 * @return
	 */
	public LocalDateTime getDateTime();
	
	/**
	 * Gets the trade table for a given security
	 * @param securityCode the 3-5 letters corresponding to the security
	 * @return
	 */
	public TradeTable getSecTrades(String securityCode);
	
	/**
	 * Gets the trade overview for a given security
	 * @param securityCode the 3-5 letters corresponding to the security
	 * @return
	 */
	public TradeOverview getOverview(String securityCode);

	/**
	 * Gets the current market price for a given security
	 * @param securityCode the 3-5 letters corresponding to the security
	 * @return
	 */
	public float getSecPrice(String securityCode);
	
	/**
	 * Gets the trading balance for the currently logged-in account
	 * @return
	 */
	public float getTradingBalance();
}
