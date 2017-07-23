/**
 * ANZWebApi.java
 *
 * Created by Jaiden Baker on Jul 20, 2017 4:20:14 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Jul 21, 2017 2:35:38 PM
 */

package jdz.NZXN.WebApi;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ANZWebApi {
	private static String loginURL = "https://www.anzsecurities.co.nz/DirectTrade/dynamic/signon.aspx";
	
	private static String prefURL = "https://www.anzsecurities.co.nz/DirectTrade/secure/preferences.aspx";
	
	private static String balanceURL = "https://www.anzsecurities.co.nz/DirectTrade/secure/accounts.aspx?view=bal";
	private static String balanceClass = "td.dgitlastcolumn.catradingbalance";
	
	private static String quoteURL = "https://www.anzsecurities.co.nz/DirectTrade/dynamic/quote.aspx?qqsc=[SEC]&qqe=NZSE";
	private static String quoteToReplace = "[SEC]";
	private static String quotePriceClass = "span#quotelast";

	public static double[] getSecPrices(String username, String password, String[] secCodes){
		double[] prices = new double[secCodes.length];
		
		Response res = login(username,password);
		if (testLogin(res)){
			Map<String,String> cookies = res.cookies();
			String oldView = setDefaultView(cookies, "depth");
			for (int i=0; i<secCodes.length; i++)
				prices[i] = getSecPrice(cookies,secCodes[i++]);
			setDefaultView(cookies, oldView);
		}
		else
			prices = null;
		return prices;
	}
	
	private static Response login(String username, String password){
		try {
	        Connection con = Jsoup.connect(loginURL);
	        String __VIEWSTATE = con.get().select("input[name=__VIEWSTATE]").first().attr("value");
	        
	        Response res = con
	                .data("username", username)
	                .data("password", password)
	                .data("__VIEWSTATE", __VIEWSTATE)
	                .data("LoginStartIn1:ddlStartin","../secure/accounts.aspx?view=bal")
	                .data("btnSignon","Login")
	                .method(Method.POST)
	                .execute();
	        
	        return res;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String setDefaultView(Map<String,String> cookies, String view){
		try {
	        Connection con = Jsoup.connect(prefURL)
			        .cookies(cookies);
			Document doc = con.get();
	        String __VIEWSTATE = doc.select("input[name=__VIEWSTATE]").first().attr("value");
	        String DefaultCurrency = doc.select("input[type=radio][checked=checked]").first().attr("value");
	        String previousView = doc.select("select[name=ddlDefaultQuoteView] option[selected]").first().attr("value");

	        con
	        .data("__VIEWSTATE", __VIEWSTATE)
            .data("ddlDefaultQuoteView", view)
            .data("CorporateActionPaymentCurrency", DefaultCurrency)
            .data("btnGo","Save")
            .method(Method.POST)
            .execute();
	        
	        return previousView;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static String getTradingBalance(Map<String,String> cookies){
		try {
			Document doc = Jsoup.connect(balanceURL).cookies(cookies).get();
			return doc.select(balanceClass).select("a").text();
		}
		catch (IOException e) { return null; }
	}
	
	private static double getSecPrice(Map<String,String> cookies, String securityCode){
		try {
			Document doc = Jsoup.connect(quoteURL.replace(quoteToReplace, securityCode)).cookies(cookies).get();
			return Double.parseDouble(doc.select(quotePriceClass).text());
		}
		catch (IOException e) { return -1; }
	}
	
	private static boolean testLogin(Response res){
		return !res.url().toString().startsWith(loginURL);
	}
	
	public static boolean checkConnection(){
		try { new URL(loginURL).openConnection().getContent(); return true; }
		catch (IOException e) { return false; }
	}
}
