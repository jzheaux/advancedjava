package com.joshcummings.di.abstractclasses;


public abstract class AbstractExample {
	private Dependency dependency;
	
	public AbstractExample(Dependency dependency) {
		this.dependency = dependency;
	}
}
