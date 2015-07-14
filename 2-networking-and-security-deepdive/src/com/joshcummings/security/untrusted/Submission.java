package com.joshcummings.security.untrusted;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Submission {
	private Long id;
	
	private String location;
	
	private String code;
	
	private boolean passed;

	private Language language;
	
	private LocalDateTime date;
	
	private Problem problem;

	private Set<SubmissionResult> tests = new HashSet<SubmissionResult>();

	private List<String> messages = new ArrayList<String>();
	
	public Submission() {}
	
	public Submission(String location, String code, 
			Language language, Problem problem) {
		this.location = location;
		this.language = language;
		this.problem = problem;
		this.date = LocalDateTime.now();
		this.code = code;
	}
	
	public Submission(Long id, String location, String code,
			Language language, Problem problem) {
		this(location, code, language, problem);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}


	public Language getLanguage() {
		return language;
	}

	public Problem getProblem() {
		return problem;
	}
	
	public LocalDateTime getDate() {
		return date;
	}
	
	public String getCode() {
		if ( code == null && location != null ) {
			try {
				byte[] b = Files.readAllBytes(Paths.get(new File(location).getAbsolutePath()));
				code = new String(b);
			} catch (IOException e) {
				// this is a nice-to-have UI help for the user, so we don't want to blow up if it fails
				e.printStackTrace();
			}
		}
		return code;
	}
	
	public Set<SubmissionResult> getResults() {
		return Collections.unmodifiableSet(tests);
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public boolean updatePassed() {
		for ( SubmissionResult result : tests ) {
			if ( !result.isPassed() ) {
				return false;
			}
		}
		return true;
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	public void addErrors(List<String> errorMessages) {
		messages.addAll(errorMessages);
	}
	
	public void addResult(Test test, String actual, long time) {
		tests.add(new SubmissionResult(this, test, actual, time));
		this.passed = updatePassed();
	}
	
	public void addResult(Test test, List<String> errorMessages) {
		tests.add(new SubmissionResult(this, test, errorMessages));
		this.passed = updatePassed();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Submission ) {
			Submission that = (Submission)obj;
			return that.id == null ? super.equals(obj) : that.id.equals(this.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id == null ? super.hashCode() : this.id.hashCode();
	}
}
