package com.joshcummings.codeplay.concurrency;


public interface StatsLedger {
	void recordEntry(StatsEntry entry);
	
	Integer getRecordCount();
	
	public class StatsEntry {
		private final String firstName;
		private final String lastName;
		private final Integer age;
		
		public StatsEntry(Identity identity) {
			String[] parts = identity.getName().split(" ");
			this.firstName = parts[0];
			this.lastName = parts[1];
			this.age = identity.getAge();
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public Integer getAge() {
			return age;
		}
	}
}
