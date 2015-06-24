package com.joshcummings.jpa.secure;

import javax.persistence.EntityManager;

import com.joshcummings.jpa.common.EntityManagerFactory;

public class UserService {
	private final EntityManager em;
	
	public UserService() {
		this(EntityManagerFactory.getInstance());
	}
	
	public UserService(EntityManager em) {
		this.em = em;
	}
	
	public User addUser(String username, char[] password) {
		User user = new User(username, password);
		em.persist(user);
		
		return em.find(User.class, user.getId());
	}
}
