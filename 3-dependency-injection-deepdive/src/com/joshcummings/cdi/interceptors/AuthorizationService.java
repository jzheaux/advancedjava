package com.joshcummings.cdi.interceptors;

import java.lang.reflect.Method;

public interface AuthorizationService {
	public boolean isCurrentUserAuthorized(Method method);
}
