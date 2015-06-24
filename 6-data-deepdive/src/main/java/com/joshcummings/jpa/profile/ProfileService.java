package com.joshcummings.jpa.profile;

import javax.persistence.EntityManager;

import com.joshcummings.jpa.common.EntityManagerFactory;

public class ProfileService {
	private final EntityManager em;
	
	public ProfileService() {
		/**
		 * We access it statically here. Though this is convenient for ensuring that
		 * everyone has one copy across the JVM, it makes unit testing trickier since
		 * it is impossible to override a static method;
		 * 
		 */
		this(EntityManagerFactory.getInstance());
	}
	
	/**
	 * An important constructor where we can pass a Mock reference to an entity manager
	 * should we want to test the class's business logic in isolation of the database
	 * transactions.
	 * 
	 * @param em
	 */
	public ProfileService(EntityManager em) {
		this.em = em;
	}
	
	public Profile create(String firstName, String lastName, String emailAddress) {
		Profile p = new Profile(firstName, lastName, emailAddress);
		
		/**
		 * We leave the id out of the constructor and out of the code here; we let EntityManager
		 * do that for us.
		 * 
		 */
		em.persist(p);
		return p;
	}
	
	public Profile retreive(Long id) {
		return em.find(Profile.class, id);
	}
	
	/**
	 * May want to protect this instead of giving folks unfettered access to persisting a full Profile object
	 * 
	 * @param updated
	 * @return
	 */
	protected Profile update(Profile updated) {
		em.persist(updated);
		return updated;
	}
	
	/**
	 * We don't need to expose things in such a verbose manner as the following;
	 * however, it does keep us from accepting fully constructed Profile objects
	 * whose migration we may not be on top of.
	 * 
	 * OTOH, the above method {@link ProfileService#update(Profile)} is nice in that
	 * the contract doesn't need to change as the domain evolves.
	 * 
	 * @param id
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public Profile changeName(Long id, String firstName, String lastName) {
		Profile p = retreive(id);
		p.setFirstName(firstName);
		p.setLastName(lastName);
		return update(p);
	}
	
	public Profile changeEmail(Long id, String email) {
		Profile p = retreive(id);
		p.setEmail(email);
		return update(p);
	}
	
	public void delete(Long id) {
		/**
		 * Kind of annoying to have to retreive first, no?
		 * 
		 * We'll see some other ways that are closer to what we'd like to do; however, I have this
		 * here so you can be reminded that only using an ORM tool can cause you to do inefficient
		 * things in the name of adhering to the API.
		 */
		Profile p = retreive(id);
		em.remove(p);
	}
}
