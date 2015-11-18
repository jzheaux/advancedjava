package com.joshcummings.codeplay.concurrency;

import java.io.OutputStream;

public interface StatsCounter {
	void countRecord(Identity identity);
	void countFirstName(Identity identity);
	void countLastName(Identity identity);
	void countAge(Identity identity);
	
	void writeStats(OutputStream os);
}
