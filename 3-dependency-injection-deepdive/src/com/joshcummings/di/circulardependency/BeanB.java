package com.joshcummings.di.circulardependency;

public class BeanB {
	private final BeanA beanA;

	public BeanB(BeanA beanA) {
		this.beanA = beanA;
	}
	
	
}
