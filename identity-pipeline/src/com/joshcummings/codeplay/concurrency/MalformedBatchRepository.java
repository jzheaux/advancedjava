package com.joshcummings.codeplay.concurrency;

import java.io.InputStream;

public interface MalformedBatchRepository {
	public void addIdentity(Identity identity, String reason);
	
	public void addIdentity(InputStream message, String reason);
}
