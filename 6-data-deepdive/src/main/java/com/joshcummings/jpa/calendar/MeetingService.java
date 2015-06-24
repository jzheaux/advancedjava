package com.joshcummings.jpa.calendar;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import com.joshcummings.jpa.common.EntityManagerFactory;

public class MeetingService {
	private final EntityManager em;
	
	public MeetingService() {
		em = EntityManagerFactory.getInstance();
	}
	
	public MeetingService(EntityManager em) {
		this.em = em;
	}
	
	public Meeting createMeeting(String title, LocalDateTime start, LocalDateTime end) {
		Meeting m = new Meeting(title, start, end);
		em.persist(m);
		return m;
	}
}
