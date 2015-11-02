package com.joshcummings.codeplay.concurrency;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BadIdentity implements Identity {

	public Integer getId() {
		return 0;
	}
	
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhoneNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmailAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Integer getAge() {
		return 0;
	}

	@Override
	public List<Address> getAddresses() {
		// TODO Auto-generated method stub
		return Collections.EMPTY_LIST;
	}

	@Override
	public ReentrantLock getLock() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
