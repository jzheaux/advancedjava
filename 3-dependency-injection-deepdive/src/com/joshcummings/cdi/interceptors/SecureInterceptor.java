package com.joshcummings.cdi.interceptors;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Secure
@Interceptor
public class SecureInterceptor implements Serializable {

	@Inject AuthorizationService authorizationService;
	
    public SecureInterceptor() {
    }

    /**
     * This method will be called any time the @Secure annotation appears on a method in
     * a CDI-managed bean.
     * 
     * @param invocationContext
     * @return
     * @throws Exception
     */
    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext)
            throws Exception {
        if ( authorizationService.isCurrentUserAuthorized(invocationContext.getMethod()) ) {
        	return invocationContext.proceed();
        } else {
        	throw new IllegalArgumentException("User not authorized to perform operation.");
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
