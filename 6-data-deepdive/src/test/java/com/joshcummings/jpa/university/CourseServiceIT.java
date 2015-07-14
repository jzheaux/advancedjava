package com.joshcummings.jpa.university;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.joshcummings.jpa.profile.Profile;

public class CourseServiceIT {

	private static EntityManager em;
	private static EntityTransaction t;
	private static CourseService cs;
	private static StudentService ss;
	private Profile p1, p2, p3, p4, p5, p6;
	
	@BeforeClass
	public static void setUp() {
		em = Persistence.createEntityManagerFactory("testPersistenceUnit").createEntityManager(); 
		t = em.getTransaction();
		cs = new CourseService(em);
		ss = new StudentService(em);
	}
	
	@Before
	public void openTransaction() {
		t.begin();
		
		p1 = new Profile("David", "Blaine", "david.blaine@trickster.com");
		em.persist(p1);
		
		p2 = new Profile("Paul", "McCartney", "paul.mccartney@beatles.com");
		em.persist(p2);
		
		p3 = new Profile("Carol", "Burnett", "carol.burnett@annie.com");
		em.persist(p3);
		
		p4 = new Profile("James", "Gosling", "james.gosling@java.com");
		em.persist(p4);
		
		p5 = new Profile("Bill", "Nye", "bill.nye@scienceguy.com");
		em.persist(p5);
		
		p6 = new Profile("Florence", "Nightingale", "florence.nightingale@stats.com");
		em.persist(p6);
	}
	
	@After
	public void rollbackTransation() {
		t.rollback();
	}
	
	@Test
	public void testEnrollStudentInCourse() {
		ReadOnlyCourse c = cs.addCourse("CS3500", "Software Engineering");
		ReadOnlyStudent s = ss.addStudent(p1, 3.5);
		
		int numStudents = c.getStudents().size();
		
		ReadOnlyCourse withStudent = cs.enrollStudentInCourse(c.getId(), s.getId());

		Assert.assertNotNull(withStudent);
		Assert.assertEquals(numStudents + 1, withStudent.getStudents().size());
		
		Assert.assertTrue(withStudent.getStudents().contains(s));
	}
	
	@Test
	public void testGetStudentsInGpaRange() {
		ReadOnlyStudent s1 = ss.addStudent(p1, 3.5);
		ReadOnlyStudent s2 = ss.addStudent(p2, 3.6);
		ReadOnlyStudent s3 = ss.addStudent(p3, 3.2);
		ReadOnlyStudent s4 = ss.addStudent(p4, 2.4);
		ReadOnlyStudent s5 = ss.addStudent(p5, 2.9);
		ReadOnlyStudent s6 = ss.addStudent(p6, 4.0);
		
		List<ReadOnlyStudent> honors = ss.getStudentsWithGpa(3.5, 4.0);
		Assert.assertEquals(3, honors.size());
		Assert.assertTrue(honors.contains(s1));
		Assert.assertTrue(honors.contains(s2));
		Assert.assertTrue(honors.contains(s6));
		Assert.assertFalse(honors.contains(s3));
	}
}
