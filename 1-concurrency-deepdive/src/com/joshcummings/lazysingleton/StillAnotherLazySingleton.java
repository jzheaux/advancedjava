package com.joshcummings.lazysingleton;

public enum StillAnotherLazySingleton {
	INSTANCE;

	private String enumsHaveMutableState;
	
	public String getEnumsHaveMutableState() {
		return enumsHaveMutableState;
	}
	
	public void setEnumsHaveMutableState(String enumsHaveMutableState) {
		this.enumsHaveMutableState = enumsHaveMutableState;
	}
}