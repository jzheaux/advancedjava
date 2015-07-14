package com.joshcummings.security.untrusted;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubmissionResult {
	private Long id;

	private Test test;

	private String actualOutput;

	private long actualTime;
	
	private List<String> messages;
	
	public SubmissionResult(Submission submission, Test test, String actual, long actualTime) {
		this(submission, test, actual, actualTime, new ArrayList<String>());
	}

	public SubmissionResult(Submission submission, Test test, List<String> errorMessages) {
		this(submission, test, null, 0L, errorMessages);
	}
	
	private SubmissionResult(Submission submission, Test test, String actual, long actualTime, List<String> errorMessages) {
		this.actualOutput = actual;
		this.actualTime = actualTime;
		this.messages = errorMessages;
		this.test = test;
	}

	public String getExpectedOutput() {
		return test.getExpected();
	}

	public String getActualOutput() {
		return actualOutput;
	}

	public long getMaxTime() {
		return test.getMaxTime();
	}

	public long getActualTime() {
		return actualTime;
	}

	public boolean isPassed() {
		return messages.isEmpty() && outputsMatch() && doneInTime();
	}
	
	private boolean outputsMatch() {
		return getExpectedOutput().equals(actualOutput);
	}
	
	private boolean doneInTime() {
		return actualTime <= getMaxTime();
	}
	
	public void addErrors(List<String> errors) {
		messages.addAll(errors);
	}
	
	public Long getTestId() {
		return test.getId();
	}
	
	public List<String> getMessages() {
		if ( messages.isEmpty() ) {
			if ( !outputsMatch() ) {
				messages.add("Expected [" + getExpectedOutput() + "] but got [" + actualOutput + "]");
			}
			if ( !doneInTime() ) {
				messages.add("Wasn't finished with the required " + getMaxTime() + " milliseconds.");
			}
		}
		return Collections.unmodifiableList(messages);
	}

	public boolean isPublic() {
		return test.isPublic();
	}
}
