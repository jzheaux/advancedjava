package com.joshcummings.jpa.university;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.joshcummings.jpa.common.EntityManagerFactory;
import com.joshcummings.jpa.profile.Profile;

public class StudentService {
	private final EntityManager em;
	
	public StudentService() {
		em = EntityManagerFactory.getInstance();
	}
	
	public StudentService(EntityManager em) {
		this.em = em;
	}
	
	public ReadOnlyStudent retreive(Long id) {
		Student s = em.find(Student.class, id);
		return s;
	}
	
	public ReadOnlyStudent addStudent(Profile profile, Double gpa) {
		Student s = new Student(profile, gpa);
		em.persist(s);
		return s;
	}
	
	public List<ReadOnlyStudent> getStudentsWithGpa(Double minGpa, Double maxGpa) {
		TypedQuery<Student> queryForStudents = em.createNamedQuery(Student.STUDENTS_IN_GPA_RANGE, Student.class);
		queryForStudents.setParameter("minGpa", minGpa);
		queryForStudents.setParameter("maxGpa", maxGpa);
		
		return Collections.unmodifiableList(queryForStudents.getResultList());
	}
}
