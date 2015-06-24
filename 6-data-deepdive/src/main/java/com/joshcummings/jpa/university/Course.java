package com.joshcummings.jpa.university;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name=Course.WITH_STUDENTS, query="FROM Course c left join fetch c.students")
})
public class Course implements ReadOnlyCourse {
	public static final String WITH_STUDENTS = "withStudents";
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	private String number;
	
	@Column
	private String title;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(joinColumns = @JoinColumn(name="course_id"),
			inverseJoinColumns = @JoinColumn(name="student_id"))
	private Set<Student> students = new HashSet<>();

	protected Course() {}
	
	public Course(String number, String title) {
		this.number = number;
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyCourse#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyCourse#getNumber()
	 */
	@Override
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyCourse#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see com.joshcummings.jpa.university.ReadOnlyCourse#getStudents()
	 */
	@Override
	public Set<ReadOnlyStudent> getStudents() {
		return Collections.unmodifiableSet(students);
	}

	public void addStudent(Student student) {
		students.add(student);
		student.addCourse(this);
	}
	
	public void removeStudent(Student student) {
		students.remove(student);
		student.removeCourse(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Course other = (Course) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
