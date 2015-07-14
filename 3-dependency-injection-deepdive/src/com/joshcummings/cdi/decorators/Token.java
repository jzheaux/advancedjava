package com.joshcummings.cdi.decorators;

import java.util.Set;

public interface Token {
	public Set<String> getAuthorities();
	public String getRealm();
}
