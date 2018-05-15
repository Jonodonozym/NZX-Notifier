
package jdz.NZXN.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access  = AccessLevel.PACKAGE)
public class LoginDetails{
	private final String username;
	private final String password;
	
	static LoginDetails getEmpty() {
		return new LoginDetails("","");
	}
	
	public boolean isEmpty() {
		return username.equals("") || password.equals("");
	}
}