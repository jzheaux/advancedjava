package com.joshcummings.jpa.common;

import java.time.Instant;

import javax.persistence.PrePersist;

public class AuditEntityListener {
	@PrePersist
	public void addCreateDate(Auditable a) {
		a.setCreatedDate(Instant.now());
		a.setLastUpdate(Instant.now());
	}
	
}
