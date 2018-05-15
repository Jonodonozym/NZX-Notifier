package jdz.NZXN.webApi.nzx;

import static org.junit.Assert.*;

import org.junit.Test;

public class NZXWebApiTests {

	@Test
	public void testValidSecurity(){
		long time = System.currentTimeMillis();
		for (int i=0; i<5; i++) {
			assertTrue(NZXWebApi.instance.isValidSecutiry("ATM"));
			assertFalse(NZXWebApi.instance.isValidSecutiry("SDGHOS"));
		}
		System.out.println("Time to check validity of 4 securities: "+(System.currentTimeMillis() - time)+"ms");
	}
}
