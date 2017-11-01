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
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.structs.TradeTable;

public class ANZWebApi {
	public static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.appendPattern("d/M hh:mm")
			.parseDefaulting(ChronoField.YEAR, Year.now().getValue())
			.toFormatter();
	
	public static Response login(String username, String password){
		try {
	        Connection con = Jsoup.connect(Websites.ANZloginURL);
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
		TradeTable tradeTable = new TradeTable();
		try{
			if (checkLoggedIn(loginResponse)){
				Document doc = Jsoup.connect(Websites.ANZquoteURL.replace(Websites.ANZquoteToReplace, securityCode)).cookies(loginResponse.cookies()).get();
				
				addBids(doc, tradeTable);
				addAsks(doc, tradeTable);
				addTradeHistory(doc, tradeTable);
				
				return tradeTable;
			}
		}
		catch (IOException e){ }
		return tradeTable;
	}
	
	private static void addBids(Document doc, TradeTable tradeTable){
		Element bidsTable = doc.select("table[id=biddepth]").first();
		for (Element tr: bidsTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(2).text());
			tradeTable.addBid(price, volume);
		}
	}
	
	private static void addAsks(Document doc, TradeTable tradeTable){
		Element asksTable = doc.select("table[id=askdepth]").first();
		for (Element tr: asksTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(2).text());
			tradeTable.addAsk(price, volume);
		}
	}
	
	private static void addTradeHistory(Document doc, TradeTable tradeTable){
		Element tradeHistoryTable = doc.select("table[id=tblRecentTrades]").first();
		for (Element tr: tradeHistoryTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			String time = td.get(2).text();
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(1).text());
			String cond = td.get(3).text();
			tradeTable.addTrade(price, volume, time, cond);
		}
	}
	
	public static String getDate(){
		try {
			Document doc = Jsoup.connect(Websites.ANZhomeURL).get();
			return doc.select("span[id=NZXMarketSummary_lblMarketDate]").text();
		}
		catch (IOException e) { return null; }
	}
	
	public static double getSecPrice(Response loginResponse, String securityCode){
		try {
			Document doc = Jsoup.connect(Websites.ANZquoteURL.replace(Websites.ANZquoteToReplace, securityCode)).cookies(loginResponse.cookies()).get();
			return Double.parseDouble(doc.select(Websites.ANZquotePriceClass).text());
		}
		catch (IOException e) { return -1; }
	}
	
	public static String getTradingBalance(Response loginResponse){
		try {
			Document doc = Jsoup.connect(Websites.ANZbalanceURL).cookies(loginResponse.cookies()).get();
			return doc.select(Websites.ANZbalanceClass).select("a").text();
		}
		catch (IOException e) { return null; }
	}
	
	private static boolean checkLoggedIn(Response res){
		return !res.url().toString().startsWith(Websites.ANZloginURL);
	}
	
	private static String setDefaultView(Response loginResponse, String view){
		try {
	        Connection con = Jsoup.connect(Websites.ANZprefURL)
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
