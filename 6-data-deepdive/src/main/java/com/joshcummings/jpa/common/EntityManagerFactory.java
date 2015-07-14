package com.joshcummings.jpa.common;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * In these examples, we aren't using Dependency Injection. Because of this,
 * we need to do our own singleton.
 * 
 * @author jzheaux
 *
 */
public class EntityManagerFactory {
	private static class EntityManagerHolder {
		public static final EntityManager em = Persistence.createEntityManagerFactory("persistenceUnit").createEntityManager();
	}
	
	public static EntityManager getInstance() {
		return EntityManagerHolder.em;
	}
}
