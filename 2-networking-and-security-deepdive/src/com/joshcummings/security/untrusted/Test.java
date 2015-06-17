package com.joshcummings.security.untrusted;



public class Test {
	private Long id;
	
	private Problem problem;
	
	private String name;
	
	private String input;
	
	private String expected;
	
	private long maxTime;

	private boolean isPublic;
	
	public Test() {}
	
	public Test(Problem problem, String name, String input, String expected, long maxTime, boolean isPublic) {
		this.problem = problem;
		this.name = name;
		this.input = input;
		this.expected = expected;
		this.maxTime = maxTime;
		this.isPublic = isPublic;
	}

	public Long getId() {
		return id;
	}
	
	public Problem getProblem() {
		return problem;
	}

	public String getName() {
		return name;
	}

	public String getInput() {
		return input;
	}

	public String getExpected() {
		return expected;
	}

	public long getMaxTime() {
		return maxTime;
	}
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
}
