package com.joshcummings.jpa.university;

import javax.persistence.EntityManager;

import com.joshcummings.jpa.common.EntityManagerFactory;

public class CourseService {
	private final EntityManager em;
	
	public CourseService() {
		em = EntityManagerFactory.getInstance();
	}
	
	public CourseService(EntityManager em) {
		this.em = em;
	}
	
	public ReadOnlyCourse enrollStudentInCourse(Long courseId, Long studentId) {
		Course c = em.find(Course.class, courseId);
		Student s = em.find(Student.class, studentId);
		
		c.addStudent(s);
		
		// this will persist bi-directionally; should be able to add for student from student service
		// and see the change
		em.persist(c);
		
		return c;
	}
	
	public ReadOnlyCourse addCourse(String number, String title) {
		Course c = new Course(number, title);
		em.persist(c);
		return c;
	}
}
