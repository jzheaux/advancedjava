package com.joshcummings.jpa.common;

import java.time.Instant;

public interface Auditable {
	void setLastUpdate(Instant i);
	void setCreatedDate(Instant i);
}
