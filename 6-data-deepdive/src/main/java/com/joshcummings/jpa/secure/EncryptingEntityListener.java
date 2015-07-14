package com.joshcummings.jpa.secure;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

public class EncryptingEntityListener {
	@PrePersist
	public void encryptUser(User u) {
		char[] password = u.getPassword();
		for ( int i = 0; i < password.length; i++ ) {
			password[i] = (char)(password[i] + 1);
		}
	}
	
	@PostLoad
	public void decryptUser(User u) {
		char[] password = u.getPassword();
		for ( int i = 0; i < password.length; i++ ) {
			password[i] = (char)(password[i] - 1);
		}
	}
}
