package com.joshcummings.security.untrusted;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class Problem {
	private Long id;
	
	private String title;
	
	private String description;
	
	private String name;

	private LocalDateTime startDate;

	private LocalDateTime endDate;
	
	private final Set<Test> tests = new HashSet<Test>();
	
	private final Set<Submission> submissions = new HashSet<Submission>();
		
	public Problem() {}
	
	public Problem(String name, String title, String description) {
		this.name = name;
		this.title = title;
		this.description = description;
		startDate = LocalDateTime.now();
		endDate = LocalDateTime.now().plusYears(23);
	}
	
	public Problem(String name, String title, InputStream file) throws IOException {
		this.name = name;
		this.title = title;
		byte[] description = IOUtils.toByteArray(file);
		this.description = new String(description);
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	public LocalDateTime getEndDate() {
		return endDate;
	}
	
	public void addTest(Test test) {
		tests.add(test);
		test.setProblem(this);
	}
	
	public Set<Test> getTests() {
		return Collections.unmodifiableSet(tests);
	}
	
	public Set<Test> getExpectations() {
		Set<Test> expectations = new HashSet<Test>();
		for ( Test test : getTests() ) {
			if ( test.isPublic() ) {
				expectations.add(test);
			}
		}
		
		return expectations;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public Test getTest(Long testId) {
		for ( Test test : tests ) {
			if ( testId.equals(test.getId()) ) {
				return test;
			}
		}
		return null;
	}
	
	public void removeTest(Long testId) {
		Test test = getTest(testId);
		tests.remove(test);
	}
	
	public Set<Submission> getSubmissions() {
		return Collections.unmodifiableSet(submissions);
	}
	
	public void addSubmission(Submission s) {
		submissions.add(s);
	}
}