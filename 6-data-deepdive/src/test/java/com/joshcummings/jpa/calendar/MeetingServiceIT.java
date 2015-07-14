package com.joshcummings.jpa.calendar;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeetingServiceIT {
	private static EntityManager em;
	private static EntityTransaction t;
	private static MeetingService ms;
	
	@BeforeClass
	public static void setUp() {
		em = Persistence.createEntityManagerFactory("testPersistenceUnit").createEntityManager(); 
		t = em.getTransaction();
		ms = new MeetingService(em);
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
	public void testAudit() {
		Meeting m = ms.createMeeting("Java Course in Room 206", LocalDateTime.of(2015, 6, 15, 7, 0), LocalDateTime.of(2015, 6, 25, 11, 0));
		
		Assert.assertNotNull(m.getCreatedDate());
		Assert.assertNotNull(m.getLastUpdate());
	}
}
