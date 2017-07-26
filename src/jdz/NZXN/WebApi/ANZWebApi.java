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
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.utils.TradeTable;

public class ANZWebApi {
	public static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.appendPattern("d/M hh:mm")
			.parseDefaulting(ChronoField.YEAR, Year.now().getValue())
			.toFormatter();
	
	private static String loginURL = "https://www.anzsecurities.co.nz/DirectTrade/dynamic/signon.aspx";
	private static String homeURL = "https://www.anzsecurities.co.nz/DirectTrade/dynamic/marketsummary.aspx";
	
	private static String prefURL = "https://www.anzsecurities.co.nz/DirectTrade/secure/preferences.aspx";
	
	private static String balanceURL = "https://www.anzsecurities.co.nz/DirectTrade/secure/accounts.aspx?view=bal";
	private static String balanceClass = "td.dgitlastcolumn.catradingbalance";
	
	private static String quoteURL = "https://www.anzsecurities.co.nz/DirectTrade/dynamic/quote.aspx?qqsc=[SEC]&qqe=NZSE";
	private static String quoteToReplace = "[SEC]";
	private static String quotePriceClass = "span#quotelast";
	
	public static Response login(String username, String password){
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

			setDefaultView(res, "depth");
			
	        return res;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static TradeTable getSecTrades(Response loginResponse, String securityCode){
		TradeTable trades = new TradeTable();
		try{
			if (testLogin(loginResponse)){
				Document doc = Jsoup.connect(quoteURL.replace(quoteToReplace, securityCode)).cookies(loginResponse.cookies()).get();
				Element table;
				
				//bids
				table = doc.select("table[id=biddepth]").first();
				for (Element tr: table.select("tbody").select("tr[class=dgitTR]")){
					Elements td = tr.select("td");
					double price = Double.parseDouble(td.get(0).text());
					double volume = Double.parseDouble(td.get(2).text());
					trades.addBid(price, volume);
				}
				
				//asks
				table = doc.select("table[id=askdepth]").first();
				for (Element tr: table.select("tbody").select("tr[class=dgitTR]")){
					Elements td = tr.select("td");
					double price = Double.parseDouble(td.get(0).text());
					double volume = Double.parseDouble(td.get(2).text());
					trades.addAsk(price, volume);
				}
				
				//past trades
				table = doc.select("table[id=tblRecentTrades]").first();
				for (Element tr: table.select("tbody").select("tr[class=dgitTR]")){
					Elements td = tr.select("td");
					String time = td.get(2).text();
					double price = Double.parseDouble(td.get(0).text());
					double volume = Double.parseDouble(td.get(1).text());
					String cond = td.get(3).text();
					trades.addTrade(price, volume, time, cond);
				}
				
				return trades;
			}
		}
		catch (IOException e){ }
		return trades;
	}
	
	public static String getDate(){
		try {
			Document doc = Jsoup.connect(homeURL).get();
			return doc.select("span[id=NZXMarketSummary_lblMarketDate]").text();
		}
		catch (IOException e) { return null; }
	}
	
	public static double getSecPrice(Response loginResponse, String securityCode){
		try {
			Document doc = Jsoup.connect(quoteURL.replace(quoteToReplace, securityCode)).cookies(loginResponse.cookies()).get();
			return Double.parseDouble(doc.select(quotePriceClass).text());
		}
		catch (IOException e) { return -1; }
	}
	
	public static String getTradingBalance(Response loginResponse){
		try {
			Document doc = Jsoup.connect(balanceURL).cookies(loginResponse.cookies()).get();
			return doc.select(balanceClass).select("a").text();
		}
		catch (IOException e) { return null; }
	}
	
	private static boolean testLogin(Response res){
		return !res.url().toString().startsWith(loginURL);
	}
	
	private static String setDefaultView(Response loginResponse, String view){
		try {
	        Connection con = Jsoup.connect(prefURL)
			        .cookies(loginResponse.cookies());
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
}
