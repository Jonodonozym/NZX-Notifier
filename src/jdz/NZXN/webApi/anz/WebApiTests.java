
package jdz.NZXN.webApi.anz;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Test;

public class WebApiTests {

	@Test
	public void loginValid(){
		System.out.println("======[ loginValid ]======");
		
		resetTimeSinceLastCheck();
		assertFalse(ANZWebApi.instance.isLoggedIn());
		printTimeSinceLastCheck("Checking invalid login");
		ANZWebApi.instance.login("3048390", "Asus23843");
		printTimeSinceLastCheck("Logging in");
		assertTrue(ANZWebApi.instance.isLoggedIn());
		printTimeSinceLastCheck("Checking valid login");
	}
	
	@Test
	public void loginInvalid(){
		System.out.println("======[ loginInvalid ]======");
		
		resetTimeSinceLastCheck();
		assertFalse(ANZWebApi.instance.isLoggedIn());
		printTimeSinceLastCheck("Checking invalid login");
		ANZWebApi.instance.login("a", "a");
		printTimeSinceLastCheck("Logging in");
		assertFalse(ANZWebApi.instance.isLoggedIn());
		printTimeSinceLastCheck("Checking invalid login");
	}
	
	@Test
	public void correctDate() {
		System.out.println("======[ correctDate ]======");
		
		LocalDateTime ANZTime = ANZWebApi.instance.getDateTime();
		LocalDateTime SysTime = LocalDateTime.now();
		
		resetTimeSinceLastCheck();
		System.out.println("ANZ time: "+ANZTime);
		System.out.println("Sys time: "+SysTime);
		printTimeSinceLastCheck("Fetching time");
		
		long differenceMinutes = Duration.between(ANZTime, SysTime).toMinutes();
		assert differenceMinutes < 15: "ANZ and System times do not match, difference of "+differenceMinutes+" minutes";
	}

	
	
	
	
	private static long time = System.currentTimeMillis();
	private static void resetTimeSinceLastCheck(){
		time = System.currentTimeMillis();
	}
	
	private static void printTimeSinceLastCheck(String action){
		System.out.println(action+ " took "+(System.currentTimeMillis()-time)+"ms");
		time = System.currentTimeMillis();
	}
}
