/**
 * ANZWebApiImpl.java
 *
 * Created by Jaiden Baker on Jul 20, 2017 4:20:14 PM
 * Copyright © 2017. All rights reserved.
 * 
 * Last modified on Nov 2, 2017 11:06:12 AM
 */

package jdz.NZXN.webApi.anz;

import java.io.IOException;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jdz.NZXN.dataStructs.TradeOverview;
import jdz.NZXN.dataStructs.TradeTable;
import jdz.NZXN.dataStructs.TradeTableBuilder;
import jdz.NZXN.utils.FileLogger;
import jdz.NZXN.webApi.Websites;
import jdz.NZXN.webApi.nzx.NZXWebApi;

class ANZWebApiImpl implements ANZWebApi{	
	private final DateTimeFormatter lastTradedFormatter = new DateTimeFormatterBuilder().appendPattern("dd/MM HH:mm")
			.parseDefaulting(ChronoField.YEAR, Year.now().getValue())
			.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
			.toFormatter(Locale.ENGLISH);
	
	private final DateTimeFormatter tradeTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	
	private Response loginSession = null;
	
	@Override
	public void login(String username, String password){
		try {
	        Connection con = Jsoup.connect(Websites.ANZloginURL);
	        String __VIEWSTATE = con.get().select("input[name=__VIEWSTATE]").first().attr("value");
	        
	        loginSession = con
	                .data("username", username)
	                .data("password", password)
	                .data("__VIEWSTATE", __VIEWSTATE)
	                .data("LoginStartIn1:ddlStartin","../secure/accounts.aspx?view=bal")
	                .data("btnSignon","Login")
	                .method(Method.POST)
	                .execute();

			setDefaultView(ANZView.DEPTH);
		} catch (IOException e) {
			FileLogger.createErrorLog(e);
		}
	}

	@Override
	public boolean isLoggedIn(){
		if (loginSession != null && !loginSession.url().toString().startsWith(Websites.ANZloginURL)){
			try{
		        Connection con = Jsoup.connect(Websites.ANZprefURL)
				        .cookies(loginSession.cookies());
		        
		        String title = con.get().title();
				
				return !title.startsWith("Login");
			}
			catch (IOException e){ FileLogger.createErrorLog(e); }
		}
		return false;
	}

	@Override
	public LocalDateTime getDateTime(){
		return NZXWebApi.instance.getDateTime();
	}

	@Override
	public float getSecPrice(String securityCode){
		try {
			Document doc = Jsoup.connect(Websites.ANZquoteURL.replace(Websites.ANZquoteToReplace, securityCode)).cookies(loginSession.cookies()).get();
			return Float.parseFloat(doc.select(Websites.ANZquotePriceClass).text());
		}
		catch (Exception e) { FileLogger.createErrorLog(e); }
		return -1;
		
	}

	@Override
	public TradeTable getSecTrades(String securityCode){
		try{
			if (isLoggedIn()){
				Document doc = Jsoup.connect(Websites.ANZquoteURL.replace(Websites.ANZquoteToReplace, securityCode)).cookies(loginSession.cookies()).get();
				
				TradeTableBuilder tradeTableBuilder = new TradeTableBuilder(securityCode, getOverview(securityCode));
				addBids(doc, tradeTableBuilder);
				addAsks(doc, tradeTableBuilder);
				addTradeHistory(doc, tradeTableBuilder);
				
				return tradeTableBuilder.create();
			}
		}
		catch (IOException e){ FileLogger.createErrorLog(e); }
		return null;
	}
	
	public TradeOverview getOverview(String securityCode) {
		try{
			if (isLoggedIn()){
				Document doc = Jsoup.connect(Websites.ANZquoteURL.replace(Websites.ANZquoteToReplace, securityCode)).cookies(loginSession.cookies()).get();
				
				Element overviewTable = doc.select("table[id=SecurityPriceTable]").get(0);

				String[] cells = new String[] {
						overviewTable.select("span[id=quotelast]").text(),
						overviewTable.select("span[id=quoteVWAP]").text(),
						overviewTable.select("span[id=quotebuy]").text(),
						overviewTable.select("span[id=quotesell]").text(),
						overviewTable.select("span[id=quotesell]").text().replaceAll(",", ""),
						overviewTable.select("span[id=quotesell]").text().replaceAll(",", "").substring(1),
						overviewTable.select("span[id=quotelasttradedate]").text()
				};
				
				double[] values = new double[6];
				for (int i=0; i<6; i++)
					values[i] = cells[i].equals(" ")?0:Double.parseDouble(cells[0]);
				
				LocalDateTime time = LocalDateTime.from(lastTradedFormatter.parse(cells[6], new ParsePosition(0)));
				
				return new TradeOverview(values[0], values[1], values[2], values[3], values[4], values[5], time);
			}
		}
		catch (IOException e){ FileLogger.createErrorLog(e); }
		return null;
	}
	
	private void addBids(Document doc, TradeTableBuilder tradeTableBuilder){
		Element bidsTable = doc.select("table[id=biddepth]").first();
		for (Element tr: bidsTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(2).text());
			tradeTableBuilder.addBid(price, volume);
		}
	}
	
	private void addAsks(Document doc, TradeTableBuilder tradeTableBuilder){
		Element asksTable = doc.select("table[id=askdepth]").first();
		for (Element tr: asksTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(2).text());
			tradeTableBuilder.addAsk(price, volume);
		}
	}
	
	private void addTradeHistory(Document doc, TradeTableBuilder tradeTableBuilder){
		Element tradeHistoryTable = doc.select("table[id=tblRecentTrades]").first();
		for (Element tr: tradeHistoryTable.select("tbody").select("tr[class=dgitTR]")){
			Elements td = tr.select("td");
			LocalTime time = LocalTime.parse(td.get(2).text(), tradeTimeFormatter);
			double price = Double.parseDouble(td.get(0).text());
			double volume = Double.parseDouble(td.get(1).text());
			String cond = td.get(3).text();
			tradeTableBuilder.addTrade(price, volume, time, cond);
		}
	}

	@Override
	public float getTradingBalance(){
		try {
			Document doc = Jsoup.connect(Websites.ANZbalanceURL).cookies(loginSession.cookies()).get();
			String text = doc.select(Websites.ANZbalanceClass).select("a").text();
			return Float.parseFloat(text.replaceAll(",", ""));
		}
		catch (IOException e) { FileLogger.createErrorLog(e); }
		return -1;
	}
	
	private String setDefaultView(ANZView view){
		try {
	        Connection con = Jsoup.connect(Websites.ANZprefURL)
			        .cookies(loginSession.cookies());
			Document doc = con.get();
	        String __VIEWSTATE = doc.select("input[name=__VIEWSTATE]").first().attr("value");
	        String DefaultCurrency = doc.select("input[type=radio][checked=checked]").first().attr("value");
	        String previousView = doc.select("select[name=ddlDefaultQuoteView] option[selected]").first().attr("value");

	        con
	        .data("__VIEWSTATE", __VIEWSTATE)
            .data("ddlDefaultQuoteView", view.toString())
            .data("CorporateActionPaymentCurrency", DefaultCurrency)
            .data("btnGo","Save")
            .method(Method.POST)
            .execute();
	        
	        return previousView;
		}
		catch (IOException e) { FileLogger.createErrorLog(e); }
		return null;
	}
}
