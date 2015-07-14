package com.joshcummings.webapp.profile;

import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class ProfileViewBean {
	private String username;

	// 1. Add here a reference to profile service
	
	public ProfileViewBean() {
		Principal p = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getUserPrincipal();
		username = p.getName();
	}

	@IsLoggedIn(
			orThrow=IllegalArgumentException.class
	)
	public void show() {
		// 2. Add here a lookup to profile service by username
	}
}
