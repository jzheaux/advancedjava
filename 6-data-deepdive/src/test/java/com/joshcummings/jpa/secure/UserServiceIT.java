package com.joshcummings.jpa.secure;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserServiceIT {
	private static EntityManager em;
	private static EntityTransaction t;
	private static UserService us;
	
	@BeforeClass
	public static void setUp() {
		em = Persistence.createEntityManagerFactory("testPersistenceUnit").createEntityManager(); 
		t = em.getTransaction();
		us= new UserService(em);
	}
	
	@Before
	public void openTransaction() {
		t.begin();
	}
	
	@After
	public void rollbackTransation() {
		t.rollback();
	}
	
	@Test
	public void testAddUser() {
		User u = us.addUser("bobs", "yeruncle".toCharArray());
		
		Assert.assertArrayEquals("zfsvodmf".toCharArray(), u.getPassword());
	}
}
