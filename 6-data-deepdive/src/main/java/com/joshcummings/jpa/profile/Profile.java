package com.joshcummings.jpa.profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Profile {
	@Id
	@GeneratedValue
	private Long id;
	
	/**
	 * Bummer! These can't be final because they need to be specified by Hibernate after it calls the
	 * empty constructor. What should we do about thread-safety?
	 */
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@Column
	private String email;
	
	public Profile() {
		// hibernate needs this
	}
	
	/**
	 * The constructor we really want is one where we can specify all the data up
	 * front. Hibernate needs the other though for simplicity when it is taking a
	 * database record and deserializing it into an instance of this class.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 */
	public Profile(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public Long getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	/**
	 * Notice the exclusion of id here. This is because the id isn't part of the object
	 * as much as it is part of the database record. Further, id won't be present until
	 * the object is persisted, so we end up having terrible hashcode performance if we 
	 * have id be the thing in hashCode
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Profile other = (Profile) obj;
		
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}
}
