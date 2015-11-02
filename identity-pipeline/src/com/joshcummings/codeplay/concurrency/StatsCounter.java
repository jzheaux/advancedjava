package com.joshcummings.codeplay.concurrency;

public interface StatsCounter {
	void lock();
	void unlock();
	
	void countRecord(Identity identity);
	void countFirstName(Identity identity);
	void countLastName(Identity identity);
	void countAge(Identity identity);
	
	Integer getRecordCount();
}
