package com.joshcummings.webapp.profile;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


@Named
@RequestScoped
public class LoginBean {
  private String username;
  private char[] password;

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public char[] getPassword() {
    return this.password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }

  public String login () {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) 
        context.getExternalContext().getRequest();
    try {
      request.login(this.username, new String(this.password));
    } catch (ServletException e) {
    	e.printStackTrace();
      context.addMessage(null, new FacesMessage("Username or password is invalid."));
      return null;// "pretty:error";
    }
    return "pretty:view-account";
  }

  public void logout() {
    FacesContext context = FacesContext.getCurrentInstance();
    HttpServletRequest request = (HttpServletRequest) 
        context.getExternalContext().getRequest();
    try {
      request.logout();
    } catch (ServletException e) {
      context.addMessage(null, new FacesMessage("Logout failed."));
    }
  }
}