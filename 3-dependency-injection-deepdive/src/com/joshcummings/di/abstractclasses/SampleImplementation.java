package com.joshcummings.di.abstractclasses;


public class SampleImplementation extends AbstractExample {
	private OtherDependency otherDependency;
	
	public SampleImplementation(Dependency dependency, OtherDependency otherDependency) {
		super(dependency);
	}

}
