package com.joshcummings.jpa.profile;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProfileServiceIT {
	private static EntityTransaction t;
	private static ProfileService ps;
	
	@BeforeClass
	public static void setUp() {
		EntityManager em = Persistence.createEntityManagerFactory("testPersistenceUnit").createEntityManager(); 
		t = em.getTransaction();
		ps = new ProfileService(em);
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
	public void testQueryAbsentObject() {
		Profile nothing = ps.retreive(314L);
		Assert.assertNull(nothing);
	}
	
	@Test
	public void testQueryExistingObject() {
		Profile created = ps.create("Jack", "Bower", "jack.bower@24.net");
		Assert.assertNotNull(created);
		
		Profile same = ps.retreive(created.getId());
		Assert.assertEquals(created, same);
	}

	@Test
	public void testUpdateName() {
		Profile created = ps.create("Jack", "Bower", "jack.bower@24.net");
		
		Profile changedName = ps.changeName(created.getId(), "Jill", "Bower");
		
		Assert.assertEquals(created.getEmail(), changedName.getEmail());
		Assert.assertEquals("Jill", changedName.getFirstName());
		Assert.assertEquals("Bower", changedName.getLastName());
	}
	
	@Test
	public void testUpdateEmail() {
		Profile created = ps.create("Jack", "Bower", "jack.bower@24.net");
		
		Profile changedEmail = ps.changeEmail(created.getId(), "jack.bower@48.net");
		
		Assert.assertEquals("jack.bower@48.net", changedEmail.getEmail());
		Assert.assertEquals("Jack", changedEmail.getFirstName());
		Assert.assertEquals("Bower", changedEmail.getLastName());
	}
	
	@Test
	public void testRemove() {
		Profile created = ps.create("Dave", "Thomas", "dave.thomas@wendys.com");
		
		ps.delete(created.getId());
		
		Profile notThere = ps.retreive(created.getId());
		
		Assert.assertNull(notThere);
	}
}
