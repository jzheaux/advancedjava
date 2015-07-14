package com.joshcummings.cdi.decorators;

import java.util.HashSet;
import java.util.Set;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public abstract class WithAdminToken implements Token {
	@Inject
	@Delegate
	Token token;
	
	/**
	 * Wrap any existing token in a token that has admin privileges
	 */
	@Override
	public Set<String> getAuthorities() {
		Set<String> s = new HashSet<String>(token.getAuthorities());
		s.add("ADMIN");
		return s;
	}
}
