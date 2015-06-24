package com.joshcummings.jpa.university;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import com.joshcummings.jpa.profile.Profile;

@Entity
@NamedQueries({
	@NamedQuery(name=Student.STUDENTS_IN_GPA_RANGE, query="FROM Student WHERE gpa >= :minGpa AND gpa <= :maxGpa")
})
public class Student implements ReadOnlyStudent {
	public static final String STUDENTS_IN_GPA_RANGE = "studentsInGpaRange";
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	@JoinColumn(name="PROFILE_ID")
	private Profile profile;
	
	@Column
	private Double gpa;

	@ManyToMany(mappedBy="students")
	private Set<Course> courses = new HashSet<>();
	
	public Student(Profile profile, Double gpa) {
		this.profile = profile;
		this.gpa = gpa;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyStudent#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyStudent#getProfile()
	 */
	@Override
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyStudent#getGpa()
	 */
	@Override
	public Double getGpa() {
		return gpa;
	}

	public void setGpa(Double gpa) {
		this.gpa = gpa;
	}
	
	public Set<ReadOnlyCourse> getCourses() {
		return Collections.unmodifiableSet(courses);
	}
	
	public void addCourse(Course course) {
		courses.add(course);
	}
	
	public void removeCourse(Course course) {
		courses.remove(course);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gpa == null) ? 0 : gpa.hashCode());
		result = prime * result + ((profile == null) ? 0 : profile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (gpa == null) {
			if (other.gpa != null)
				return false;
		} else if (!gpa.equals(other.gpa))
			return false;
		if (profile == null) {
			if (other.profile != null)
				return false;
		} else if (!profile.equals(other.profile))
			return false;
		return true;
	}
}
